package dev.kriate.catwalk.mixin;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(BufferBuilder.class)
public interface ForgematicaBufferAccess {
    @Accessor(value="building",remap=false) boolean morefix$building();
}
