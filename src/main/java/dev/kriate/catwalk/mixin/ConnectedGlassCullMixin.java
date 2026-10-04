package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.VanillaConnections;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={HalfTransparentBlock.class,IronBarsBlock.class},remap=false)
public abstract class ConnectedGlassCullMixin {
    @Inject(method="skipRendering",at=@At("HEAD"),cancellable=true)
    private void morefix$matchingGlass(BlockState state,BlockState neighbor,Direction side,CallbackInfoReturnable<Boolean> cir){
        if(neighbor.getBlock() instanceof VanillaConnections.ConnectedVariant variant)
            cir.setReturnValue(state.skipRendering(VanillaConnections.copyProperties(neighbor,variant.originalBlock().defaultBlockState()),side));
    }
}
