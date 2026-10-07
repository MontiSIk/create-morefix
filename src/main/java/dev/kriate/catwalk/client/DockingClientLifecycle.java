package dev.kriate.catwalk.client;
@net.neoforged.fml.common.EventBusSubscriber(modid="morefix",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class DockingClientLifecycle {
    @net.neoforged.bus.api.SubscribeEvent
    public static void logout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event){
        if(net.neoforged.fml.ModList.get().isLoaded("sable")&&net.neoforged.fml.ModList.get().isLoaded("simulated"))DockingRender.clear();
    }
}
