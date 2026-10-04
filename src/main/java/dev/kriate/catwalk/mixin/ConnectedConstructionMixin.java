package dev.kriate.catwalk.mixin;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.kriate.catwalk.PlacementModes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value=AbstractRegistrate.class,remap=false)
public abstract class ConnectedConstructionMixin {
    @ModifyVariable(method="block(Ljava/lang/Object;Ljava/lang/String;Lcom/tterrag/registrate/util/nullness/NonNullFunction;)Lcom/tterrag/registrate/builders/BlockBuilder;",at=@At("HEAD"),argsOnly=true)
    private NonNullFunction morefix$factory(NonNullFunction factory,Object parent,String name,NonNullFunction original){
        if(!PlacementModes.supports(((AbstractRegistrate)(Object)this).getModid(),name))return factory;
        return properties->{boolean old=PlacementModes.CONSTRUCTING.get();PlacementModes.CONSTRUCTING.set(true);try{return factory.apply(properties);}finally{PlacementModes.CONSTRUCTING.set(old);}};
    }
}
