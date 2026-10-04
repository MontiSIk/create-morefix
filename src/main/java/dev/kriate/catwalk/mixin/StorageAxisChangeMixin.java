package dev.kriate.catwalk.mixin;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=Level.class,remap=false)
public abstract class StorageAxisChangeMixin {
    // Split while the old state is still installed, before LevelChunk starts replacing it.
    @Inject(method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",at=@At("HEAD"))
    private void morefix$beforeAxisChange(BlockPos pos,BlockState next,int flags,int depth,CallbackInfoReturnable<Boolean> cir) {
        var level=(Level)(Object)this;if(level.isClientSide)return;
        var old=level.getBlockState(pos);
        if(old.getBlock()!=next.getBlock() || StorageAxes.axis(old)==StorageAxes.axis(next))return;
        var be=level.getBlockEntity(pos);
        if(be instanceof FluidTankBlockEntity tank) {ConnectivityHandler.splitMulti(tank);tank.removeController(true);}
        else if(be instanceof ItemVaultBlockEntity vault) {ConnectivityHandler.splitMulti(vault);vault.removeController(true);}
    }
}
