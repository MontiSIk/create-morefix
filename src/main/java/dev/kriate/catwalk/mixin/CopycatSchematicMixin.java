package dev.kriate.catwalk.mixin;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import dev.kriate.catwalk.CopycatPlacementAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=ICopycatBlockEntity.class,remap=false)
public interface CopycatSchematicMixin {
    @Inject(method="accept",at=@At("HEAD"),cancellable=true)
    private void morefix$mergeMaterials(BlockEntity other,CallbackInfo ci) {
        if ((Object)this instanceof IMultiStateCopycatBlockEntity target && other instanceof IMultiStateCopycatBlockEntity source) {
            // The native schematic merge copies only the default material of a multipart Copycat.
            var storage=com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage.create(source.getMaterialItemStorage().getAllProperties());
            for(String key:source.getMaterialItemStorage().getAllProperties()){var part=source.getMaterialItemStorage().getMaterialItem(key);storage.storeMaterialItem(key,new com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage.MaterialItem(part.material(),part.consumedItem().copy(),part.enableCT()));}
            target.setMaterialItemStorageInternal(storage);
            if ((Object)this instanceof CopycatPlacementAccess access && other instanceof CopycatPlacementAccess from) access.morefix$connected(from.morefix$connected());
            target.notifyUpdate();
            com.copycatsplus.copycats.utility.BlockEntityUtils.redraw((BlockEntity)(Object)this);
            ci.cancel();
        }
    }
    @Inject(method="accept",at=@At("RETURN"))
    private void morefix$mergePlacementMode(BlockEntity other,CallbackInfo ci) {
        if ((Object)this instanceof CopycatPlacementAccess access && other instanceof CopycatPlacementAccess from) {
            access.morefix$connected(from.morefix$connected());
            ((ICopycatBlockEntity)(Object)this).notifyUpdate();
        }
    }
}