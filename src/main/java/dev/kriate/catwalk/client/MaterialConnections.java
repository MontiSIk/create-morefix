package dev.kriate.catwalk.client;
import dev.kriate.catwalk.PlacementModes;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.foundation.block.connected.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import java.util.*;

public final class MaterialConnections extends ConnectedTextureBehaviour.Base {
    // Pane surfaces sit inside the cell; their multipart quads need context even
    // when vanilla face culling marks the corresponding cell face as hidden.
    @Override public boolean buildContextForOccludedDirections(){return true;}
    @Override protected boolean reverseUVsHorizontally(BlockState state,Direction face){
        return dev.kriate.catwalk.VanillaConnections.PANES.contains(dev.kriate.catwalk.VanillaConnections.original(state.getBlock()))
            ?face.getAxisDirection()==Direction.AxisDirection.NEGATIVE:super.reverseUVsHorizontally(state,face);
    }
    private static final Map<String,CTSpriteShiftEntry> IRON=new HashMap<>();
    public static boolean applies(BlockState state){var id=BuiltInRegistries.BLOCK.getKey(state.getBlock());return PlacementModes.supports(id.getNamespace(),id.getPath());}
    private static CTSpriteShiftEntry casing(String original,String target){return IRON.computeIfAbsent(original+"->"+target,n->CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL,ResourceLocation.parse(original),ResourceLocation.parse(target)));}
    private static CTSpriteShiftEntry iron(String name){return IRON.computeIfAbsent(name,n->CTSpriteShifter.getCT(AllCTTypes.CROSS,ResourceLocation.fromNamespaceAndPath("create","block/"+n),ResourceLocation.fromNamespaceAndPath("catwalk_orientation","block/"+n+"_connected")));}
    @Override public CTSpriteShiftEntry getShift(BlockState state,Direction face,TextureAtlasSprite sprite){var original=dev.kriate.catwalk.VanillaConnections.original(state.getBlock());if(dev.kriate.catwalk.VanillaConnections.IDS.containsKey(original)){
        String texture=dev.kriate.catwalk.VanillaConnections.texture(original);
        if(original.getPath().endsWith("froglight"))texture+=state.getValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS)==face.getAxis()?"_top":"_side";
        if(sprite!=null&&!sprite.contents().name().equals(ResourceLocation.parse(texture)))return null;
        String source=texture;return IRON.computeIfAbsent(source,n->CTSpriteShifter.getCT(AllCTTypes.CROSS,ResourceLocation.parse(source),ResourceLocation.fromNamespaceAndPath("catwalk_orientation","block/vanilla_"+source.substring(source.indexOf(':')+1).replace('/','_')+"_connected")));
    }return switch(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()){
        case "andesite_casing"->AllSpriteShifts.ANDESITE_CASING;
        case "copper_casing"->AllSpriteShifts.COPPER_CASING;
        case "brass_casing"->AllSpriteShifts.BRASS_CASING;
        case "railway_casing"->face.getAxis()==Direction.Axis.Y?casing("create:block/railway_casing","create:block/railway_casing_connected"):casing("create:block/railway_casing_side","create:block/railway_casing_side_connected");
        case "shadow_steel_casing","refined_radiance_casing"->casing("create:block/"+BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath(),"create:block/"+BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()+"_connected");
        case "zinc_casing"->casing("createcasing:block/casing/zinc","createcasing:block/casing/zinc_connected");
        case "creative_casing"->casing("create:block/creative_casing","createcasing:block/creative_casing_connected");
        case "industrial_iron_block","weathered_iron_block"->iron(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()+(face.getAxis()==Direction.Axis.Y?"_top":""));
        case "andesite_alloy_block"->iron("andesite_block");
        case "zinc_block","brass_block"->iron(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath());
        default->null;
    };}
    public static void prepare(){for(String name:List.of("industrial_iron_block","weathered_iron_block")){iron(name);iron(name+"_top");}for(String name:List.of("andesite_block","zinc_block","brass_block"))iron(name);for(var block:PlacementModes.blocks())for(Direction face:Direction.values())new MaterialConnections().getShift(block.defaultBlockState(),face,null);}
    private static boolean enabled(BlockAndTintGetter reader,BlockPos pos,BlockState fallback){
        // Copycats filters out positions which cannot share geometry. The
        // placement flag belongs to the real BE, not to that filtered view.
        while(true){
            if(reader instanceof com.copycatsplus.copycats.foundation.copycat.model.FilteredBlockAndTintGetter filtered){reader=filtered.wrapped;continue;}
            if(reader instanceof com.copycatsplus.copycats.foundation.copycat.model.ScaledBlockAndTintGetter scaled){pos=scaled.getTruePos(pos);reader=scaled.getWrapped();continue;}
            break;
        }
        var actual=reader.getBlockState(pos);
        if(actual.getBlock() instanceof ICopycatBlock)return reader.getBlockEntity(pos) instanceof dev.kriate.catwalk.CopycatPlacementAccess access&&access.morefix$connected();
        return fallback.getOptionalValue(PlacementModes.CONNECTED).orElse(false);
    }
    @Override public boolean connectsTo(BlockState state,BlockState other,BlockAndTintGetter reader,BlockPos pos,BlockPos otherPos,Direction face){
        var id=dev.kriate.catwalk.VanillaConnections.original(state.getBlock());
        if(dev.kriate.catwalk.VanillaConnections.PANES.contains(id))return enabled(reader,pos,state)&&enabled(reader,otherPos,other)&&id.equals(dev.kriate.catwalk.VanillaConnections.original(other.getBlock()));
        return enabled(reader,pos,state)&&enabled(reader,otherPos,other)&&super.connectsTo(state,other,reader,pos,otherPos,face);
    }
}
