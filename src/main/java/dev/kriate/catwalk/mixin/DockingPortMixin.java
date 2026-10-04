package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.docking.*;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo
@Mixin(targets="dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity",remap=false)
public abstract class DockingPortMixin implements PortAccess {
    @Unique private java.util.UUID morefix$waitingOwner;
    @Shadow protected DockingConnectorBlockEntity.DockingConnectorState state;
    @Invoker("removeConstraint") public abstract void morefix$clearConstraint();
    public void morefix$locked(){state=DockingConnectorBlockEntity.DockingConnectorState.LOCKED;}
    public void morefix$clearWaiting(){morefix$waitingOwner=null;}
    @Inject(method="sable$physicsTick",at=@At("HEAD"))
    private void movingAnchor(CallbackInfo ci){MovingPorts.physicsStep(this);}
    @Inject(method="remove",at=@At("HEAD"),cancellable=true)
    private void preserve(CallbackInfo ci){if(MovingPorts.moving(this))ci.cancel();}
    @Inject(method="updateSignal",at=@At("HEAD"),cancellable=true)
    private void preserveMovingPower(CallbackInfo ci){
        // The original world position no longer describes the moving port's neighbors.
        if(MovingPorts.moving(this)||DockingAssembly.preparing(this))ci.cancel();
    }
    @Inject(method="tick",at=@At("HEAD"),cancellable=true)
    private void boundTick(CallbackInfo ci){
        var port=(DockingConnectorBlockEntity)(Object)this;
        if((MovingPorts.bound(this)||morefix$waitingOwner!=null)&&port.powered)ci.cancel();
    }
    @Inject(method="unDock",at=@At("HEAD"),cancellable=true)
    private void keepLink(CallbackInfo ci){if((MovingPorts.bound(this)||morefix$waitingOwner!=null)&&((DockingConnectorBlockEntity)(Object)this).powered)ci.cancel();else morefix$waitingOwner=null;}
    @Inject(method="setDock",at=@At("HEAD"),cancellable=true)
    private void keepJoint(CallbackInfo ci){if(MovingPorts.bound(this))ci.cancel();}
    @Redirect(method="getOtherConnector",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity lookup(Level l,BlockPos p){return MovingPorts.resolve(l,p);}
    @Inject(method="write",at=@At("RETURN"))
    private void saveMovingPeer(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider registries,boolean clientPacket,CallbackInfo ci){
        var owner=MovingPorts.owner(this);
        if(owner!=null)tag.putUUID("MoreFixMovingPeer",owner);else if(morefix$waitingOwner!=null)tag.putUUID("MoreFixMovingPeer",morefix$waitingOwner);
        MovingPorts.writeRenderBinding(this,tag);
    }
    @Inject(method="read",at=@At("RETURN"))
    private void readMovingPeer(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider registries,boolean clientPacket,CallbackInfo ci){
        morefix$waitingOwner=tag.hasUUID("MoreFixMovingPeer")?tag.getUUID("MoreFixMovingPeer"):null;
    }
}

