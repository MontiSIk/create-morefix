package dev.kriate.catwalk.pipes;

import com.github.talrey.createdeco.blocks.*;
import com.simibubi.create.content.fluids.pipes.*;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class PipeSupports {
    private PipeSupports() {}
    public static boolean isSupport(Block block){return dev.kriate.catwalk.OptionalMods.deco()&&(block instanceof SupportBlock||block instanceof SupportWedgeBlock);}
    public static boolean isPipe(Block block){
        if(!(block instanceof FluidPipeBlock||block instanceof AxisPipeBlock||block instanceof EncasedPipeBlock))return false;
        var namespace=BuiltInRegistries.BLOCK.getKey(block).getNamespace();return namespace.equals("create")||namespace.equals("createcasing")||namespace.equals("copycats");
    }
    public static PipeSupportBehaviour behaviour(BlockGetter world,BlockPos pos){return BlockEntityBehaviour.get(world,pos,PipeSupportBehaviour.TYPE);}
    public static BlockState support(BlockGetter world,BlockPos pos){var b=behaviour(world,pos);return b==null?null:b.support;}
    /** Retains the exact clicked cell while letting native BlockItem placement perform its normal checks. */
    public static BlockPlaceContext inside(UseOnContext ctx,BlockPos pos){return new BlockPlaceContext(ctx){
        {replaceClicked=true;}
        @Override public BlockPos getClickedPos(){return pos;}
        @Override public boolean canPlace(){return true;}
        @Override public boolean replacingClickedOnBlock(){return true;}
    };}
    public static InteractionResult cover(BlockItem item,UseOnContext ctx){
        if(!isSupport(item.getBlock())||ctx.isSecondaryUseActive())return null;
        var world=ctx.getLevel();var pos=ctx.getClickedPos();if(!isPipe(world.getBlockState(pos).getBlock()))return null;
        var b=behaviour(world,pos);if(b==null||b.support!=null)return null;
        var player=ctx.getPlayer();if(player!=null&&(!player.mayBuild()||!world.mayInteract(player,pos)))return InteractionResult.FAIL;
        var support=item.getBlock().getStateForPlacement(inside(ctx,pos));if(support==null)return InteractionResult.FAIL;
        if(!world.isClientSide){b.setSupport(support);if(player==null||!player.isCreative())ctx.getItemInHand().shrink(1);
            world.playSound(null,pos,support.getSoundType().getPlaceSound(),net.minecraft.sounds.SoundSource.BLOCKS,.8f,1);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
}
