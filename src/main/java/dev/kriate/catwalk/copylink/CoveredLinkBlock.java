package dev.kriate.catwalk.copylink;
import com.copycatsplus.copycats.CCBlocks;
import com.copycatsplus.copycats.content.copycat.block.CopycatBlockBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.link.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
public final class CoveredLinkBlock extends CopycatBlockBlock implements com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement {
    @Override public com.simibubi.create.content.schematics.requirement.ItemRequirement getRequiredItems(BlockState state,BlockEntity entity){
        return new com.simibubi.create.content.schematics.requirement.ItemRequirement(com.simibubi.create.content.schematics.requirement.ItemRequirement.ItemUseType.CONSUME,java.util.List.of(new ItemStack(AllBlocks.REDSTONE_LINK.get()),new ItemStack(CCBlocks.COPYCAT_BLOCK.get())));
    }
    public CoveredLinkBlock(Properties properties){super(properties);registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING,Direction.UP).setValue(RedstoneLinkBlock.RECEIVER,false));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){super.createBlockStateDefinition(builder);builder.add(BlockStateProperties.FACING,RedstoneLinkBlock.RECEIVER);}
    @Override public Class<CCCopycatBlockEntity> getBlockEntityClass(){return CCCopycatBlockEntity.class;}
    @Override public BlockEntityType<? extends CCCopycatBlockEntity> getBlockEntityType(){return CopyLinks.ENTITY.get();}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return type==CopyLinks.ENTITY.get()?(l,p,s,be)->((CoveredLinkEntity)be).tick():null;}
    @Override public boolean isSignalSource(BlockState state){return state.getValue(RedstoneLinkBlock.RECEIVER);}
    @Override public int getSignal(BlockState state,BlockGetter level,BlockPos pos,Direction side){return state.getValue(RedstoneLinkBlock.RECEIVER)&&level.getBlockEntity(pos) instanceof CoveredLinkEntity be?be.received():0;}
    @Override public int getDirectSignal(BlockState state,BlockGetter level,BlockPos pos,Direction side){return side==state.getValue(BlockStateProperties.FACING)?getSignal(state,level,pos,side):0;}
    public boolean canConnectRedstone(BlockState state,BlockGetter level,BlockPos pos,Direction side){return side!=null;}
    @Override public InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        if(player.isShiftKeyDown()){if(!level.isClientSide)level.setBlock(pos,state.cycle(RedstoneLinkBlock.RECEIVER),3);return InteractionResult.SUCCESS;}
        return super.useWithoutItem(state,level,pos,player,hit);
    }
    @Override public InteractionResult onSneakWrenched(BlockState state,UseOnContext ctx){
        if(!(ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof CoveredLinkEntity be))return InteractionResult.PASS;
        if(be.hasCustomMaterial())return super.onWrenched(state,ctx);
        if(!ctx.getLevel().isClientSide){
            var old=be.wireless();be.dropShell=false;
            ctx.getLevel().setBlock(ctx.getClickedPos(),AllBlocks.REDSTONE_LINK.getDefaultState().setValue(BlockStateProperties.FACING,state.getValue(BlockStateProperties.FACING)).setValue(RedstoneLinkBlock.RECEIVER,state.getValue(RedstoneLinkBlock.RECEIVER)),3);
            if(ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof RedstoneLinkBlockEntity restored){var frequencies=new net.minecraft.nbt.CompoundTag();old.write(frequencies,ctx.getLevel().registryAccess(),false);frequencies.putBoolean("Transmitter",!state.getValue(RedstoneLinkBlock.RECEIVER));restored.loadWithComponents(frequencies,ctx.getLevel().registryAccess());restored.tick();restored.notifyUpdate();}
            if(ctx.getPlayer()!=null && !ctx.getPlayer().isCreative())Block.popResource(ctx.getLevel(),ctx.getClickedPos(),new ItemStack(CCBlocks.COPYCAT_BLOCK.get()));
        }
        return InteractionResult.SUCCESS;
    }
    @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player){if(player.isCreative()&&level.getBlockEntity(pos) instanceof CoveredLinkEntity be)be.dropShell=false;return super.playerWillDestroy(level,pos,state,player);}
    @Override public void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(state.getBlock()!=next.getBlock()&&!moving&&level.getBlockEntity(pos) instanceof CoveredLinkEntity be&&be.dropShell)Block.popResource(level,pos,new ItemStack(CCBlocks.COPYCAT_BLOCK.get()));
        super.onRemove(state,level,pos,next,moving);
    }
}





