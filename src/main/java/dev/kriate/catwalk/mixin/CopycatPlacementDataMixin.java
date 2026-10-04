package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.CopycatPlacementAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=com.simibubi.create.foundation.blockEntity.SmartBlockEntity.class,remap=false)
public abstract class CopycatPlacementDataMixin implements CopycatPlacementAccess {
    @Unique private boolean morefix$connected;
    public boolean morefix$connected(){return morefix$connected;}
    public void morefix$connected(boolean value){morefix$connected=value;}
    @Inject(method="read",at=@At("RETURN"))
    private void morefix$read(CompoundTag tag,HolderLookup.Provider registries,boolean client,CallbackInfo ci){
        if(!((Object)this instanceof com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity))return;boolean old=morefix$connected;morefix$connected=tag.getBoolean("MoreFixConnected");
        if(client&&old!=morefix$connected)com.copycatsplus.copycats.utility.BlockEntityUtils.redraw((net.minecraft.world.level.block.entity.BlockEntity)(Object)this);
    }
    @Inject(method="write",at=@At("RETURN"))
    private void morefix$write(CompoundTag tag,HolderLookup.Provider registries,boolean client,CallbackInfo ci){if((Object)this instanceof com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity)tag.putBoolean("MoreFixConnected",morefix$connected);}
    @Inject(method="writeSafe",at=@At("RETURN"))
    private void morefix$safe(CompoundTag tag,HolderLookup.Provider registries,CallbackInfo ci){if((Object)this instanceof com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity)tag.putBoolean("MoreFixConnected",morefix$connected);}
}
