package dev.kriate.catwalk.mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction.Axis;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=FluidTankRenderer.class,remap=false)
public abstract class TankFluidRendererMixin {
    @Inject(method="renderSafe(Lcom/simibubi/create/content/fluids/tank/FluidTankBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",at=@At("HEAD"),cancellable=true)
    private void morefix$fluid(FluidTankBlockEntity be,float ticks,PoseStack pose,MultiBufferSource buffer,int light,int overlay,CallbackInfo ci) {
        Axis axis=be.getMainConnectionAxis();if(axis==Axis.Y || be.boiler.isActive())return;
        var level=be.getFluidLevel();var fluid=be.getTankInventory().getFluid();
        // Real-world gravity is vertical even when the hull's long axis is horizontal.
        if(be.isController() && !be.boiler.isActive() && level!=null && !fluid.isEmpty() && be.getBlockState().getValue(com.simibubi.create.content.fluids.tank.FluidTankBlock.SHAPE)!=com.simibubi.create.content.fluids.tank.FluidTankBlock.Shape.PLAIN) {
            float w=be.getWidth(),l=be.getHeight(),inset=.0703125f,cap=.25f;
            float xmax=axis==Axis.X?l-cap:w-inset,zmax=axis==Axis.Z?l-cap:w-inset;
            float xmin=axis==Axis.X?cap:inset,zmin=axis==Axis.Z?cap:inset;
            float fill=Math.max(0,Math.min(1,level.getValue(ticks)))*(w-2*inset);
            if(fill>0.001f) {
                float ymin=fluid.getFluidType().isLighterThanAir()?w-inset-fill:inset;
                NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluid,xmin,ymin,zmin,xmax,ymin+fill,zmax,buffer,pose,light,false,true);
            }
        }
        ci.cancel();
    }
}
