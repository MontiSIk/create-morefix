package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.contraptions.gantry.GantryCarriageRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.kriate.catwalk.GantryAxes;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value=GantryCarriageRenderer.class,remap=false)
public abstract class GantryCarriageRendererMixin {
    // Only the pinion uses the legacy axis. The base renderer draws the output shaft on its new axis.
    @Redirect(method="renderSafe(Lcom/simibubi/create/content/contraptions/gantry/GantryCarriageBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",at=@At(value="INVOKE",target="Lcom/simibubi/create/content/contraptions/gantry/GantryCarriageRenderer;getRotationAxisOf(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)Lnet/minecraft/core/Direction$Axis;"))
    private Direction.Axis morefix$pinion(KineticBlockEntity be){return GantryAxes.pinionAxis(be.getBlockState());}
}
