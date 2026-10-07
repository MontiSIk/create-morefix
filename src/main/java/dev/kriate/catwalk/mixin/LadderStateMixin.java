package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.ladders.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(value=BlockBehaviour.BlockStateBase.class,remap=false)
public abstract class LadderStateMixin {
    @Inject(method="useItemOn",at=@At("HEAD"),cancellable=true)
    private void cover(ItemStack stack,net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand,net.minecraft.world.phys.BlockHitResult hit,CallbackInfoReturnable<net.minecraft.world.ItemInteractionResult> ci){
        if(player==null||!(stack.getItem() instanceof BlockItem item))return;
        var result=LadderAttachments.cover(item,new net.minecraft.world.item.context.UseOnContext(player,hand,hit));if(result!=null)ci.setReturnValue(result==net.minecraft.world.InteractionResult.FAIL?net.minecraft.world.ItemInteractionResult.FAIL:net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide));
    }
    @Inject(method={"getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;","getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;","getVisualShape"},at=@At("RETURN"),cancellable=true)
    private void shape(BlockGetter world,BlockPos pos,CollisionContext context,CallbackInfoReturnable<VoxelShape> ci){if(!LadderAttachments.isLadder(((net.minecraft.world.level.block.state.BlockState)(Object)this).getBlock()))return;var b=LadderAttachments.behaviour(world,pos);if(b==null)return;var shape=ci.getReturnValue();for(var part:b.parts())shape=Shapes.or(shape,part.getShape(world,pos,context));ci.setReturnValue(shape);}
    @Inject(method="getDrops",at=@At("RETURN"),cancellable=true)
    private void drops(LootParams.Builder params,CallbackInfoReturnable<java.util.List<ItemStack>> ci){if(!LadderAttachments.isLadder(((net.minecraft.world.level.block.state.BlockState)(Object)this).getBlock()))return;var b=com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour.get(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY),LadderAttachmentBehaviour.TYPE);if(b==null)return;var drops=new java.util.ArrayList<>(ci.getReturnValue());for(var part:b.parts())drops.add(new ItemStack(part.getBlock()));ci.setReturnValue(drops);}
}
