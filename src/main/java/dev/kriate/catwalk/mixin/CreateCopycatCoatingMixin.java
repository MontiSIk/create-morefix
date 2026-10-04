package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=CopycatBlock.class,remap=false)
public abstract class CreateCopycatCoatingMixin {
    @Inject(method="useItemOn",at=@At("RETURN"),cancellable=true)
    private void morefix$nativeCoating(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit,CallbackInfoReturnable<ItemInteractionResult> cir){
        cir.setReturnValue(dev.kriate.catwalk.CopycatCoatings.apply(stack,state,level,pos,player,hit,cir.getReturnValue()));
    }
}
