package dev.kriate.catwalk;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.entity.player.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PlacementModes {
    public static final BooleanProperty CONNECTED=BooleanProperty.create("morefix_connected");
    public static final Set<String> MATERIALS=Set.of("industrial_iron_block","weathered_iron_block","andesite_alloy_block","zinc_block","brass_block","andesite_casing","copper_casing","brass_casing","railway_casing","shadow_steel_casing","refined_radiance_casing");
    public static final Set<String> ENCASED=Set.of("zinc_casing","creative_casing");
    public static boolean supports(String namespace,String name){return namespace.equals("create")&&MATERIALS.contains(name)||namespace.equals("createcasing")&&ENCASED.contains(name)||VanillaConnections.supports(namespace,name);}
    public static java.util.List<net.minecraft.world.level.block.Block> blocks(){return net.minecraft.core.registries.BuiltInRegistries.BLOCK.stream().filter(block->{var id=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);return supports(id.getNamespace(),id.getPath());}).toList();}
    public static final ThreadLocal<Boolean> CONSTRUCTING=ThreadLocal.withInitial(()->false);
    public static final Map<UUID,Boolean> CONTROL=new ConcurrentHashMap<>();
    public static boolean ctrl(Player player){return player!=null&&(player.level().isClientSide?dev.kriate.catwalk.client.ControlPlacement.current():CONTROL.getOrDefault(player.getUUID(),false));}
}

