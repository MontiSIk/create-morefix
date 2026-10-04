package dev.kriate.catwalk.mixin;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=CopycatModelCore.class,remap=false)
public abstract class CopycatBarsModelMixin {
    @Inject(method="getModelOf",at=@At("HEAD"),cancellable=true)
    private static void morefix$bars(BlockState material,CallbackInfoReturnable<BakedModel> cir){var model=dev.kriate.catwalk.client.BarMaterials.model(material);var id=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(material.getBlock());if(model==null&&dev.kriate.catwalk.VanillaConnections.PANES.contains(id))model=dev.kriate.catwalk.VanillaConnections.paneModel(id);if(model!=null)cir.setReturnValue(Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(model,"standalone")));}
}

