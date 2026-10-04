package dev.kriate.catwalk.mixin;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.items.CatwalkBlockItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CatwalkBlockItem.class, remap = false)
public abstract class CatwalkBlockItemMixin extends BlockItem {
    protected CatwalkBlockItemMixin(Block block, Item.Properties properties) { super(block, properties); }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void catwalkOrientation$normalPlacement(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        // Deco's helper searches only a horizontal plane. Normal placement works
        // for vertical sheets and copies the adjacent catwalk's orientation.
        if (context.getPlayer() != null && context.getPlayer().isSecondaryUseActive()) {
            cir.setReturnValue(super.useOn(context));
        }
    }
}
