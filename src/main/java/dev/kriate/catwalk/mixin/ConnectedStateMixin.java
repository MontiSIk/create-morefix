package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.PlacementModes;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateDefinition.Builder.class)
public abstract class ConnectedStateMixin {
    @Shadow @Final private Object owner;
    @Inject(method="create",at=@At("HEAD"))
    private void morefix$placementProperty(CallbackInfoReturnable<?> ci){
        if(owner instanceof Block&&PlacementModes.CONSTRUCTING.get())((StateDefinition.Builder)(Object)this).add(PlacementModes.CONNECTED);
    }
}
