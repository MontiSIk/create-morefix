package dev.kriate.catwalk.mixin;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Pseudo
@Mixin(targets={"com.simibubi.create.content.fluids.tank.FluidTankModel","fr.iglee42.createcasing.fluids.EncasedFluidTankModel"},remap=false)
public abstract class TankModelMixin extends com.simibubi.create.foundation.block.connected.CTModel {
    protected TankModelMixin(net.minecraft.client.resources.model.BakedModel original,com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour behaviour){super(original,behaviour);}
    @Override public net.neoforged.neoforge.common.util.TriState useAmbientOcclusion(
        net.minecraft.world.level.block.state.BlockState state,
        net.neoforged.neoforge.client.model.data.ModelData data,
        net.minecraft.client.renderer.RenderType type) {
        // Rotated tank shells include inset/back-facing faces; their AO samples
        // the support blocks under the tank and produces diagonal dark patches.
        // Flat lighting still applies directional shading and world/block light.
        if(state!=null && dev.kriate.catwalk.StorageAxes.axis(state)!=Direction.Axis.Y)
            return net.neoforged.neoforge.common.util.TriState.FALSE;
        return super.useAmbientOcclusion(state,data,type);
    }
    @Redirect(method="gatherModelData",at=@At(value="FIELD",target="Lnet/createmod/catnip/data/Iterate;horizontalDirections:[Lnet/minecraft/core/Direction;"))
    private Direction[] morefix$sixSides() {return Direction.values();}
}

