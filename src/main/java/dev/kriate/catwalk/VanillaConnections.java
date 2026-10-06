package dev.kriate.catwalk;

import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.neoforged.neoforge.registries.*;
import java.util.*;

/** Opt-in variants keep vanilla state definitions and existing worlds intact. */
public final class VanillaConnections {
    public static final List<String> NAMES=List.of("iron_block","gold_block","diamond_block","lapis_block","emerald_block","netherite_block","copper_block","exposed_copper","weathered_copper","oxidized_copper","waxed_copper_block","waxed_exposed_copper","waxed_weathered_copper","waxed_oxidized_copper","glass","tinted_glass","white_stained_glass","orange_stained_glass","magenta_stained_glass","light_blue_stained_glass","yellow_stained_glass","lime_stained_glass","pink_stained_glass","gray_stained_glass","light_gray_stained_glass","cyan_stained_glass","purple_stained_glass","blue_stained_glass","brown_stained_glass","green_stained_glass","red_stained_glass","black_stained_glass","redstone_block","smooth_stone","polished_granite","polished_diorite","polished_andesite","polished_deepslate","polished_tuff","polished_blackstone","sea_lantern","shroomlight","ochre_froglight","verdant_froglight","pearlescent_froglight");
    public static final List<String> STONES=List.of("granite","diorite","andesite","deepslate","tuff","dripstone","calcite","limestone","scoria","scorchia","asurine","crimsite","ochrum","veridium");
    public static final List<String> METALS=List.of("iron","industrial_iron","andesite","brass","zinc","copper");
    public static final List<String> CREATE_PANES=List.of("industrial_iron_window_pane","warped_window_pane","vertical_framed_glass_pane","dark_oak_window_pane","ornate_iron_window_pane","cherry_window_pane","jungle_window_pane","spruce_window_pane","bamboo_window_pane","birch_window_pane","framed_glass_pane","mangrove_window_pane","weathered_iron_window_pane","crimson_window_pane","oak_window_pane","tiled_glass_pane","horizontal_framed_glass_pane","acacia_window_pane");
    public static final Set<ResourceLocation> PANES=new LinkedHashSet<>();
    public static final Map<ResourceLocation,String> IDS=new LinkedHashMap<>();
    public static final DeferredRegister<Block> REGISTER=DeferredRegister.create(Registries.BLOCK,"catwalk_orientation");
    public static final Map<ResourceLocation,DeferredHolder<Block,Block>> VARIANTS=new LinkedHashMap<>();
    static {
        for(String name:NAMES)IDS.put(ResourceLocation.withDefaultNamespace(name),name);
        for(String stone:STONES)IDS.put(ResourceLocation.fromNamespaceAndPath("create","polished_cut_"+stone),"create_polished_cut_"+stone);
        addPane(ResourceLocation.withDefaultNamespace("glass_pane"));
        for(var dye:net.minecraft.world.item.DyeColor.values())addPane(ResourceLocation.withDefaultNamespace(dye.getName()+"_stained_glass_pane"));
        if(OptionalMods.deco())for(String metal:METALS){var id=ResourceLocation.fromNamespaceAndPath("createdeco",metal+"_window");IDS.put(id,"createdeco_"+id.getPath());addPane(ResourceLocation.fromNamespaceAndPath("createdeco",metal+"_window_pane"));}
        for(String pane:CREATE_PANES)addPane(ResourceLocation.fromNamespaceAndPath("create",pane));
        IDS.forEach((id,name)->VARIANTS.put(id,REGISTER.register("connected_"+name,()->createVariant(id))));
    }
    private static void addPane(ResourceLocation id){PANES.add(id);IDS.put(id,id.getNamespace().equals("minecraft")?id.getPath():id.getNamespace()+"_"+id.getPath());}
    private static Block createVariant(ResourceLocation id){Block base=base(id);if(PANES.contains(id))return base instanceof BeaconBeamBlock?new StainedPaneConnectedBlock(base):new PaneConnectedBlock(base);if(id.getPath().endsWith("froglight"))return new PillarConnectedBlock(base);if(id.equals(ResourceLocation.withDefaultNamespace("redstone_block")))return new PoweredConnectedBlock(base);if(base instanceof BeaconBeamBlock)return new StainedConnectedBlock(base);if(id.getPath().endsWith("glass")||id.getPath().endsWith("_window"))return new GlassConnectedBlock(base);if(base instanceof WeatheringCopper)return new CopperConnectedBlock(base);return new ConnectedBlock(base);}
    public interface ConnectedVariant {Block originalBlock();}
    @SuppressWarnings({"unchecked","rawtypes"}) public static BlockState copyProperties(BlockState from,BlockState to){for(var property:from.getProperties())if(to.hasProperty(property))to=to.setValue((net.minecraft.world.level.block.state.properties.Property)property,from.getValue(property));return to;}
    public static ResourceLocation paneModel(ResourceLocation id){return ResourceLocation.fromNamespaceAndPath("catwalk_orientation","block/copycat_pane_material_"+id.getNamespace()+"_"+id.getPath());}
    public static Block base(ResourceLocation id){return BuiltInRegistries.BLOCK.get(id);}
    public static ResourceLocation original(Block block){return block instanceof ConnectedVariant connected?BuiltInRegistries.BLOCK.getKey(connected.originalBlock()):BuiltInRegistries.BLOCK.getKey(block);}
    public static boolean supports(String namespace,String name){return IDS.containsKey(ResourceLocation.fromNamespaceAndPath(namespace,name))||namespace.equals("catwalk_orientation")&&name.startsWith("connected_")&&IDS.containsValue(name.substring(10));}
    public static BlockState placement(BlockState state){var variant=VARIANTS.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()));if(variant==null)return state;return copyProperties(state,variant.get().defaultBlockState()).setValue(PlacementModes.CONNECTED,true);}
    public static String texture(ResourceLocation id){String name=id.getPath();if(PANES.contains(id))name=name.substring(0,name.length()-5);if(id.getNamespace().equals("createdeco"))return "createdeco:block/palettes/windows/"+name;if(id.getNamespace().equals("create")){if(PANES.contains(id)){if(name.equals("horizontal_framed_glass")||name.equals("vertical_framed_glass"))name="framed_glass";return "create:block/palettes/"+name;}return "create:block/palettes/stone_types/polished/"+name.substring("polished_cut_".length())+"_cut_polished";}if(name.startsWith("waxed_"))name=name.substring(6);return "minecraft:block/"+name;}
    public static class PaneConnectedBlock extends IronBarsBlock implements ConnectedVariant {
        public final Block base;
        public PaneConnectedBlock(Block base){super(BlockBehaviour.Properties.ofFullCopy(base).dropsLike(base));this.base=base;registerDefaultState(defaultBlockState().setValue(PlacementModes.CONNECTED,true));}
        public Block originalBlock(){return base;}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){super.createBlockStateDefinition(builder);builder.add(PlacementModes.CONNECTED);}
        @Override public net.minecraft.world.item.Item asItem(){return base.asItem();}
        @Override public ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level,BlockPos pos,BlockState state){return new ItemStack(base);}
        @Override public int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return copyProperties(state,base.defaultBlockState()).getLightBlock(level,pos);}
        @Override public boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return copyProperties(state,base.defaultBlockState()).propagatesSkylightDown(level,pos);}
        @Override public float getShadeBrightness(BlockState state,BlockGetter level,BlockPos pos){return copyProperties(state,base.defaultBlockState()).getShadeBrightness(level,pos);}
        @Override public boolean skipRendering(BlockState state,BlockState adjacent,Direction side){Block neighbor=adjacent.getBlock();if(neighbor instanceof ConnectedVariant variant)adjacent=copyProperties(adjacent,variant.originalBlock().defaultBlockState());return copyProperties(state,base.defaultBlockState()).skipRendering(adjacent,side);}
    }
    public static final class StainedPaneConnectedBlock extends PaneConnectedBlock implements BeaconBeamBlock {
        public StainedPaneConnectedBlock(Block base){super(base);}
        @Override public net.minecraft.world.item.DyeColor getColor(){return ((BeaconBeamBlock)base).getColor();}
    }

    public static class ConnectedBlock extends Block implements ConnectedVariant {
        public Block originalBlock(){return base;}
        public final Block base;
        public ConnectedBlock(Block base){super(BlockBehaviour.Properties.ofFullCopy(base).dropsLike(base));this.base=base;registerDefaultState(defaultBlockState().setValue(PlacementModes.CONNECTED,true));}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(PlacementModes.CONNECTED);}
        @Override public ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level,BlockPos pos,BlockState state){return new ItemStack(base);}
        @Override public net.minecraft.world.item.Item asItem(){return base.asItem();}
        public BlockState getToolModifiedState(BlockState state,net.minecraft.world.item.context.UseOnContext context,net.neoforged.neoforge.common.ItemAbility ability,boolean simulate){var changed=((net.neoforged.neoforge.common.extensions.IBlockExtension)(Object)base).getToolModifiedState(base.defaultBlockState(),context,ability,simulate);return changed==null?null:placement(changed);}
        @Override public boolean skipRendering(BlockState state,BlockState adjacent,Direction side){return base instanceof TransparentBlock&&adjacent.is(this)&&state.getValue(PlacementModes.CONNECTED)&&adjacent.getValue(PlacementModes.CONNECTED)||super.skipRendering(state,adjacent,side);}
    }
    public static class GlassConnectedBlock extends ConnectedBlock {
        public GlassConnectedBlock(Block base){super(base);}
        @Override public boolean skipRendering(BlockState state,BlockState adjacent,Direction side){
            if(adjacent.getBlock() instanceof ConnectedVariant variant)adjacent=copyProperties(adjacent,variant.originalBlock().defaultBlockState());
            return copyProperties(state,base.defaultBlockState()).skipRendering(adjacent,side);
        }
        @Override public int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return base.defaultBlockState().getLightBlock(level,pos);}
        @Override public boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return base.defaultBlockState().propagatesSkylightDown(level,pos);}
        @Override public float getShadeBrightness(BlockState state,BlockGetter level,BlockPos pos){return base.defaultBlockState().getShadeBrightness(level,pos);}
    }
    public static final class StainedConnectedBlock extends GlassConnectedBlock implements BeaconBeamBlock {
        public StainedConnectedBlock(Block base){super(base);}
        @Override public net.minecraft.world.item.DyeColor getColor(){return ((BeaconBeamBlock)base).getColor();}
    }
    public static final class PoweredConnectedBlock extends ConnectedBlock {
        public PoweredConnectedBlock(Block base){super(base);}
        @Override public boolean isSignalSource(BlockState state){return base.defaultBlockState().isSignalSource();}
        @Override public int getSignal(BlockState state,BlockGetter level,BlockPos pos,Direction side){return base.defaultBlockState().getSignal(level,pos,side);}
        @Override public int getDirectSignal(BlockState state,BlockGetter level,BlockPos pos,Direction side){return base.defaultBlockState().getDirectSignal(level,pos,side);}
    }
    public static final class PillarConnectedBlock extends ConnectedBlock {
        public PillarConnectedBlock(Block base){super(base);registerDefaultState(defaultBlockState().setValue(RotatedPillarBlock.AXIS,Direction.Axis.Y));}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){super.createBlockStateDefinition(builder);builder.add(RotatedPillarBlock.AXIS);}
        @Override public BlockState rotate(BlockState state,Rotation rotation){return RotatedPillarBlock.rotatePillar(state,rotation);}
    }
    public static final class CopperConnectedBlock extends ConnectedBlock implements WeatheringCopper {
        public CopperConnectedBlock(Block base){super(base);}
        @Override public WeatherState getAge(){return ((WeatheringCopper)base).getAge();}
        @Override public boolean isRandomlyTicking(BlockState state){return getNext(state).isPresent();}
        @Override public void randomTick(BlockState state,net.minecraft.server.level.ServerLevel level,BlockPos pos,net.minecraft.util.RandomSource random){changeOverTime(state,level,pos,random);}
    }
}

