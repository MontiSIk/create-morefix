package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.ladders.*;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=SmartBlockEntity.class,remap=false)
public abstract class LadderBehaviourMixin {
    @Shadow @Final private java.util.Map<BehaviourType<?>,BlockEntityBehaviour> behaviours;
    @Inject(method="<init>",at=@At("TAIL"))
    private void attachments(BlockEntityType<?> type,BlockPos pos,BlockState state,CallbackInfo ci){if(LadderAttachments.isLadder(state.getBlock()))behaviours.put(LadderAttachmentBehaviour.TYPE,new LadderAttachmentBehaviour((SmartBlockEntity)(Object)this));}
}
