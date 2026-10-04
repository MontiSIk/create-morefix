package dev.kriate.catwalk.client;
import dev.kriate.catwalk.pipes.PipeSupports;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.client.extensions.IBakedModelExtension;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import java.util.*;
public final class PipeSupportModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<BlockState> SUPPORT=new ModelProperty<>();
    public PipeSupportModel(BakedModel model){super(model);}
    @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){
        return ((IBakedModelExtension)originalModel).getModelData(world,pos,state,data).derive().with(SUPPORT,PipeSupports.support(world,pos)).build();
    }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){
        var base=((IBakedModelExtension)originalModel).getQuads(state,side,random,data,type);var support=data.get(SUPPORT);if(support==null)return base;
        var model=(IBakedModelExtension)Minecraft.getInstance().getBlockRenderer().getBlockModel(support);
        if(type!=null&&!model.getRenderTypes(support,random,ModelData.EMPTY).contains(type))return base;
        var quads=new ArrayList<>(base);quads.addAll(model.getQuads(support,side,random,ModelData.EMPTY,type));return quads;
    }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){
        var base=((IBakedModelExtension)originalModel).getRenderTypes(state,random,data);var support=data.get(SUPPORT);if(support==null)return base;
        return ChunkRenderTypeSet.union(base,((IBakedModelExtension)Minecraft.getInstance().getBlockRenderer().getBlockModel(support)).getRenderTypes(support,random,ModelData.EMPTY));
    }
}
