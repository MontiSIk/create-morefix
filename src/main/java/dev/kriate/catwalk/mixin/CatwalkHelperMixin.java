package dev.kriate.catwalk.mixin;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.blocks.SupportBlock;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(targets="com.github.talrey.createdeco.items.CatwalkBlockItem$CatwalkHelper", remap=false)
public abstract class CatwalkHelperMixin {
    @Inject(method="getOffset",at=@At("HEAD"),cancellable=true)
    private void orientedOffset(Player player, Level level, BlockState state, BlockPos pos, BlockHitResult hit,
                                CallbackInfoReturnable<PlacementOffset> cir) {
        if(!(state.getBlock() instanceof CatwalkBlock)) return;
        Direction face=state.getValue(BlockStateProperties.FACING);
        List<Direction> directions=hit.getDirection().getAxis()==face.getAxis()
            ? IPlacementHelper.orderedByDistanceExceptAxis(pos,hit.getLocation(),face.getAxis())
            : List.of(hit.getDirection());
        for(Direction direction:directions) {
            BlockPos target=pos.relative(direction);
            if(!CatwalkBlock.canPlaceCatwalk(level,target)) continue;
            boolean supported=level.getBlockState(target.relative(face.getOpposite())).getBlock() instanceof SupportBlock;
            cir.setReturnValue(PlacementOffset.success(target, placed -> placed
                .setValue(BlockStateProperties.FACING,face).setValue(CatwalkBlock.BOTTOM,supported)));
            return;
        }
        cir.setReturnValue(PlacementOffset.fail());
    }
}
