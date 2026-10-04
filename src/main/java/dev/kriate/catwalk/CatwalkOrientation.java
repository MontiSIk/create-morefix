package dev.kriate.catwalk;

import net.neoforged.fml.common.Mod;

@Mod("catwalk_orientation")
public final class CatwalkOrientation {
    public CatwalkOrientation(net.neoforged.bus.api.IEventBus bus) {if(net.neoforged.fml.loading.FMLEnvironment.dist.isClient())dev.kriate.catwalk.client.MaterialConnections.prepare();dev.kriate.catwalk.copylink.CopyLinks.register(bus);
        dev.kriate.catwalk.docking.DockingPayload.register(bus);ControlPayload.register(bus);VanillaConnections.REGISTER.register(bus);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent e)->PlacementModes.CONTROL.remove(e.getEntity().getUUID()));
        bus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent e)->e.enqueueWork(()->{
            if(net.neoforged.fml.ModList.get().isLoaded("simulated")&&net.neoforged.fml.ModList.get().isLoaded("sable"))dev.kriate.catwalk.docking.MovingPorts.register();
        }));
    }
}

