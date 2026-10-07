package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.ladders.*;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=StructureTransform.class,remap=false)
public abstract class LadderTransformMixin {
    @Inject(method="apply(Lnet/minecraft/world/level/block/entity/BlockEntity;)V",at=@At("RETURN"))
    private void attachments(BlockEntity entity,CallbackInfo ci){
        // Native Copycats transforms its material through an interface default method.
        // Our own entity already transforms decorations through the native callback.
        if(entity instanceof LadderAttachmentEntity)return;
        var b=BlockEntityBehaviour.get(entity,LadderAttachmentBehaviour.TYPE);
        if(b!=null)b.transform((StructureTransform)(Object)this);
    }
}
