package dev.kriate.catwalk;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import dev.kriate.catwalk.CopycatPlacementAccess;
import dev.kriate.catwalk.PlacementModes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class CopycatCoatings {
    public static ItemInteractionResult apply(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit,ItemInteractionResult result){
        if(!PlacementModes.ctrl(player)||!player.mayBuild())return result;
        if(!(level.getBlockEntity(pos) instanceof ICopycatBlockEntity cc)||!(cc instanceof CopycatPlacementAccess access))return result;
        ItemStack coating=stack;
        if(coating.isEmpty()&&result==ItemInteractionResult.SUCCESS){
            coating=cc.getConsumedItem();
            if(state.getBlock() instanceof IMultiStateCopycatBlock block&&cc instanceof IMultiStateCopycatBlockEntity multi)coating=multi.getMaterialItemStorage().getMaterialItem(block.getPropertyFromInteraction(state,level,pos,hit,true)).consumedItem();
        }
        if(!(coating.getItem() instanceof BlockItem item))return result;
        var id=BuiltInRegistries.BLOCK.getKey(item.getBlock());if(!PlacementModes.supports(id.getNamespace(),id.getPath()))return result;
        if(state.getBlock() instanceof IMultiStateCopycatBlock block&&cc instanceof IMultiStateCopycatBlockEntity multi){
            String part=block.getPropertyFromInteraction(state,level,pos,hit,true);
            if(!block.partExists(state,part)||!multi.getMaterialItemStorage().getMaterialItem(part).material().is(item.getBlock()))return result;
            multi.setEnableCT(part,true);
        }else{
            if(!cc.getMaterial().is(item.getBlock()))return result;
            cc.setCTEnabled(true);
        }
        access.morefix$connected(true);
        ((com.simibubi.create.foundation.blockEntity.SmartBlockEntity)cc).notifyUpdate();
        com.copycatsplus.copycats.utility.BlockEntityUtils.redraw((net.minecraft.world.level.block.entity.BlockEntity)cc);
        if(result==ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return ItemInteractionResult.SUCCESS;
        return result;
    }
}
