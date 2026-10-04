package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=ItemVaultBlock.class, remap=false)
public abstract class VaultBlockMixin extends Block implements IWrenchable {
    @Shadow @Final @Mutable public static Property<Axis> HORIZONTAL_AXIS;
    protected VaultBlockMixin(Properties properties) { super(properties); }
    @Inject(method="<clinit>",at=@At("RETURN"))
    private static void morefix$axis(CallbackInfo ci) { HORIZONTAL_AXIS=BlockStateProperties.AXIS; }
    @Inject(method="getStateForPlacement",at=@At("HEAD"),cancellable=true)
    private void morefix$placement(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir) {
        if (ctx.getPlayer()!=null && ctx.getPlayer().isShiftKeyDown())
            cir.setReturnValue(defaultBlockState().setValue(HORIZONTAL_AXIS,ctx.getClickedFace().getAxis()));
    }
    @Override public BlockState getRotatedBlockState(BlockState state, Direction face) {
        Axis axis=state.getValue(HORIZONTAL_AXIS);
        return state.setValue(HORIZONTAL_AXIS,axis==Axis.X?Axis.Y:axis==Axis.Y?Axis.Z:Axis.X);
    }
    @Inject(method="onWrenched",at=@At("HEAD"),cancellable=true)
    private void morefix$wrench(BlockState state, UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ctx.getLevel().isClientSide && ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof ItemVaultBlockEntity be) {
            ConnectivityHandler.splitMulti(be);
            be.removeController(true);
            ctx.getLevel().setBlock(ctx.getClickedPos(),getRotatedBlockState(state,ctx.getClickedFace()).setValue(ItemVaultBlock.LARGE,false),3);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
