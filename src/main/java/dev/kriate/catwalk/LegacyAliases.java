package dev.kriate.catwalk;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.*;
import net.neoforged.neoforge.registries.*;
/** Registry aliases migrate existing world palettes, materials and block-entity NBT. */
public final class LegacyAliases {
    public static final String OLD_NAMESPACE="catwalk_orientation";
    public static void register(IEventBus bus){
        bus.addListener(EventPriority.LOWEST,(RegisterEvent event)->{
            if(!(event.getRegistry() instanceof BaseMappedRegistry<?> registry))return;
            for(var id:new java.util.ArrayList<>(event.getRegistry().keySet()))if(id.getNamespace().equals("morefix"))
                registry.addAlias(ResourceLocation.fromNamespaceAndPath(OLD_NAMESPACE,id.getPath()),id);
        });
    }
}
