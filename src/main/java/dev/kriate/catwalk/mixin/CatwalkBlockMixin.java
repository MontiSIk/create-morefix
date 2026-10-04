package dev.kriate.catwalk.mixin;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.blocks.SupportBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.kriate.catwalk.CatwalkGeometry;
import dev.kriate.catwalk.EmbeddedRailings;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CatwalkBlock.class, remap = false)
public abstract class CatwalkBlockMixin extends Block implements IWrenchable {
    protected CatwalkBlockMixin(BlockBehaviour.Properties properties) { super(properties); }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void catwalkOrientation$define(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(BlockStateProperties.FACING, EmbeddedRailings.NORTH, EmbeddedRailings.EAST,
            EmbeddedRailings.SOUTH, EmbeddedRailings.WEST);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void catwalkOrientation$default(BlockBehaviour.Properties properties, CallbackInfo ci) {
        // Missing facing in old saves resolves to the original upper catwalk.
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP)
            .setValue(EmbeddedRailings.NORTH,false).setValue(EmbeddedRailings.EAST,false)
            .setValue(EmbeddedRailings.SOUTH,false).setValue(EmbeddedRailings.WEST,false));
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void catwalkOrientation$place(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state == null) return;
        Direction clicked = context.getClickedFace();
        Direction face;
        BlockState neighbor = context.getLevel().getBlockState(context.getClickedPos().relative(clicked.getOpposite()));
        if (!context.isSecondaryUseActive() && neighbor.getBlock() instanceof CatwalkBlock) {
            face = neighbor.getValue(BlockStateProperties.FACING);
        } else if (context.isSecondaryUseActive() && clicked.getAxis().isHorizontal()) {
            face = clicked.getOpposite();
        } else if (clicked == Direction.UP) {
            face = Direction.DOWN;
        } else if (clicked == Direction.DOWN) {
            face = Direction.UP;
        } else {
            face = context.getClickLocation().y - context.getClickedPos().getY() > 0.5
                ? Direction.UP : Direction.DOWN;
        }
        cir.setReturnValue(state.setValue(BlockStateProperties.FACING, face)
            .setValue(CatwalkBlock.BOTTOM, context.getLevel()
                .getBlockState(context.getClickedPos().relative(face.getOpposite())).getBlock() instanceof SupportBlock));
    }

    @Inject(method = "isBottom", at = @At("HEAD"), cancellable = true)
    private void catwalkOrientation$support(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CatwalkBlock && state.hasProperty(BlockStateProperties.FACING)) {
            Direction face = state.getValue(BlockStateProperties.FACING);
            cir.setReturnValue(level.getBlockState(pos.relative(face.getOpposite())).getBlock() instanceof SupportBlock);
        }
    }

    @Inject(method = "getInteractionShape", at = @At("HEAD"), cancellable = true)
    private void catwalkOrientation$shape(BlockState state, BlockGetter level, BlockPos pos,
                                         CallbackInfoReturnable<VoxelShape> cir) {
        Direction face = state.getValue(BlockStateProperties.FACING);
        // Deco uses full-block collision when the support is present. Preserve
        // that behaviour for every mounting direction, not only the upper one.
        cir.setReturnValue(EmbeddedRailings.shape(state,
            state.getValue(CatwalkBlock.BOTTOM) ? Shapes.block() : CatwalkGeometry.shape(face)));
    }

    @Override
    public BlockState getRotatedBlockState(BlockState state, Direction targetedFace) {
        return state.setValue(BlockStateProperties.FACING,
            CatwalkGeometry.next(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return EmbeddedRailings.reorient(state,rotation.rotate(state.getValue(BlockStateProperties.FACING)),rotation::rotate);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return EmbeddedRailings.reorient(state,mirror.mirror(state.getValue(BlockStateProperties.FACING)),mirror::mirror);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state,UseOnContext context) {
        if(EmbeddedRailings.count(state)==0)return IWrenchable.super.onSneakWrenched(state,context);
        var level=context.getLevel(); var player=context.getPlayer(); var pos=context.getClickedPos();
        if(player!=null && (!player.mayBuild() || !level.mayInteract(player,pos)))return InteractionResult.FAIL;
        if(!level.isClientSide) {
            Direction edge=EmbeddedRailings.edge(state,pos,context.getClickLocation(),true);
            level.setBlock(pos,state.setValue(EmbeddedRailings.property(edge),false),3);
            if(player!=null && !player.getAbilities().instabuild)
                player.getInventory().placeItemBackInInventory(new ItemStack(EmbeddedRailings.railing(state)));
            IWrenchable.playRemoveSound(level,pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params) {
        List<ItemStack> drops=new ArrayList<>(super.getDrops(state,params));
        int count=EmbeddedRailings.count(state);
        Float radius=params.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        if(radius!=null) {
            int surviving=0;
            for(int i=0;i<count;i++) if(params.getLevel().random.nextFloat()<=1.0f/radius) surviving++;
            count=surviving;
        }
        if(count>0)drops.add(new ItemStack(EmbeddedRailings.railing(state),count));
        return drops;
    }
}
