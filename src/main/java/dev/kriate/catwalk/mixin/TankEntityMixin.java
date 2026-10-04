package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=FluidTankBlockEntity.class,remap=false)
public abstract class TankEntityMixin {
    private FluidTankBlockEntity morefix$self() {return (FluidTankBlockEntity)(Object)this;}
    @Inject(method="getMainConnectionAxis",at=@At("HEAD"),cancellable=true)
    private void morefix$axis(CallbackInfoReturnable<Axis> cir) {cir.setReturnValue(StorageAxes.axis(morefix$self().getBlockState()));}
    @Inject(method="getMaxLength",at=@At("HEAD"),cancellable=true)
    private void morefix$length(Axis axis,int width,CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(axis==morefix$self().getMainConnectionAxis()?FluidTankBlockEntity.getMaxHeight():morefix$self().getMaxWidth());
    }
    @Redirect(method={"setWindows","updateBoilerState","onFluidStackChanged"},at=@At(value="INVOKE",target="Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"))
    private BlockPos morefix$parts(BlockPos origin,int x,int y,int z) {return StorageAxes.offset(origin,morefix$self().getMainConnectionAxis(),x,y,z);}
    @Redirect(method="onFluidStackChanged",at=@At(value="INVOKE",target="Lcom/simibubi/create/content/fluids/tank/FluidTankBlockEntity;setLuminosity(I)V"))
    private void morefix$gravityLight(FluidTankBlockEntity part,int value,net.neoforged.neoforge.fluids.FluidStack fluid) {
        var self=morefix$self();
        if(self.getMainConnectionAxis()!=Axis.Y) {
            int luminosity=(int)(fluid.getFluidType().getLightLevel(fluid)/1.2f);
            float fill=(float)self.getTankInventory().getFluidAmount()/Math.max(1,self.getTankInventory().getCapacity());
            int maxY=(int)(fill*self.getWidth()+1),y=part.getBlockPos().getY()-self.getBlockPos().getY();
            boolean bright=fluid.getFluidType().isLighterThanAir()?self.getWidth()-y<=maxY:y<maxY;
            value=bright?luminosity:luminosity>0?1:0;
        }
        ((TankLuminosityAccessor)part).morefix$setLuminosity(value);
    }
    @Redirect(method="setWindows",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private Object morefix$windowCorners(BlockState state,Property property,Comparable value) {
        if(property==FluidTankBlock.SHAPE && value instanceof FluidTankBlock.Shape shape) {
            Axis axis=morefix$self().getMainConnectionAxis();
            if(axis==Axis.X)value=switch(shape) {case WINDOW_SW->FluidTankBlock.Shape.WINDOW_NE;case WINDOW_NE->FluidTankBlock.Shape.WINDOW_SW;default->shape;};
            if(axis==Axis.Z)value=switch(shape) {case WINDOW_NW->FluidTankBlock.Shape.WINDOW_SW;case WINDOW_SW->FluidTankBlock.Shape.WINDOW_NW;case WINDOW_NE->FluidTankBlock.Shape.WINDOW_SE;case WINDOW_SE->FluidTankBlock.Shape.WINDOW_NE;default->shape;};
        }
        return state.setValue(property,value);
    }
    @Inject(method="notifyMultiUpdated",at=@At("TAIL"))
    private void morefix$caps(CallbackInfo ci) {
        var self=morefix$self(); var axis=self.getMainConnectionAxis();
        if(axis==Axis.Y) return;
        var state=self.getBlockState(); int start=self.getController().get(axis); int coordinate=self.getBlockPos().get(axis);
        self.getLevel().setBlock(self.getBlockPos(),state.setValue(FluidTankBlock.BOTTOM,start==coordinate).setValue(FluidTankBlock.TOP,start+self.getHeight()-1==coordinate),6);
    }
    @Inject(method="createRenderBoundingBox",at=@At("HEAD"),cancellable=true)
    private void morefix$bounds(CallbackInfoReturnable<AABB> cir) {
        var self=morefix$self(); if(!self.isController()) return;
        Axis axis=self.getMainConnectionAxis();int w=self.getWidth(),l=self.getHeight();
        cir.setReturnValue(new AABB(self.getBlockPos()).expandTowards(StorageAxes.size(axis,Axis.X,w,l)-1,StorageAxes.size(axis,Axis.Y,w,l)-1,StorageAxes.size(axis,Axis.Z,w,l)-1));
    }
}
