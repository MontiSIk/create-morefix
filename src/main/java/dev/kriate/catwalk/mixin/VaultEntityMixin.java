package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=ItemVaultBlockEntity.class,remap=false)
public abstract class VaultEntityMixin {
    private ItemVaultBlockEntity morefix$self() {return (ItemVaultBlockEntity)(Object)this;}
    @Inject(method="getMainConnectionAxis",at=@At("HEAD"),cancellable=true)
    private void morefix$axis(CallbackInfoReturnable<Axis> cir) {cir.setReturnValue(StorageAxes.axis(morefix$self().getBlockState()));}
    @Inject(method="getMaxLength(Lnet/minecraft/core/Direction$Axis;I)I",at=@At("HEAD"),cancellable=true)
    private void morefix$length(Axis axis,int width,CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(axis==morefix$self().getMainConnectionAxis()?ItemVaultBlockEntity.getMaxLength(width):morefix$self().getMaxWidth());
    }
    @Redirect(method="initCapability",at=@At(value="INVOKE",target="Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"))
    private BlockPos morefix$inventoryPosition(BlockPos origin,int x,int y,int z) {
        return morefix$self().getMainConnectionAxis()==Axis.Y?origin.offset(y,x,z):origin.offset(x,y,z);
    }
    @Inject(method="updateComparators",at=@At("HEAD"),cancellable=true)
    private void morefix$comparators(CallbackInfo ci) {
        var controller=morefix$self().getControllerBE();
        if(controller==null || controller.getMainConnectionAxis()!=Axis.Y) return;
        var level=controller.getLevel(); var origin=controller.getBlockPos();
        level.blockEntityChanged(origin);
        for(int y=0;y<controller.getHeight();y++) for(int x=0;x<controller.getWidth();x++) for(int z=0;z<controller.getWidth();z++) {
            var pos=origin.offset(x,y,z);
            if(level.hasChunkAt(pos)) level.updateNeighbourForOutputSignal(pos,level.getBlockState(pos).getBlock());
        }
        ci.cancel();
    }
}
