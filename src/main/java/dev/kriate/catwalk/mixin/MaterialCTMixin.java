package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.client.MaterialConnections;
import com.simibubi.create.foundation.block.connected.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=CTModel.class,remap=false)
public abstract class MaterialCTMixin {
    @Shadow @Final @Mutable private ConnectedTextureBehaviour behaviour;
    @Inject(method="createCTData",at=@At("HEAD"))
    private void morefix$controlled(BlockAndTintGetter world,BlockPos pos,BlockState state,CallbackInfoReturnable<?> ci){if(MaterialConnections.applies(state)&&!(behaviour instanceof MaterialConnections))behaviour=new MaterialConnections();}
}
