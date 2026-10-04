package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.pipes.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
@Mixin(value=Level.class,remap=false)
public abstract class PipeSupportChangeMixin {
    @WrapMethod(method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private boolean morefix$convert(BlockPos pos,BlockState next,int flags,int depth,Operation<Boolean> original){
        var world=(Level)(Object)this;var old=world.getBlockState(pos);
        var support=old.getBlock()!=next.getBlock()&&PipeSupports.isPipe(old.getBlock())&&PipeSupports.isPipe(next.getBlock())?PipeSupports.support(world,pos):null;
        boolean result=original.call(pos,next,flags,depth);
        if(result&&support!=null){var b=PipeSupports.behaviour(world,pos);if(b!=null&&b.support==null)b.setSupport(support);}
        return result;
    }
}
