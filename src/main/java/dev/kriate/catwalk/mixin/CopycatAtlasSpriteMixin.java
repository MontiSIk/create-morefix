package dev.kriate.catwalk.mixin;

import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/** Edge sprites have x/u0 or y/v0 = 0/0; derive atlas dimensions from the UV span instead. */
@Mixin(value=MultiStateTextureAtlasSprite.class,remap=false)
public abstract class CopycatAtlasSpriteMixin {
    @ModifyArgs(method="<init>",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/texture/SpriteContents;IIII)V"))
    private static void morefix$atlasSize(Args args,String property,TextureAtlasSprite source){
        float width=source.getU1()-source.getU0(),height=source.getV1()-source.getV0();
        if(Float.isFinite(width)&&width>0)args.set(2,Math.round(source.contents().width()/width));
        if(Float.isFinite(height)&&height>0)args.set(3,Math.round(source.contents().height()/height));
    }
}
