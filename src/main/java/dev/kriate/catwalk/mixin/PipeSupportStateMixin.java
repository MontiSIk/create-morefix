package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.pipes.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(value=BlockBehaviour.BlockStateBase.class,remap=false)
public abstract class PipeSupportStateMixin {
    /** Claim support placement before a Copycat interprets the support item as coating material. */
    @Inject(method="useItemOn",at=@At("HEAD"),cancellable=true)
    private void morefix$coverBeforeMaterial(ItemStack stack,net.minecraft.world.level.Level level,
            net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit,CallbackInfoReturnable<net.minecraft.world.ItemInteractionResult> cir){
        if(player==null||!(stack.getItem() instanceof net.minecraft.world.item.BlockItem item))return;
        var state=(net.minecraft.world.level.block.state.BlockState)(Object)this;
        if(!PipeSupports.isPipe(state.getBlock())||!PipeSupports.isSupport(item.getBlock()))return;
        var result=PipeSupports.cover(item,new net.minecraft.world.item.context.UseOnContext(player,hand,hit));
        if(result!=null)cir.setReturnValue(result==net.minecraft.world.InteractionResult.FAIL
                ?net.minecraft.world.ItemInteractionResult.FAIL:net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide));
    }
    @Inject(method={"getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;","getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;","getVisualShape"},at=@At("RETURN"),cancellable=true)
    private void morefix$shape(BlockGetter world,BlockPos pos,CollisionContext context,CallbackInfoReturnable<VoxelShape> cir){
        var state=(net.minecraft.world.level.block.state.BlockState)(Object)this;if(!PipeSupports.isPipe(state.getBlock()))return;
        var support=PipeSupports.support(world,pos);if(support!=null)cir.setReturnValue(Shapes.or(cir.getReturnValue(),support.getShape(world,pos,context)));
    }
    @Inject(method="getDrops",at=@At("RETURN"),cancellable=true)
    private void morefix$drops(LootParams.Builder params,CallbackInfoReturnable<java.util.List<ItemStack>> cir){
        var state=(net.minecraft.world.level.block.state.BlockState)(Object)this;if(!PipeSupports.isPipe(state.getBlock()))return;
        var be=params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);var b=com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour.get(be,PipeSupportBehaviour.TYPE);
        if(b!=null&&b.support!=null){var drops=new java.util.ArrayList<>(cir.getReturnValue());drops.add(new ItemStack(b.support.getBlock()));cir.setReturnValue(drops);}
    }
}
