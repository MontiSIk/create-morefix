package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.logistics.vault.ItemVaultItem;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(value=ItemVaultItem.class,remap=false)
public abstract class VaultItemMixin {
    @Redirect(method="tryMultiPlace",at=@At(value="INVOKE",target="Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"))
    private BlockPos morefix$layerPlane(BlockPos origin,int x,int y,int z,BlockPlaceContext ctx) {
        return StorageAxes.axis(ctx.getLevel().getBlockState(ctx.getClickedPos()))==Axis.Y?origin.offset(x,z,y):origin.offset(x,y,z);
    }
}
