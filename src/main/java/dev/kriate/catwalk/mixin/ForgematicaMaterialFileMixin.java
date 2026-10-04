package dev.kriate.catwalk.mixin;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import dev.kriate.catwalk.client.ForgematicaMaterials;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets="fi.dy.masa.litematica.materials.MaterialListUtils",remap=false)
public abstract class ForgematicaMaterialFileMixin {
    @Inject(method="createMaterialListFor(Lfi/dy/masa/litematica/schematic/LitematicaSchematic;Ljava/util/Collection;)Ljava/util/List;",at=@At("HEAD"),cancellable=true)
    private static void morefix$fileMaterials(LitematicaSchematic schematic,java.util.Collection<String> regions,CallbackInfoReturnable<java.util.List<MaterialListEntry>> ci){ci.setReturnValue(ForgematicaMaterials.file(schematic,regions));}
}
