package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.PlacementModes;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.InteractionResult;
import dev.kriate.catwalk.CopycatPlacementAccess;

@Mixin(BlockItem.class)
public abstract class ConnectedPlacementMixin {
    @Inject(method="getPlacementState",at=@At("RETURN"),cancellable=true)
    private void morefix$markPlacement(BlockPlaceContext context,CallbackInfoReturnable<BlockState> ci){
        var state=ci.getReturnValue();if(state==null)return;
        boolean connected=PlacementModes.ctrl(context.getPlayer());
        if(connected)state=dev.kriate.catwalk.VanillaConnections.placement(state);
        if(!state.hasProperty(PlacementModes.CONNECTED))return;
        ci.setReturnValue(state.setValue(PlacementModes.CONNECTED,connected));
    }
    @Inject(method="place",at=@At("RETURN"))
    private void morefix$copycatMode(BlockPlaceContext context,CallbackInfoReturnable<InteractionResult> ci){
        if(dev.kriate.catwalk.OptionalMods.copycats()&&ci.getReturnValue().consumesAction()&&PlacementModes.ctrl(context.getPlayer())&&context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CopycatPlacementAccess access&&access instanceof com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity){
            access.morefix$connected(true);
            if(access instanceof com.simibubi.create.foundation.blockEntity.SmartBlockEntity entity)entity.notifyUpdate();
        }
    }
}
