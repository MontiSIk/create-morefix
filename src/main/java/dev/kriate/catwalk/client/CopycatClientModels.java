package dev.kriate.catwalk.client;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Loaded only when Copycats is installed, including by the JVM verifier. */
public final class CopycatClientModels {
    public static void colors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event){
        event.register(com.copycatsplus.copycats.foundation.copycat.ICopycatBlock.wrappedColor(),dev.kriate.catwalk.copylink.CopyLinks.BLOCK.get());
    }
    public static void links(ModelEvent.ModifyBakingResult event){
        for(var state:dev.kriate.catwalk.copylink.CopyLinks.BLOCK.get().getStateDefinition().getPossibleStates()){
            var key=net.minecraft.client.renderer.block.BlockModelShaper.stateToModelLocation(state);
            var original=event.getModels().get(key);
            if(original!=null)event.getModels().put(key,com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore.createModel(original,new com.copycatsplus.copycats.content.copycat.block.CopycatBlockModelCore()));
        }
    }
}
