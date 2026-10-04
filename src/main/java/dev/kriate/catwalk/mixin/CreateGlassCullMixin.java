package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.VanillaConnections;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassBlock;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ConnectedGlassBlock.class,ConnectedGlassPaneBlock.class,WindowBlock.class},remap=false)
public abstract class CreateGlassCullMixin {
    @Inject(method="skipRendering",at=@At("HEAD"),cancellable=true)
    private void morefix$matchingGlass(BlockState state,BlockState neighbor,Direction side,CallbackInfoReturnable<Boolean> cir){
        if(neighbor.getBlock() instanceof VanillaConnections.ConnectedVariant variant)
            cir.setReturnValue(state.skipRendering(VanillaConnections.copyProperties(neighbor,variant.originalBlock().defaultBlockState()),side));
    }
}
