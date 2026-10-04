package dev.kriate.catwalk.mixin;

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

@Mixin(value={ICopycatBlock.class,IMultiStateCopycatBlock.class},remap=false)
public interface CopycatCoatingMixin {
    @Inject(method="useItemOn",at=@At("RETURN"),cancellable=true)
    private void morefix$coatControl(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit,CallbackInfoReturnable<ItemInteractionResult> cir){
        cir.setReturnValue(dev.kriate.catwalk.CopycatCoatings.apply(stack,state,level,pos,player,hit,cir.getReturnValue()));
    }
}
