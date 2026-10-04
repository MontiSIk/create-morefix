package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.docking.MovingPorts;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Pseudo
@Mixin(targets="dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorTank",remap=false)
public abstract class DockingTankMixin {
    @Redirect(method="canInteract",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity lookup(Level l,BlockPos p){return MovingPorts.resolve(l,p);}
}
