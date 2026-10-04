package dev.kriate.catwalk.mixin;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import dev.kriate.catwalk.client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo @Mixin(targets="fi.dy.masa.litematica.scheduler.tasks.TaskCountBlocksPlacement",remap=false)
public abstract class ForgematicaMaterialPlacementMixin implements ForgematicaCountAccess {
    @Shadow @Final protected boolean ignoreState;
    @Unique private final ForgematicaMaterials morefix$counts=new ForgematicaMaterials();
    public ForgematicaMaterials morefix$materials(){return morefix$counts;}
    @Inject(method="countAtPosition",at=@At("HEAD"),cancellable=true)
    private void morefix$countCopycat(BlockPos pos,CallbackInfo ci){
        var world=fi.dy.masa.litematica.world.SchematicWorldHandler.getSchematicWorld();var client=Minecraft.getInstance().level;if(world==null||client==null)return;var state=world.getBlockState(pos);
        if(!(state.getBlock() instanceof ICopycatBlock))return;
        morefix$counts.count(state,ForgematicaMaterials.data(world.getBlockEntity(pos)),client.getBlockState(pos),ForgematicaMaterials.data(client.getBlockEntity(pos)),ignoreState);ci.cancel();
    }
}
