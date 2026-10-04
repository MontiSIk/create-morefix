package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.VanillaConnections;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
@Mixin(HoneycombItem.class)
public abstract class ConnectedWaxMixin {
    @Inject(method="getWaxed",at=@At("HEAD"),cancellable=true)
    private static void morefix$wax(BlockState state,CallbackInfoReturnable<Optional<BlockState>> cir){if(state.getBlock() instanceof VanillaConnections.ConnectedBlock connected)cir.setReturnValue(HoneycombItem.getWaxed(connected.base.defaultBlockState()).map(VanillaConnections::placement));}
}
