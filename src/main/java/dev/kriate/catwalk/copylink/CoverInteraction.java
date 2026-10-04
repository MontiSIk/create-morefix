package dev.kriate.catwalk.copylink;
import com.copycatsplus.copycats.CCBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.link.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
@EventBusSubscriber(modid="catwalk_orientation")
public final class CoverInteraction {
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.HIGHEST) public static void cover(PlayerInteractEvent.RightClickBlock event){
        var level=event.getLevel();var pos=event.getPos();var stack=event.getItemStack();var state=level.getBlockState(pos);
        if(!AllBlocks.REDSTONE_LINK.has(state)||!stack.is(CCBlocks.COPYCAT_BLOCK.asItem()))return;
        event.setCanceled(true);event.setCancellationResult(InteractionResult.SUCCESS);
        if(level.isClientSide)return;
        if(!(level.getBlockEntity(pos) instanceof RedstoneLinkBlockEntity original))return;
        var link=original.getBehaviour(LinkBehaviour.TYPE);
        if(link==null)return;
        var shell=CopyLinks.BLOCK.get().defaultBlockState().setValue(BlockStateProperties.FACING,state.getValue(BlockStateProperties.FACING)).setValue(RedstoneLinkBlock.RECEIVER,state.getValue(RedstoneLinkBlock.RECEIVER));
        level.setBlock(pos,shell,3);
        if(level.getBlockEntity(pos) instanceof CoveredLinkEntity covered){covered.wireless().copyItemsFrom(link);covered.notifyUpdate();if(!event.getEntity().isCreative())stack.shrink(1);}
    }
}

