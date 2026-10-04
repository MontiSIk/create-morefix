package dev.kriate.catwalk.pipes;

import com.github.talrey.createdeco.blocks.SupportWedgeBlock;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;

/** Changes decoration only; the native pipe state and transport entity are never replaced. */
public final class SupportRotation {
    private SupportRotation() {}
    public static BlockState turn(BlockState state,Direction face){
        if(state.hasProperty(SupportWedgeBlock.ORIENTATION))return state.cycle(SupportWedgeBlock.ORIENTATION);
        var direction=state.getValue(BlockStateProperties.FACING);
        var next=direction.getAxis()==face.getAxis()?(face.getAxis()==Direction.Axis.X?Direction.NORTH:Direction.EAST):direction.getClockWise(face.getAxis());
        return state.setValue(BlockStateProperties.FACING,next);
    }
    public static void submit(ServerPlayer player,BlockPos pos,BlockState desired){
        var world=player.level();var b=PipeSupports.behaviour(world,pos);
        if(b==null||b.support==null||!desired.is(b.support.getBlock())||!player.mayBuild()||!world.mayInteract(player,pos)||!player.canInteractWithBlock(pos,0))return;
        if(!player.getMainHandItem().is(AllItems.WRENCH.get())&&!player.getOffhandItem().is(AllItems.WRENCH.get()))return;
        var next=b.support;
        for(var property:next.getProperties())if(property instanceof DirectionProperty||property.equals(SupportWedgeBlock.ORIENTATION))next=copy(next,desired,property);
        if(next!=b.support){b.setSupport(next);IWrenchable.playRotateSound(world,pos);}
    }
    private static <T extends Comparable<T>> BlockState copy(BlockState state,BlockState desired,Property<T> property){return state.setValue(property,desired.getValue(property));}
}
