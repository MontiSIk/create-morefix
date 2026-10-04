package dev.kriate.catwalk.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets="com.railwayteam.railways.content.palettes.boiler.BoilerBlock",remap=false)
public abstract class LocometalBoilerOutlineMixin {
    @Inject(method="matrixRotation",at=@At("HEAD"),cancellable=true)
    private void morefix$verticalOutline(PoseStack pose,BlockState state,CallbackInfo ci){
        if(state.getValue(BlockStateProperties.AXIS)==Direction.Axis.Y){pose.mulPose(Axis.XP.rotationDegrees(-90));ci.cancel();}
    }
}
