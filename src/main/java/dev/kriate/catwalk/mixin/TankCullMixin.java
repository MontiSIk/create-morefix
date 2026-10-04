package dev.kriate.catwalk.mixin;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Pseudo
@Mixin(targets={"com.simibubi.create.content.fluids.tank.FluidTankModel$CullData","fr.iglee42.createcasing.fluids.EncasedFluidTankModel$CullData"},remap=false)
public abstract class TankCullMixin {
    @Shadow(remap=false) boolean[] culledFaces;
    @Inject(method="<init>",at=@At("RETURN"))
    private void morefix$sixSides(CallbackInfo ci) {culledFaces=new boolean[6];}
    @Inject(method="setCulled",at=@At("HEAD"),cancellable=true)
    private void morefix$set(Direction face,boolean cull,CallbackInfo ci) {culledFaces[face.get3DDataValue()]=cull;ci.cancel();}
    @Inject(method="isCulled",at=@At("HEAD"),cancellable=true)
    private void morefix$get(Direction face,CallbackInfoReturnable<Boolean> cir) {cir.setReturnValue(culledFaces[face.get3DDataValue()]);}
}

