package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.client.MaterialConnections;
import dev.kriate.catwalk.PlacementModes;
import com.simibubi.create.foundation.model.ModelSwapper;
import com.simibubi.create.foundation.block.connected.CTModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=ModelSwapper.class,remap=false)
public abstract class MaterialModelBakeMixin {
    @Inject(method="onModelBake",at=@At("RETURN"))
    private void morefix$afterCreate(net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult event,CallbackInfo ci){
        for(var block:PlacementModes.blocks()){
            for(var state:block.getStateDefinition().getPossibleStates()){
                var key=net.minecraft.client.renderer.block.BlockModelShaper.stateToModelLocation(state);var original=event.getModels().get(key);
                if(original!=null&&!(original instanceof CTModel))event.getModels().put(key,new CTModel(original,new MaterialConnections()));
            }
        }
    }
}
