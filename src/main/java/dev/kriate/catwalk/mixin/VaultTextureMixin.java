package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.logistics.vault.ItemVaultCTBehaviour;
import dev.kriate.catwalk.StorageAxes;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.*;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=ItemVaultCTBehaviour.class,remap=false)
public abstract class VaultTextureMixin {
    @Inject(method="getShift",at=@At("HEAD"),cancellable=true)
    private void morefix$uniformSides(BlockState state,Direction face,TextureAtlasSprite sprite,CallbackInfoReturnable<CTSpriteShiftEntry> cir) {
        if(StorageAxes.axis(state)==Axis.Y) {
            boolean small=!ItemVaultBlock.isLarge(state);
            cir.setReturnValue((face.getAxis()==Axis.Y?AllSpriteShifts.VAULT_FRONT:AllSpriteShifts.VAULT_SIDE).get(small));
        }
    }
    @Inject(method="getUpDirection",at=@At("HEAD"),cancellable=true)
    private void morefix$up(BlockAndTintGetter reader,BlockPos pos,BlockState state,Direction face,CallbackInfoReturnable<Direction> cir) {
        if(StorageAxes.axis(state)==Axis.Y)cir.setReturnValue(face.getAxis().isHorizontal()?Direction.UP:Direction.NORTH);
    }
    @Inject(method="getRightDirection",at=@At("HEAD"),cancellable=true)
    private void morefix$right(BlockAndTintGetter reader,BlockPos pos,BlockState state,Direction face,CallbackInfoReturnable<Direction> cir) {
        if(StorageAxes.axis(state)==Axis.Y)cir.setReturnValue(face.getAxis()==Axis.X?Direction.SOUTH:Direction.WEST);
    }
}
