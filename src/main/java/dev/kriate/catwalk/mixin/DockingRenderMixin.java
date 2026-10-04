package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.client.DockingRender;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="dev.ryanhcode.sable.sublevel.ClientSubLevel",remap=false)
public abstract class DockingRenderMixin {
    @Inject(method="renderPose(F)Ldev/ryanhcode/sable/companion/math/Pose3dc;",at=@At("RETURN"))
    private void alignRender(float partial,CallbackInfoReturnable<Pose3dc> ci){
        if(ci.getReturnValue() instanceof Pose3d pose)DockingRender.align((ClientSubLevel)(Object)this,pose,partial);
    }
    @Inject(method="tick",at=@At("RETURN"))
    private void alignLogical(CallbackInfo ci){
        var sub=(ClientSubLevel)(Object)this;
        if(DockingRender.align(sub,sub.logicalPose(),1))sub.forceUpdateBounds();
    }
}
