package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.kriate.catwalk.pipes.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=FluidPipeBlockEntity.class,remap=false)
public abstract class PipeSupportTransformMixin {
    @Inject(method="transform",at=@At("RETURN"))
    private void morefix$rotate(BlockEntity be,StructureTransform transform,CallbackInfo ci){var b=com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour.get((BlockEntity)(Object)this,PipeSupportBehaviour.TYPE);if(b!=null)b.transform(transform);}
}
