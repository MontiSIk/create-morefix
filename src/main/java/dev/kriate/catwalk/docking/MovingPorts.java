package dev.kriate.catwalk.docking;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.*;
import dev.ryanhcode.sable.api.physics.constraint.*;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import java.util.*;

/** Keeps port contents alive and updates a Sable joint anchor on each physics substep. */
public final class MovingPorts implements MovementBehaviour {
    private static final Map<DockingConnectorBlockEntity, Transit> PORTS = new java.util.concurrent.ConcurrentHashMap<>();
    public static void register() {
        MovementBehaviour.REGISTRY.register(SimBlocks.DOCKING_CONNECTOR.get(),new MovingPorts());
        com.simibubi.create.api.contraption.BlockMovementChecks.registerMovementAllowedCheck((state,world,pos)->
            state.is(SimBlocks.DOCKING_CONNECTOR.get())||state.is(SimBlocks.PAIRED_DOCKING_CONNECTOR.get())
                ?com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.SUCCESS:com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.PASS);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppedEvent e)->PORTS.clear());
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.tick.ServerTickEvent.Post e)->{
            for(var t:new ArrayList<>(PORTS.values())){
                var entity=t.context.contraption.entity;
                if(entity!=null&&entity.isRemoved()&&entity.getRemovalReason()!=null&&entity.getRemovalReason().shouldDestroy()){
                    t.release();PORTS.remove(t.port);t.port.remove();
                }
            }
        });
        com.simibubi.create.api.contraption.BlockMovementChecks.registerAttachedCheck((state,world,pos,direction)->
            state.is(SimBlocks.PAIRED_DOCKING_CONNECTOR.get())&&direction==state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)
                ?com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.SUCCESS:com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.PASS);
    }
    public static boolean moving(Object port){return PORTS.containsKey(port);}
    public static boolean bound(Object port){return PORTS.containsKey(port)||PORTS.values().stream().anyMatch(t->t.other==port);}
    public static UUID owner(Object peer){for(var t:PORTS.values())if(t.other==peer&&t.context.contraption.entity!=null)return t.context.contraption.entity.getUUID();return null;}
    public static void writeRenderBinding(Object peer,CompoundTag tag){
        for(var t:PORTS.values())if(t.other==peer&&t.joint!=null&&t.peerAnchor!=null
            &&t.context.contraption.entity!=null){
            var data=new CompoundTag();data.putUUID("Owner",t.context.contraption.entity.getUUID());
            data.putUUID("SubLevel",t.targetBody.getUniqueId());
            for(int i=0;i<3;i++){data.putDouble("Local"+i,t.localAnchor.get(i));data.putDouble("Peer"+i,t.peerAnchor.get(i));}
            data.putDouble("OrientationX",t.localOrientation.x);data.putDouble("OrientationY",t.localOrientation.y);
            data.putDouble("OrientationZ",t.localOrientation.z);data.putDouble("OrientationW",t.localOrientation.w);
            tag.put("MoreFixMovingRender",data);return;
        }
    }
    public static BlockEntity resolve(Level level,BlockPos pos){
        for(var t:PORTS.values())if(t.context.world==level&&t.port.getBlockPos().equals(pos))return t.port;
        return level.getBlockEntity(pos);
    }
    public static void capture(Contraption contraption,Level level,BlockPos offset){
        if(level.isClientSide)return;
        for(var actor:contraption.getActors()){
            var ctx=actor.right;
            if(ctx==null||!(actor.left.state().getBlock() instanceof dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlock))continue;
            var be=level.getBlockEntity(actor.left.pos().offset(contraption.anchor).offset(offset));
            if(be instanceof DockingConnectorBlockEntity port){
                var other=DockingAssembly.prepare(port,contraption,offset);
                var transit=new Transit(ctx,port,other);
                PORTS.put(port,transit);ctx.temporaryData=transit;
            }
        }
    }
    @Override public boolean mustTickWhileDisabled(){return true;}
    @Override public void tick(MovementContext ctx){
        if(ctx.world.isClientSide)return;
        var t=transit(ctx);if(t==null)return;
        t.port.clearRemoved();
        if(t.other!=null&&(!live(t.other)||!t.other.powered||!t.port.powered||!t.other.hasOtherConnector())){t.release();}
        if(t.other!=null&&t.joint==null&&ctx.contraption.entity!=null){t.attach();}
        t.step(false);
        if(t.other!=null){
            ((PortAccess)t.other).morefix$clearWaiting();
            ((PortAccess)t.port).morefix$locked();((PortAccess)t.other).morefix$locked();
            t.port.tank.connect(t.other.getBlockPos(),t.other.tank);t.other.tank.connect(t.port.getBlockPos(),t.port.tank);
            t.port.battery.connect(t.other.battery);t.other.battery.connect(t.port.battery);
            if(!t.wiredConnected){t.port.ccWiredElement.connect(t.other.ccWiredElement);t.wiredConnected=true;}
        }
        t.store();
        if(t.joint!=null&&ctx.contraption.entity!=null&&(ctx.contraption.entity.tickCount<=5||ctx.contraption.entity.tickCount%20==0))t.syncRender(false);
    }
    @Override public void writeExtraData(MovementContext ctx){var t=transit(ctx);if(t!=null)t.store();}
    // Create also calls stopMoving when reversing direction. Restoration happens only in addBlocksToWorld.
    public static void beforeRestore(Contraption c,Level l,StructureTransform transform){if(l.isClientSide)return;
        for(var t:new ArrayList<>(PORTS.values()))if(t.context.contraption==c){t.store();t.target=transform.apply(t.context.localPos);}
    }
    public static void afterRestore(Contraption c,Level l){if(l.isClientSide)return;
        for(var t:new ArrayList<>(PORTS.values()))if(t.context.contraption==c){
            t.syncRender(true);PORTS.remove(t.port);if(t.joint!=null)t.joint.remove();
            var placed=l.getBlockEntity(t.target);
            if(placed instanceof DockingConnectorBlockEntity port&&t.other!=null){
                ((PortAccess)t.other).morefix$clearWaiting();
                t.other.otherConnectorPosition=null;t.other.otherConnectorSubLevelId=null;
                port.otherConnectorPosition=null;
                var peerLevel=Sable.HELPER.getContaining(t.other);
                port.otherConnectorSubLevelId=peerLevel==null?null:peerLevel.getUniqueId();
                var powered=net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
                port.powered=l.hasNeighborSignal(port.getBlockPos());
                l.setBlock(port.getBlockPos(),port.getBlockState().setValue(powered,port.powered),2);
                port.extension.setValue(1);port.feet.setValue(1);t.other.feet.setValue(1);
                // Native initialization rebuilds its stationary joint on the following tick.
                if(port.powered&&t.other.powered)port.pairTo(t.other);else t.other.unDock();
            }else if(t.other!=null)t.other.unDock();
            if(t.other!=null)t.other.sendData();
            t.port.remove();t.context.temporaryData=null;
        }
    }
    private static Transit transit(MovementContext ctx){
        if(ctx.temporaryData instanceof Transit t)return t;
        // Loaded contraptions contain the latest port NBT; recreate the functional port without world placement.
        if(ctx.world.isClientSide||ctx.blockEntityData==null)return null;
        var tag=ctx.blockEntityData.copy();var pos=new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
        var loaded=BlockEntity.loadStatic(pos,ctx.state,tag,ctx.world.registryAccess());
        if(!(loaded instanceof DockingConnectorBlockEntity port))return null;
        port.setLevel(ctx.world);
        for(var previous:new ArrayList<>(PORTS.values()))if(previous.context.world==ctx.world&&previous.port.getBlockPos().equals(pos)){
            var t=new Transit(ctx,previous.port,previous.other);t.joint=previous.joint;t.targetBody=previous.targetBody;
            t.wiredConnected=previous.wiredConnected;
            t.localAnchor=previous.localAnchor;t.peerAnchor=previous.peerAnchor;t.localOrientation=previous.localOrientation;t.lastAnchor.set(previous.lastAnchor);
            PORTS.put(previous.port,t);ctx.temporaryData=t;return t;
        }
        var other=port.getOtherConnector();var t=new Transit(ctx,port,other);
        PORTS.put(port,t);ctx.temporaryData=t;port.initialize();return t;
    }
    private static final class Transit {
        final MovementContext context;final DockingConnectorBlockEntity port;DockingConnectorBlockEntity other;
        GenericConstraintHandle joint;BlockPos target;Vector3d localAnchor,peerAnchor;Quaterniond localOrientation;ServerSubLevel targetBody;
        Vector3d lastAnchor=new Vector3d(Double.NaN);
        Quaterniond lastOrientation=new Quaterniond(Double.NaN,0,0,1);
        boolean wiredConnected;
        Transit(MovementContext c,DockingConnectorBlockEntity p,DockingConnectorBlockEntity o){context=c;port=p;other=o;}
        void store(){CompoundTag saved=port.saveWithFullMetadata(context.world.registryAccess());context.blockEntityData.merge(saved);
            var info=context.contraption.getBlocks().get(context.localPos);if(info!=null&&info.nbt()!=null)info.nbt().merge(saved);
        }
        void release(){syncRender(true);if(joint!=null){joint.remove();joint=null;}var peer=other;other=null;wiredConnected=false;
            if(peer!=null)port.ccWiredElement.disconnect(peer.ccWiredElement);
            // Captured actors and removed plots have no world block to notify.
            // Keep native cleanup, but never send updates to nonexistent plot holders.
            PORTS.remove(port);((PortAccess)port).morefix$disconnectDetached();
            if(peer!=null){
                ((PortAccess)peer).morefix$clearWaiting();
                if(live(peer))peer.unDock();else ((PortAccess)peer).morefix$disconnectDetached();
            }
            PORTS.put(port,this);
        }
        void attach(){
            var entity=context.contraption.entity;var k=(KinematicContraption)(Object)entity;
            var system=SubLevelPhysicsSystem.require((ServerLevel)context.world);var pipeline=system.getPipeline();
            if(k.sable$getMassTracker()==null)return;
            var sub=Sable.HELPER.getContaining(other);if(!(sub instanceof ServerSubLevel level))return;
            targetBody=level;
            ((PortAccess)port).morefix$clearConstraint();((PortAccess)other).morefix$clearConstraint();
            Vec3 tip=port.getTipPosition().subtract(Vec3.atLowerCornerOf(port.getBlockPos())).add(Vec3.atLowerCornerOf(context.localPos));
            localAnchor=new Vector3d(tip.x,tip.y,tip.z);
            var world=entity.toGlobalVector(tip,1);var a=new Vector3d(world.x,world.y,world.z);
            var b=new Vector3d(a);level.logicalPose().transformPositionInverse(b);
            peerAnchor=new Vector3d(b);
            localOrientation=new Quaterniond(k.sable$getOrientation()).conjugate().mul(level.logicalPose().orientation());
            joint=pipeline.addConstraint(null,level,new GenericConstraintConfiguration(a,b,new Quaterniond(level.logicalPose().orientation()),new Quaterniond(),EnumSet.allOf(ConstraintJointAxis.class)));
            if(joint==null)throw new IllegalStateException("Sable rejected moving docking joint");
            other.sendData();
            syncRender(false);
        }
        void syncRender(boolean remove){
            if(targetBody==null||context.contraption.entity==null)return;
            var tag=new CompoundTag();
            if(remove){tag.putUUID("SubLevel",targetBody.getUniqueId());tag.putBoolean("Remove",true);}
            else {var outer=new CompoundTag();writeRenderBinding(other,outer);if(!outer.contains("MoreFixMovingRender"))return;tag=outer.getCompound("MoreFixMovingRender");}
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntity(context.contraption.entity,new DockingPayload(tag));
        }
        void step(boolean physics){
            if(joint==null||!joint.isValid()||context.contraption.entity==null)return;
            var entity=context.contraption.entity;
            if(entity.isRemoved())return;
            double partial=SubLevelPhysicsSystem.require((ServerLevel)context.world).getPartialPhysicsTick();
            var world=entity.toGlobalVector(new Vec3(localAnchor.x,localAnchor.y,localAnchor.z),(float)partial);
            // Sable runs before Create's entity tick. Advance the anchor through the
            // upcoming gantry movement, using the live shaft speed (including reversals).
            if(physics&&entity instanceof com.simibubi.create.content.contraptions.gantry.GantryContraptionEntity gantry
                &&context.contraption instanceof com.simibubi.create.content.contraptions.gantry.GantryContraption contraption
                &&!gantry.isStalled()&&gantry.tickCount>2){
                var center=gantry.getAnchorVec().add(0.5,0.5,0.5);
                var shaftPos=BlockPos.containing(center).relative(contraption.getFacing().getOpposite());
                if(context.world.getBlockEntity(shaftPos) instanceof com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity shaft){
                    var state=shaft.getBlockState();
                    if(!state.getValue(com.simibubi.create.content.kinetics.gantry.GantryShaftBlock.POWERED)){
                        var direction=state.getValue(com.simibubi.create.content.kinetics.gantry.GantryShaftBlock.FACING);
                        double speed=shaft.getPinionMovementSpeed();
                        if(gantry.sequencedOffsetLimit>=0)speed=net.minecraft.util.Mth.clamp(speed,-gantry.sequencedOffsetLimit,gantry.sequencedOffsetLimit);
                        var motion=Vec3.atLowerCornerOf(direction.getNormal()).scale(speed);
                        double current=direction.getAxis().choose(center.x,center.y,center.z);
                        var next=center.add(motion);double nextCoord=direction.getAxis().choose(next.x,next.y,next.z);
                        if(!((net.minecraft.util.Mth.floor(current)+0.5<nextCoord)!=(speed*direction.getAxisDirection().getStep()<0)&&!shaft.canAssembleOn()))
                            world=world.add(motion.scale(java.lang.Math.min(1,partial)));
                    }
                }
            }
            var q=new Quaterniond(((KinematicContraption)(Object)entity).sable$getOrientation(partial));
            // Physics precedes Create's bearing tick. Use the live angular speed
            // for the upcoming substep, including stops and direction reversals.
            if(physics&&entity instanceof com.simibubi.create.content.contraptions.ControlledContraptionEntity controlled
                &&context.contraption instanceof com.simibubi.create.content.contraptions.bearing.BearingContraption bearing
                &&!entity.isStalled()
                &&context.world.getBlockEntity(context.contraption.anchor.relative(bearing.getFacing().getOpposite()))
                    instanceof com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity controller){
                double delta=controller.getAngularSpeed()*java.lang.Math.min(1,partial);
                var axis=controlled.getRotationAxis();
                var pivot=entity.toGlobalVector(new Vec3(0.5,0.5,0.5),1);
                world=net.createmod.catnip.math.VecHelper.rotate(
                    entity.toGlobalVector(new Vec3(localAnchor.x,localAnchor.y,localAnchor.z),1).subtract(pivot),delta,axis).add(pivot);
                var matrix=new Matrix3d();
                for(int i=0;i<3;i++){
                    var basis=new Vector3d();matrix.getColumn(i,basis);
                    var rotated=net.createmod.catnip.math.VecHelper.rotate(new Vec3(basis.x,basis.y,basis.z),delta,axis);
                    matrix.setColumn(i,rotated.x,rotated.y,rotated.z);
                }
                q.setFromNormalized(matrix).mul(((KinematicContraption)(Object)entity).sable$getOrientation(1));
            }
            q.mul(localOrientation);
            var position=new Vector3d(world.x,world.y,world.z);
            joint.setFrame1(position,q);
            // Updating a joint frame does not wake a sleeping Rapier body by itself.
            if(!Double.isFinite(lastAnchor.x)||lastAnchor.distanceSquared(position)>1e-12
                ||!Double.isFinite(lastOrientation.x)||1-java.lang.Math.abs(lastOrientation.dot(q))>1e-12){
                SubLevelPhysicsSystem.require((ServerLevel)context.world).getPipeline().wakeUp(targetBody);
                lastAnchor.set(position);
                lastOrientation.set(q);
            }
        }
    }
    public static void physicsStep(Object peer){for(var t:PORTS.values())if(t.other==peer)t.step(true);}
    private static boolean live(DockingConnectorBlockEntity port){
        var level=port.getLevel();if(level==null||port.isRemoved())return false;
        var container=dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(level);
        if(container!=null&&container.inBounds(port.getBlockPos())
            &&container.getPlot(new net.minecraft.world.level.ChunkPos(port.getBlockPos()))==null)return false;
        return level.getBlockEntity(port.getBlockPos())==port;
    }
}
