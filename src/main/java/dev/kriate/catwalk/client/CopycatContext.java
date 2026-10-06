package dev.kriate.catwalk.client;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
public final class CopycatContext {
    public static boolean enabled(BlockAndTintGetter reader,BlockPos pos,BlockState fallback){
        while(true){
            if(reader instanceof com.copycatsplus.copycats.foundation.copycat.model.FilteredBlockAndTintGetter filtered){reader=filtered.wrapped;continue;}
            if(reader instanceof com.copycatsplus.copycats.foundation.copycat.model.ScaledBlockAndTintGetter scaled){pos=scaled.getTruePos(pos);reader=scaled.getWrapped();continue;}
            break;
        }
        var actual=reader.getBlockState(pos);
        if(actual.getBlock() instanceof com.copycatsplus.copycats.foundation.copycat.ICopycatBlock)return reader.getBlockEntity(pos) instanceof dev.kriate.catwalk.CopycatPlacementAccess access&&access.morefix$connected();
        return fallback.getOptionalValue(dev.kriate.catwalk.PlacementModes.CONNECTED).orElse(false);
    }
}
