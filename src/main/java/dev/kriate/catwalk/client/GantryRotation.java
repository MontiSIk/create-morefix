package dev.kriate.catwalk.client;
import dev.kriate.catwalk.GantryAxes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="morefix",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class GantryRotation {
    @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event){
        event.enqueueWork(()->{
            com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu.registerRotationProperty(GantryAxes.SHAFT_ALONG_MOUNT,"Shaft along mount");
            if(dev.kriate.catwalk.OptionalMods.deco())com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu.registerRotationProperty(com.github.talrey.createdeco.blocks.SupportWedgeBlock.ORIENTATION,"Support angle");
        });
    }
}
