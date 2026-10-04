package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.pipes.PipeSupports;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.*;
@Mixin(value=RadialWrenchMenu.class,remap=false)
public abstract class PipeSupportMenuMixin {
    @Shadow @Final @Mutable private BlockEntity blockEntity;
    @Shadow @Final private BlockState state;
    @WrapMethod(method="tryCreateFor")
    private static Optional<RadialWrenchMenu> morefix$supportMenu(BlockState state,BlockPos pos,Level level,Operation<Optional<RadialWrenchMenu>> original){
        var cover=PipeSupports.isPipe(state.getBlock())?PipeSupports.support(level,pos):null;
        return original.call(cover==null?state:cover,pos,level);
    }
    @Redirect(method="tick",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState morefix$verifySupport(Level level,BlockPos pos){
        var cover=PipeSupports.isSupport(state.getBlock())?PipeSupports.support(level,pos):null;
        return cover==null?level.getBlockState(pos):cover;
    }
    @Inject(method="<init>",at=@At("TAIL"))
    private void morefix$preview(BlockState state,BlockPos pos,Level level,List<?> properties,CallbackInfo ci){
        if(PipeSupports.isSupport(state.getBlock())&&PipeSupports.isPipe(level.getBlockState(pos).getBlock()))blockEntity=null;
    }
}
