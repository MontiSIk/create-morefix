package dev.kriate.catwalk.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Pseudo
@Mixin(targets="com.railwayteam.railways.content.palettes.boiler.BoilerBlock",remap=false)
public abstract class LocometalBoilerMixin {
    @Shadow @Final @Mutable public static EnumProperty<Direction.Axis> HORIZONTAL_AXIS;
    @Shadow public abstract VoxelShape getShapeForState(BlockState state);

    @Inject(method="<clinit>",at=@At("RETURN"))
    private static void morefix$allAxes(CallbackInfo ci){HORIZONTAL_AXIS=BlockStateProperties.AXIS;}

    @Inject(method="rotate",at=@At("HEAD"),cancellable=true)
    private void morefix$structureRotation(BlockState state,Rotation rotation,CallbackInfoReturnable<BlockState> cir){
        var axis=state.getValue(HORIZONTAL_AXIS);
        if(axis==Direction.Axis.Y||rotation==Rotation.NONE||rotation==Rotation.CLOCKWISE_180)cir.setReturnValue(state);
        else cir.setReturnValue(state.setValue(HORIZONTAL_AXIS,axis==Direction.Axis.X?Direction.Axis.Z:Direction.Axis.X));
    }

    @Inject(method="getShapeForState",at=@At("HEAD"),cancellable=true)
    private void morefix$verticalShape(BlockState state,CallbackInfoReturnable<VoxelShape> cir){
        if(state.getValue(HORIZONTAL_AXIS)!=Direction.Axis.Y)return;
        var source=getShapeForState(state.setValue(HORIZONTAL_AXIS,Direction.Axis.Z));
        VoxelShape[] rotated={Shapes.empty()};
        source.forAllBoxes((x0,y0,z0,x1,y1,z1)->rotated[0]=Shapes.or(rotated[0],Shapes.box(x0,z0,1-y1,x1,z1,1-y0)));
        cir.setReturnValue(rotated[0].optimize());
    }
}
