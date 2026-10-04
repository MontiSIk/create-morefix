package dev.kriate.catwalk.docking;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.gantry.GantryContraption;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import java.util.*;

/** Converts only the glued structure on the opposite docking port at gantry assembly. */
public final class DockingAssembly {
    private static final Set<Object> PREPARING=java.util.concurrent.ConcurrentHashMap.newKeySet();
    public static boolean preparing(Object port){return PREPARING.contains(port);}
    public static DockingConnectorBlockEntity prepare(DockingConnectorBlockEntity source,Contraption contraption,BlockPos offset){
        var peer=source.getOtherConnector();
        if(!(contraption instanceof GantryContraption)||!(source.getLevel() instanceof ServerLevel level)||!source.powered)return peer;
        var facing=source.getBlockState().getValue(BlockStateProperties.FACING);
        if(peer==null)for(int distance=2;distance<=3;distance++){
            if(level.getBlockEntity(source.getBlockPos().relative(facing,distance)) instanceof DockingConnectorBlockEntity candidate
                &&candidate.powered&&candidate.getBlockState().getValue(BlockStateProperties.FACING)==facing.getOpposite()){
                peer=candidate;break;
            }
        }
        if(peer==null||Sable.HELPER.getContaining(peer)!=null)return peer;
        Set<BlockPos> carriage=new HashSet<>();
        for(var pos:contraption.getBlocks().keySet())carriage.add(pos.offset(contraption.anchor).offset(offset));
        Set<BlockPos> blocks=new LinkedHashSet<>();Set<SuperGlueEntity> glue=new HashSet<>();
        var frontier=new ArrayDeque<BlockPos>();frontier.add(peer.getBlockPos());blocks.add(peer.getBlockPos());
        int limit=com.simibubi.create.infrastructure.config.AllConfigs.server().kinetics.maxBlocksMoved.get();
        while(!frontier.isEmpty()){
            var pos=frontier.removeFirst();
            if(carriage.contains(pos)||!level.isLoaded(pos))return null;
            for(var direction:Direction.values()){
                var next=pos.relative(direction);
                if(blocks.contains(next)||level.getBlockState(next).isAir())continue;
                if(SuperGlueEntity.isGlued(level,pos,direction,glue)){
                    if(blocks.size()>=limit)return null;
                    blocks.add(next.immutable());frontier.addLast(next.immutable());
                }
            }
        }
        if(blocks.size()<2||glue.isEmpty())return null;
        for(var pos:new ArrayList<>(blocks))if(level.getBlockState(pos).is(SimBlocks.DOCKING_CONNECTOR.get())){
            var extension=pos.relative(level.getBlockState(pos).getValue(BlockStateProperties.FACING));
            if(level.getBlockState(extension).is(SimBlocks.PAIRED_DOCKING_CONNECTOR.get())){
                if(carriage.contains(extension))return null;
                blocks.add(extension);
            }
        }
        PREPARING.add(source);
        try{
            var sub=SubLevelAssemblyHelper.assembleBlocks(level,peer.getBlockPos(),blocks,BoundingBox3i.from(blocks));
            if(sub==null)return null;
            if(!(level.getBlockEntity(sub.getPlot().getCenterBlock()) instanceof DockingConnectorBlockEntity moved))return null;
            moved.extension.setValue(1);moved.feet.setValue(1);source.extension.setValue(1);source.feet.setValue(1);
            source.pairTo(moved);
            return source.getOtherConnector();
        }finally{PREPARING.remove(source);}
    }
}
