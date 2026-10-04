package dev.kriate.catwalk.mixin;

import com.github.talrey.createdeco.connected.CatwalkCTBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CatwalkCTBehaviour.class, remap = false)
public abstract class CatwalkCTMixin {
    @Inject(method = "connectsTo", at = @At("HEAD"), cancellable = true)
    private void catwalkOrientation$connect(BlockState state, BlockState other, BlockAndTintGetter level,
        BlockPos pos, BlockPos otherPos, Direction textureFace, CallbackInfoReturnable<Boolean> cir) {
        if (!state.hasProperty(BlockStateProperties.FACING)) return;
        Direction face = state.getValue(BlockStateProperties.FACING);
        cir.setReturnValue(state.getBlock() == other.getBlock()
            && other.hasProperty(BlockStateProperties.FACING)
            && other.getValue(BlockStateProperties.FACING) == face
            && textureFace.getAxis() == face.getAxis()
            && pos.get(face.getAxis()) == otherPos.get(face.getAxis()));
    }
}
