package dev.kriate.catwalk.mixin;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.items.RailingBlockItem;
import dev.kriate.catwalk.EmbeddedRailings;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=RailingBlockItem.class,remap=false)
public abstract class RailingItemMixin {
    @Inject(method="useOn",at=@At("HEAD"),cancellable=true)
    private void attach(UseOnContext context,CallbackInfoReturnable<InteractionResult> cir) {
        var level=context.getLevel(); var pos=context.getClickedPos(); var state=level.getBlockState(pos);
        if(!(state.getBlock() instanceof CatwalkBlock)) return;
        var player=context.getPlayer();
        if(player!=null && (!player.mayBuild() || !level.mayInteract(player,pos))) {
            cir.setReturnValue(InteractionResult.FAIL); return;
        }
        BlockState attached=EmbeddedRailings.attach(state,context.getItemInHand(),pos,context.getClickLocation());
        if(attached==null) {cir.setReturnValue(InteractionResult.FAIL);return;}
        if(!level.isClientSide) {
            level.setBlock(pos,attached,3);
            if(player==null || !player.getAbilities().instabuild) context.getItemInHand().shrink(1);
            var sound=attached.getSoundType();
            level.playSound(null,pos,sound.getPlaceSound(),net.minecraft.sounds.SoundSource.BLOCKS,1,1);
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
