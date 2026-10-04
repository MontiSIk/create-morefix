package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(value=FluidTankBlockEntity.class,remap=false)
public interface TankLuminosityAccessor {
    @Invoker("setLuminosity") void morefix$setLuminosity(int value);
}
