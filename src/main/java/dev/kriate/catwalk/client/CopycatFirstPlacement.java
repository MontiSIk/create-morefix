package dev.kriate.catwalk.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import java.util.*;

@EventBusSubscriber(modid="catwalk_orientation",value=Dist.CLIENT)
public final class CopycatFirstPlacement {
    private static final Set<BlockEntity> PENDING=Collections.newSetFromMap(new IdentityHashMap<>());
    public static void loaded(BlockEntity entity){if(entity.getLevel()!=null&&entity.getLevel().isClientSide)PENDING.add(entity);}
    @SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
        var mc=Minecraft.getInstance();var pending=new ArrayList<>(PENDING);PENDING.clear();
        for(var entity:pending){
            if(entity.isRemoved()||entity.getLevel()!=mc.level)continue;
            var pos=entity.getBlockPos();
            // Loading model data alone does not invalidate a mesh compiled before the BE arrived.
            ((IBlockEntityExtension)entity).requestModelDataUpdate();
            entity.getLevel().sendBlockUpdated(pos,entity.getBlockState(),entity.getBlockState(),3);
        }
    }
    @SubscribeEvent public static void logout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event){PENDING.clear();}
}
