package dev.kriate.catwalk.pipes;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.state.BlockState;

/** Decoration only: native pipe transport, filtering and attachments remain in their original behaviours. */
public final class PipeSupportBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<PipeSupportBehaviour> TYPE=new BehaviourType<>();
    public BlockState support;
    public PipeSupportBehaviour(SmartBlockEntity be){super(be);}
    @Override public BehaviourType<?> getType(){return TYPE;}
    public void setSupport(BlockState state){support=state;blockEntity.notifyUpdate();}
    @Override public boolean isSafeNBT(){return true;}
    @Override public void write(CompoundTag tag,HolderLookup.Provider lookup,boolean client){
        if(support!=null)tag.put("MoreFixPipeSupport",NbtUtils.writeBlockState(support));
    }
    @Override public void read(CompoundTag tag,HolderLookup.Provider lookup,boolean client){
        var state=tag.contains("MoreFixPipeSupport")?NbtUtils.readBlockState(lookup.lookupOrThrow(Registries.BLOCK),tag.getCompound("MoreFixPipeSupport")):null;
        support=state!=null&&PipeSupports.isSupport(state.getBlock())?state:null;
        if(client&&getWorld()!=null){((net.neoforged.neoforge.common.extensions.IBlockEntityExtension)blockEntity).requestModelDataUpdate();var own=blockEntity.getBlockState();getWorld().sendBlockUpdated(getPos(),own,own,3);}
    }
    @Override public com.simibubi.create.content.schematics.requirement.ItemRequirement getRequiredItems(){
        return support==null?com.simibubi.create.content.schematics.requirement.ItemRequirement.NONE:com.simibubi.create.content.schematics.requirement.ItemRequirement.of(support,null);
    }
    public void transform(com.simibubi.create.content.contraptions.StructureTransform transform){if(support!=null)setSupport(transform.apply(support));}
}
