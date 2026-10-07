package dev.kriate.catwalk.ladders;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
public final class LadderAttachmentEntity extends SmartBlockEntity implements TransformableBlockEntity {
    public LadderAttachmentEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
    public void addBehaviours(java.util.List<BlockEntityBehaviour> behaviours){}
    public void transform(BlockEntity entity,com.simibubi.create.content.contraptions.StructureTransform transform){var b=getBehaviour(LadderAttachmentBehaviour.TYPE);if(b!=null)b.transform(transform);}
}
