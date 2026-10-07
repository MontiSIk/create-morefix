package dev.kriate.catwalk.client;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

/** Full texture sampling surfaces for Copycats; native world bars keep their own models. */
public final class BarMaterials {
    public static final java.util.Set<ResourceLocation> TEXTURES = new java.util.HashSet<>();
    public static final Map<ResourceLocation, ResourceLocation> MODELS = new LinkedHashMap<>();
    static {
        add("minecraft", "iron_bars", "copycat_bars_material");
        for (String metal : new String[]{"andesite", "brass", "copper"})
            add("create", metal + "_bars", "copycat_bars_create_" + metal + "_bars");
        for (String metal : new String[]{"industrial_iron", "andesite", "brass", "zinc", "copper"})
            add("createdeco", metal + "_bars", "copycat_bars_createdeco_" + metal + "_bars");
        for (String metal : new String[]{"iron", "industrial_iron", "andesite", "brass", "zinc", "copper"})
            add("createdeco", metal + "_bars_overlay", "copycat_bars_createdeco_" + metal + "_bars_overlay");
    }
    private static void add(String namespace, String block, String model) {
        if(namespace.equals("createdeco")&&!dev.kriate.catwalk.OptionalMods.deco())return;
        String texture=namespace.equals("minecraft")?"minecraft:block/"+block:namespace.equals("create")?"create:block/bars/"+block:block.endsWith("_overlay")?"morefix:block/copycat_"+block:"createdeco:block/palettes/metal_bars/"+block;
        TEXTURES.add(ResourceLocation.parse(texture));
        MODELS.put(ResourceLocation.fromNamespaceAndPath(namespace, block),
            ResourceLocation.fromNamespaceAndPath("morefix", "block/" + model));
    }
    public static ResourceLocation model(BlockState material) {
        return MODELS.get(BuiltInRegistries.BLOCK.getKey(material.getBlock()));
    }
}
