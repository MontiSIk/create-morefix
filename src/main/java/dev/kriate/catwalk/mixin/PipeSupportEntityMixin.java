package dev.kriate.catwalk.mixin;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import dev.kriate.catwalk.pipes.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=SmartBlockEntity.class,remap=false)
public abstract class PipeSupportEntityMixin {
    @Shadow @Final private java.util.Map<BehaviourType<?>,BlockEntityBehaviour> behaviours;
    @Inject(method="<init>",at=@At("TAIL"))
    private void morefix$support(BlockEntityType<?> type,BlockPos pos,BlockState state,CallbackInfo ci){
        if(PipeSupports.isPipe(state.getBlock()))behaviours.put(PipeSupportBehaviour.TYPE,new PipeSupportBehaviour((SmartBlockEntity)(Object)this));
    }
}
