package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.contraptions.gantry.GantryCarriageVisual;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.kriate.catwalk.GantryAxes;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value=GantryCarriageVisual.class,remap=false)
public abstract class GantryCarriageVisualMixin {
    // ShaftVisual's constructor still uses the actual shaft axis; this call belongs only to the cogs.
    @Redirect(method="<init>",at=@At(value="INVOKE",target="Lcom/simibubi/create/content/kinetics/base/KineticBlockEntityRenderer;getRotationAxisOf(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)Lnet/minecraft/core/Direction$Axis;"))
    private Direction.Axis morefix$pinion(KineticBlockEntity be){return GantryAxes.pinionAxis(be.getBlockState());}
}
