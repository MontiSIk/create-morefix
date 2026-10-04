package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.kriate.catwalk.client.StorageTextureFrame;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

@Mixin(value=FluidTankCTBehaviour.class,remap=false)
public abstract class StorageTextureMixin extends HorizontalCTBehaviour {
    protected StorageTextureMixin(){super(null,null);}
    @ModifyVariable(method="getShift",at=@At("HEAD"),argsOnly=true)
    private Direction morefix$canonicalFace(Direction direction,BlockState state,Direction originalFace,TextureAtlasSprite sprite) {
        return StorageTextureFrame.tankToLocal(StorageAxes.axis(state),direction);
    }
    @Override protected Direction getUpDirection(BlockAndTintGetter reader,BlockPos pos,BlockState state,Direction face) {
        Axis axis=StorageAxes.axis(state);
        Direction localFace=StorageTextureFrame.tankToLocal(axis,face);
        Direction up=StorageTextureFrame.tankToWorld(axis,super.getUpDirection(reader,pos,state,localFace));
        return (face==Direction.DOWN)!=(localFace==Direction.DOWN)?up.getOpposite():up;
    }
    @Override protected Direction getRightDirection(BlockAndTintGetter reader,BlockPos pos,BlockState state,Direction face) {
        Axis axis=StorageAxes.axis(state);
        Direction localFace=StorageTextureFrame.tankToLocal(axis,face);
        Direction right=StorageTextureFrame.tankToWorld(axis,super.getRightDirection(reader,pos,state,localFace));
        boolean oppositeSign=face.getAxisDirection()!=localFace.getAxisDirection();
        boolean oppositeDown=(face==Direction.DOWN)!=(localFace==Direction.DOWN);
        return oppositeSign!=oppositeDown?right.getOpposite():right;
    }
}
