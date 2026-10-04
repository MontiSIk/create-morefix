package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.fluids.pipes.*;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.kriate.catwalk.pipes.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(value={StraightPipeBlockEntity.class,SmartFluidPipeBlockEntity.class},remap=false)
public abstract class StraightPipeSupportTransformMixin implements TransformableBlockEntity {
    @Override public void transform(BlockEntity be,StructureTransform transform){var b=com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour.get((BlockEntity)(Object)this,PipeSupportBehaviour.TYPE);if(b!=null)b.transform(transform);}
}
