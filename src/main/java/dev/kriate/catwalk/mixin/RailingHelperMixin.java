package dev.kriate.catwalk.mixin;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import dev.kriate.catwalk.EmbeddedRailings;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Predicate;

@Mixin(targets="com.github.talrey.createdeco.items.RailingBlockItem$RailingHelper",remap=false)
public abstract class RailingHelperMixin {
    @Inject(method="getStatePredicate",at=@At("RETURN"),cancellable=true)
    private void includeCatwalk(CallbackInfoReturnable<Predicate<BlockState>> cir) {
        cir.setReturnValue(cir.getReturnValue().or(state -> state.getBlock() instanceof CatwalkBlock));
    }
    @Inject(method="getOffset",at=@At("HEAD"),cancellable=true)
    private void preview(Player player,Level level,BlockState state,BlockPos pos,BlockHitResult hit,
                         CallbackInfoReturnable<PlacementOffset> cir) {
        if(!(state.getBlock() instanceof CatwalkBlock))return;
        var attached=EmbeddedRailings.attach(state,player.getMainHandItem(),pos,hit.getLocation());
        cir.setReturnValue(attached==null ? PlacementOffset.fail() : PlacementOffset.success(pos,ignored -> attached));
    }
}
