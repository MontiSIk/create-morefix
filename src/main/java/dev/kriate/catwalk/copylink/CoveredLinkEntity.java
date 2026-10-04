package dev.kriate.catwalk.copylink;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.simibubi.create.content.redstone.link.*;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
public final class CoveredLinkEntity extends CCCopycatBlockEntity {
    private LinkBehaviour link;
    private int sent,received;
    private boolean receivedChanged;
    public boolean dropShell=true;
    public CoveredLinkEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
    @Override public void addBehaviours(List<BlockEntityBehaviour> behaviours){
        var slots=ValueBoxTransform.Dual.makeSlots(RedstoneLinkFrequencySlot::new);
        link=getBlockState().getValue(RedstoneLinkBlock.RECEIVER)?LinkBehaviour.receiver(this,slots,this::receive):LinkBehaviour.transmitter(this,slots,()->sent);
        behaviours.add(link);
    }
    private void receive(int strength){
        if(received==strength)return; received=strength;
        // Network callbacks also run during chunk unload; notify neighbours only in a live tick.
        receivedChanged=true;
    }
    public LinkBehaviour wireless(){return link;}
    public int received(){return received;}
    @Override public void tick(){
        super.tick(); if(level==null || isRemoved())return;
        boolean receiver=getBlockState().getValue(RedstoneLinkBlock.RECEIVER);
        if(link.isListening()!=receiver){
            var previous=link;removeBehaviour(LinkBehaviour.TYPE);
            var slots=ValueBoxTransform.Dual.makeSlots(RedstoneLinkFrequencySlot::new);
            link=receiver?LinkBehaviour.receiver(this,slots,this::receive):LinkBehaviour.transmitter(this,slots,()->sent);
            link.copyItemsFrom(previous);received=0;sent=0;attachBehaviourLate(link);if(!level.isClientSide){level.updateNeighborsAt(worldPosition,getBlockState().getBlock());notifyUpdate();}
        }
        if(level.isClientSide)return;
        if(receivedChanged){receivedChanged=false;level.updateNeighborsAt(worldPosition,getBlockState().getBlock());notifyUpdate();}
        if(!receiver){int power=level.getBestNeighborSignal(worldPosition);for(Direction side:Direction.values())if(side!=getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING).getOpposite())power=Math.max(power,level.getSignal(worldPosition.relative(side),Direction.UP));if(power!=sent){sent=power;link.notifySignalChange();notifyUpdate();}}
    }
    @Override public void write(CompoundTag tag,HolderLookup.Provider registries,boolean client){super.write(tag,registries,client);tag.putInt("MoreFixReceived",received);tag.putInt("MoreFixSent",sent);}
    @Override public void read(CompoundTag tag,HolderLookup.Provider registries,boolean client){sent=tag.getInt("MoreFixSent");received=tag.getInt("MoreFixReceived");super.read(tag,registries,client);}
}


