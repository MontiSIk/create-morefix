package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import dev.kriate.catwalk.GantryAxes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=GantryCarriageBlock.class,remap=false)
public abstract class GantryCarriageMixin extends DirectionalAxisKineticBlock {
    protected GantryCarriageMixin(Properties properties){super(properties);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        super.createBlockStateDefinition(builder);builder.add(GantryAxes.SHAFT_ALONG_MOUNT);
    }
    @Inject(method="<init>",at=@At("TAIL"))
    private void morefix$default(Properties properties,CallbackInfo ci){registerDefaultState(defaultBlockState().setValue(GantryAxes.SHAFT_ALONG_MOUNT,false));}
    @Override public Direction.Axis getRotationAxis(BlockState state){
        return state.getValue(GantryAxes.SHAFT_ALONG_MOUNT)?state.getValue(FACING).getAxis():super.getRotationAxis(state);
    }
    @Inject(method="getValidGantryShaftAxis",at=@At("HEAD"),cancellable=true)
    private static void morefix$rail(BlockState state,CallbackInfoReturnable<Direction.Axis> cir){
        if(!(state.getBlock() instanceof GantryCarriageBlock)||!state.getValue(GantryAxes.SHAFT_ALONG_MOUNT))return;
        var mount=state.getValue(FACING).getAxis();var pinion=GantryAxes.pinionAxis(state);
        for(var axis:Direction.Axis.values())if(axis!=mount&&axis!=pinion){cir.setReturnValue(axis);return;}
    }
}
