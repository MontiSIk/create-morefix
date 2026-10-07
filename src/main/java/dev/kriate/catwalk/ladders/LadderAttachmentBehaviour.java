package dev.kriate.catwalk.ladders;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;
public final class LadderAttachmentBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<LadderAttachmentBehaviour> TYPE=new BehaviourType<>();
    public BlockState support;public final List<BlockState> rails=new ArrayList<>();
    public LadderAttachmentBehaviour(SmartBlockEntity entity){super(entity);}
    public BehaviourType<?> getType(){return TYPE;}
    @Override public boolean isSafeNBT(){return true;}
    public List<BlockState> parts(){var result=new ArrayList<>(rails);if(support!=null)result.add(support);return result;}
    public void changed(){blockEntity.notifyUpdate();}
    @Override public void write(CompoundTag tag,HolderLookup.Provider lookup,boolean client){
        if(support!=null)tag.put("MoreFixLadderSupport",NbtUtils.writeBlockState(support));
        var list=new ListTag();for(var rail:rails)list.add(NbtUtils.writeBlockState(rail));if(!list.isEmpty())tag.put("MoreFixLadderRails",list);
    }
    @Override public void read(CompoundTag tag,HolderLookup.Provider lookup,boolean client){
        var registry=lookup.lookupOrThrow(Registries.BLOCK);support=null;rails.clear();
        if(tag.contains("MoreFixLadderSupport")){var state=NbtUtils.readBlockState(registry,tag.getCompound("MoreFixLadderSupport"));if(dev.kriate.catwalk.pipes.PipeSupports.isSupport(state.getBlock()))support=state;}
        for(var entry:tag.getList("MoreFixLadderRails",Tag.TAG_COMPOUND)){var state=NbtUtils.readBlockState(registry,(CompoundTag)entry);if(LadderAttachments.isRailing(state.getBlock())&&rails.size()<4&&!rails.contains(state))rails.add(state);}
        if(client&&getWorld()!=null){((net.neoforged.neoforge.common.extensions.IBlockEntityExtension)blockEntity).requestModelDataUpdate();var state=blockEntity.getBlockState();getWorld().sendBlockUpdated(getPos(),state,state,3);}
    }
    @Override public com.simibubi.create.content.schematics.requirement.ItemRequirement getRequiredItems(){
        return new com.simibubi.create.content.schematics.requirement.ItemRequirement(com.simibubi.create.content.schematics.requirement.ItemRequirement.ItemUseType.CONSUME,parts().stream().map(s->new net.minecraft.world.item.ItemStack(s.getBlock())).toList());
    }
    public void transform(StructureTransform transform){if(support!=null)support=transform.apply(support);for(int i=0;i<rails.size();i++)rails.set(i,transform.apply(rails.get(i)));changed();}
}
