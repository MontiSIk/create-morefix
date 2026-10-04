package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.pipes.*;
import com.simibubi.create.content.equipment.wrench.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=WrenchItem.class,remap=false)
public abstract class PipeSupportWrenchMixin {
    @Inject(method="useOn",at=@At("HEAD"),cancellable=true)
    private void morefix$turnSupport(UseOnContext ctx,CallbackInfoReturnable<InteractionResult> cir){
        if(ctx.isSecondaryUseActive())return;
        var b=PipeSupports.behaviour(ctx.getLevel(),ctx.getClickedPos());if(b==null||b.support==null)return;
        var player=ctx.getPlayer();if(player==null||!player.mayBuild()||!ctx.getLevel().mayInteract(player,ctx.getClickedPos())){cir.setReturnValue(InteractionResult.FAIL);return;}
        if(!ctx.getLevel().isClientSide){b.setSupport(SupportRotation.turn(b.support,ctx.getClickedFace()));IWrenchable.playRotateSound(ctx.getLevel(),ctx.getClickedPos());}
        cir.setReturnValue(InteractionResult.sidedSuccess(ctx.getLevel().isClientSide));
    }
}
