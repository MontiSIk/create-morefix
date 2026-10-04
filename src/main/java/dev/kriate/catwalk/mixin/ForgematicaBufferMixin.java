package dev.kriate.catwalk.mixin;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Pseudo @Mixin(targets="fi.dy.masa.litematica.render.schematic.BufferBuilderCache",remap=false)
public abstract class ForgematicaBufferMixin {
    @Redirect(method="clearAll",at=@At(value="FIELD",target="Lcom/mojang/blaze3d/vertex/BufferBuilder;building:Z"))
    private boolean morefix$bufferIsBuilding(BufferBuilder buffer){return ((ForgematicaBufferAccess)buffer).morefix$building();}
}
