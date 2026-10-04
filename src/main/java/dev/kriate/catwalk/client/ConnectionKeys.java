package dev.kriate.catwalk.client;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
@EventBusSubscriber(modid="catwalk_orientation",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ConnectionKeys {
    @SubscribeEvent public static void register(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event){((net.neoforged.neoforge.client.extensions.IKeyMappingExtension)(Object)ControlPlacement.CONNECT).setKeyConflictContext(net.neoforged.neoforge.client.settings.KeyConflictContext.IN_GAME);event.register(ControlPlacement.CONNECT);}
}

