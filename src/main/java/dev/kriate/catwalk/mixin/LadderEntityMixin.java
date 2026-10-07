package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.ladders.*;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(value=LadderBlock.class,remap=false)
public abstract class LadderEntityMixin implements IBE<LadderAttachmentEntity> {
    public Class<LadderAttachmentEntity> getBlockEntityClass(){return LadderAttachmentEntity.class;}
    public BlockEntityType<? extends LadderAttachmentEntity> getBlockEntityType(){return LadderAttachments.ENTITY.get();}
}
