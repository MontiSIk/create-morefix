package dev.kriate.catwalk.mixin;
import com.copycatsplus.copycats.foundation.copycat.model.neoforge.CopycatModelNeoForge;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
@Mixin(value=CopycatModelNeoForge.class,remap=false)
public abstract class CopycatBarVisibilityMixin {
    @Inject(method="getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;",at=@At("RETURN"),cancellable=true)
    private void morefix$visibleBars(CallbackInfoReturnable<List<BakedQuad>> cir){
        var original=cir.getReturnValue();var repaired=dev.kriate.catwalk.client.BarQuadVisibility.repair(original);
        if(repaired!=original)cir.setReturnValue(repaired);
    }
}
