package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.PlacementModes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class ConnectedDefaultMixin {
    @Shadow private BlockState defaultBlockState;
    @Inject(method="<init>",at=@At("RETURN"))
    private void morefix$defaultIndependent(CallbackInfo ci){if(defaultBlockState.hasProperty(PlacementModes.CONNECTED))defaultBlockState=defaultBlockState.setValue(PlacementModes.CONNECTED,false);}
}
