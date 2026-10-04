package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.client.ForgematicaCountAccess;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Pseudo @Mixin(targets="fi.dy.masa.litematica.scheduler.tasks.TaskCountBlocksBase",remap=false)
public abstract class ForgematicaMaterialResultMixin {
    @ModifyArg(method="onStop",at=@At(value="INVOKE",target="Lfi/dy/masa/litematica/materials/IMaterialList;setMaterialListEntries(Ljava/util/List;)V"),index=0)
    private java.util.List<MaterialListEntry> morefix$completeMaterials(java.util.List<MaterialListEntry> list){return (Object)this instanceof ForgematicaCountAccess copycats?copycats.morefix$materials().merge(list):list;}
}
