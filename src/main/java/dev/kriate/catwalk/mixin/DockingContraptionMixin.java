package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.docking.MovingPorts;
import com.simibubi.create.content.contraptions.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=Contraption.class,remap=false)
public abstract class DockingContraptionMixin {
    @Inject(method="removeBlocksFromWorld",at=@At("HEAD"))
    private void capture(Level l,BlockPos offset,CallbackInfo ci){if(net.neoforged.fml.ModList.get().isLoaded("simulated"))MovingPorts.capture((Contraption)(Object)this,l,offset);}
    @Inject(method="addBlocksToWorld",at=@At("HEAD"))
    private void save(Level l,StructureTransform t,CallbackInfo ci){if(net.neoforged.fml.ModList.get().isLoaded("simulated"))MovingPorts.beforeRestore((Contraption)(Object)this,l,t);}
    @Inject(method="addBlocksToWorld",at=@At("RETURN"))
    private void restore(Level l,StructureTransform t,CallbackInfo ci){if(net.neoforged.fml.ModList.get().isLoaded("simulated"))MovingPorts.afterRestore((Contraption)(Object)this,l);}
}
