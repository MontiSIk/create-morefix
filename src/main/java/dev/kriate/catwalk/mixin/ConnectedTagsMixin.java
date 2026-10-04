package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.VanillaConnections;
import net.minecraft.tags.TagLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.*;

@Mixin(TagLoader.class)
public abstract class ConnectedTagsMixin {
    @Inject(method="build(Ljava/util/Map;)Ljava/util/Map;",at=@At("RETURN"),cancellable=true)
    private void morefix$variantTags(CallbackInfoReturnable<Map> cir){
        Map result=new LinkedHashMap(cir.getReturnValue());
        result.replaceAll((key,value)->{
            var members=new ArrayList((Collection)value);
            for(Object entry:(Collection)value){
                Object raw=entry instanceof Holder<?> holder?holder.value():entry;
                if(!(raw instanceof Block block))continue;
                var variant=VanillaConnections.VARIANTS.get(BuiltInRegistries.BLOCK.getKey(block));
                if(variant==null||!variant.isBound())continue;
                Object extra=entry instanceof Holder<?>?BuiltInRegistries.BLOCK.wrapAsHolder(variant.get()):variant.get();
                if(!members.contains(extra))members.add(extra);
            }
            return members;
        });
        cir.setReturnValue(result);
    }
}
