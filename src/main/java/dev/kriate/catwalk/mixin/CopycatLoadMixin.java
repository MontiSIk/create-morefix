package dev.kriate.catwalk.mixin;

import dev.kriate.catwalk.client.CopycatFirstPlacement;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity","com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlockEntity","com.simibubi.create.content.decoration.copycat.CopycatBlockEntity","com.copycatsplus.copycats.content.copycat.fluid_pipe.CopycatFluidPipeBlockEntity","com.copycatsplus.copycats.content.copycat.fluid_pipe.CopycatStraightPipeBlockEntity","com.copycatsplus.copycats.content.copycat.shaft.CopycatShaftBlockEntity","com.copycatsplus.copycats.content.copycat.sliding_door.CopycatSlidingDoorBlockEntity","com.copycatsplus.copycats.content.copycat.cogwheel.CopycatCogWheelBlockEntity"},priority=900)
public abstract class CopycatLoadMixin {
    @Inject(method="onLoad",at=@At("RETURN"),remap=false)
    private void morefix$firstMesh(CallbackInfo ci){CopycatFirstPlacement.loaded((BlockEntity)(Object)this);}
}
