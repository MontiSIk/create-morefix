package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=FluidTankBlock.class,remap=false)
public abstract class TankBlockMixin extends Block implements IWrenchable {
    protected TankBlockMixin(Properties properties) {super(properties);}
    @Inject(method="createBlockStateDefinition",at=@At("TAIL"))
    private void morefix$property(StateDefinition.Builder<Block,BlockState> builder,CallbackInfo ci) {builder.add(BlockStateProperties.AXIS);}
    @Inject(method="<init>",at=@At("TAIL"))
    private void morefix$default(Properties properties,boolean creative,CallbackInfo ci) {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.AXIS,Axis.Y));
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var against=ctx.getLevel().getBlockState(ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite()));
        Axis selected=ctx.getClickedFace().getAxis().isHorizontal()?ctx.getClickedFace().getAxis():ctx.getHorizontalDirection().getAxis();
        Axis axis=ctx.getPlayer()!=null && ctx.getPlayer().isShiftKeyDown()?selected:
            against.getBlock()==this?StorageAxes.axis(against):Axis.Y;
        return defaultBlockState().setValue(BlockStateProperties.AXIS,axis);
    }
    @Override public BlockState getRotatedBlockState(BlockState state,Direction face) {
        Axis axis=StorageAxes.axis(state);
        return state.setValue(BlockStateProperties.AXIS,axis==Axis.Y?Axis.X:axis==Axis.X?Axis.Z:Axis.Y);
    }
    @Inject(method="rotate",at=@At("RETURN"),cancellable=true)
    private void morefix$rotation(BlockState state,Rotation rotation,CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(cir.getReturnValue().setValue(BlockStateProperties.AXIS,rotation.rotate(Direction.fromAxisAndDirection(StorageAxes.axis(state),Direction.AxisDirection.POSITIVE)).getAxis()));
    }
}
