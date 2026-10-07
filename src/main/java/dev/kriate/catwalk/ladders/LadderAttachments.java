package dev.kriate.catwalk.ladders;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.kriate.catwalk.OptionalMods;
import dev.kriate.catwalk.pipes.PipeSupports;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.*;
public final class LadderAttachments {
    public static final com.simibubi.create.foundation.data.CreateRegistrate REGISTER=dev.kriate.catwalk.MoreFixRegistrate.REGISTER;
    public static com.tterrag.registrate.util.entry.BlockEntityEntry<LadderAttachmentEntity> ENTITY;
    public static void register(net.neoforged.bus.api.IEventBus bus){
        bus.addListener(net.neoforged.bus.api.EventPriority.HIGHEST,(RegisterEvent event)->{
            if(event.getRegistryKey()!=Registries.BLOCK_ENTITY_TYPE)return;
            var builder=REGISTER.blockEntity("ladder_attachments",LadderAttachmentEntity::new);
            for(var block:BuiltInRegistries.BLOCK)if(isLadder(block))builder.validBlock(()->block);
            ENTITY=builder.register();
        });
    }
    public static boolean isLadder(Block block){return block instanceof LadderBlock;}
    public static boolean isRailing(Block block){return OptionalMods.deco()&&block instanceof com.github.talrey.createdeco.blocks.CatwalkRailingBlock;}
    public static LadderAttachmentBehaviour behaviour(BlockGetter world,BlockPos pos){return BlockEntityBehaviour.get(world,pos,LadderAttachmentBehaviour.TYPE);}
    public static InteractionResult cover(BlockItem item,UseOnContext ctx){
        if(!OptionalMods.deco()||ctx.isSecondaryUseActive()||!isLadder(ctx.getLevel().getBlockState(ctx.getClickedPos()).getBlock()))return null;
        boolean support=PipeSupports.isSupport(item.getBlock());if(!support&&!isRailing(item.getBlock()))return null;
        var b=behaviour(ctx.getLevel(),ctx.getClickedPos());if(b==null)return null;
        var player=ctx.getPlayer();if(player==null||!player.mayBuild()||!ctx.getLevel().mayInteract(player,ctx.getClickedPos()))return InteractionResult.FAIL;
        var state=item.getBlock().getStateForPlacement(PipeSupports.inside(ctx,ctx.getClickedPos()));if(state==null)return InteractionResult.FAIL;
        if(support?b.support!=null:b.rails.size()>=4||b.rails.stream().anyMatch(s->s==state))return InteractionResult.FAIL;
        if(!ctx.getLevel().isClientSide){if(support)b.support=state;else b.rails.add(state);b.changed();if(!player.isCreative())ctx.getItemInHand().shrink(1);}
        return InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
    }
}
