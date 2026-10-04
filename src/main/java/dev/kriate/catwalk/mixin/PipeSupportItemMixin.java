package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.pipes.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
@Mixin(value=BlockItem.class,remap=false)
public abstract class PipeSupportItemMixin {
    @Inject(method="useOn",at=@At("HEAD"),cancellable=true)
    private void morefix$cover(UseOnContext ctx,CallbackInfoReturnable<InteractionResult> cir){var result=PipeSupports.cover((BlockItem)(Object)this,ctx);if(result!=null)cir.setReturnValue(result);}
    @WrapMethod(method="place")
    private InteractionResult morefix$inside(BlockPlaceContext ctx,Operation<InteractionResult> original){
        var item=(BlockItem)(Object)this;var pos=ctx.replacingClickedOnBlock()?ctx.getClickedPos():ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite());var support=ctx.getLevel().getBlockState(pos);
        if(ctx.isSecondaryUseActive()||!PipeSupports.isPipe(item.getBlock())||!PipeSupports.isSupport(support.getBlock()))return original.call(ctx);
        var result=original.call(PipeSupports.inside(ctx,pos));
        if(result.consumesAction()&&!ctx.getLevel().isClientSide){var b=PipeSupports.behaviour(ctx.getLevel(),pos);if(b!=null)b.setSupport(support);}
        return result;
    }
}
