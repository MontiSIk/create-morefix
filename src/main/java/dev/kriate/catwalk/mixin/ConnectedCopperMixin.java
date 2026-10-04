package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.VanillaConnections;
import net.minecraft.world.level.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
@Mixin(WeatheringCopper.class)
public interface ConnectedCopperMixin {
    @Inject(method="getNext(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;",at=@At("HEAD"),cancellable=true)
    private static void morefix$next(Block block,CallbackInfoReturnable<Optional<Block>> cir){if(block instanceof VanillaConnections.ConnectedBlock connected)cir.setReturnValue(WeatheringCopper.getNext(connected.base).map(b->VanillaConnections.placement(b.defaultBlockState()).getBlock()));}
    @Inject(method="getPrevious(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;",at=@At("HEAD"),cancellable=true)
    private static void morefix$previous(Block block,CallbackInfoReturnable<Optional<Block>> cir){if(block instanceof VanillaConnections.ConnectedBlock connected)cir.setReturnValue(WeatheringCopper.getPrevious(connected.base).map(b->VanillaConnections.placement(b.defaultBlockState()).getBlock()));}
    @Inject(method="getFirst(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;",at=@At("HEAD"),cancellable=true)
    private static void morefix$first(Block block,CallbackInfoReturnable<Block> cir){if(block instanceof VanillaConnections.ConnectedBlock connected)cir.setReturnValue(VanillaConnections.placement(WeatheringCopper.getFirst(connected.base).defaultBlockState()).getBlock());}
}
