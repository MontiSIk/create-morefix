package dev.kriate.catwalk.client;

import java.util.List;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;

/** Vanilla pane multipart models contain uncullable caps on cell boundaries. */
public final class PaneCullModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> HIDDEN=new ModelProperty<>();
    public PaneCullModel(BakedModel model){super(model);}
    @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){
        int hidden=0;
        if(state.getOptionalValue(dev.kriate.catwalk.PlacementModes.CONNECTED).orElse(false))
            for(Direction side:Direction.values())if(state.skipRendering(world.getBlockState(pos.relative(side)),side))hidden|=1<<side.ordinal();
        return ((net.neoforged.neoforge.client.extensions.IBakedModelExtension)originalModel).getModelData(world,pos,state,data).derive().with(HIDDEN,hidden).build();
    }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){
        var quads=((net.neoforged.neoforge.client.extensions.IBakedModelExtension)originalModel).getQuads(state,side,random,data,type);Integer hidden=data.get(HIDDEN);
        if(hidden==null||hidden==0)return quads;
        return quads.stream().filter(quad->!boundary(quad,hidden)).toList();
    }
    private static boolean boundary(BakedQuad quad,int hidden){
        Direction side=quad.getDirection();if((hidden&(1<<side.ordinal()))==0)return false;
        int component=switch(side.getAxis()){case X->0;case Y->1;case Z->2;};
        float boundary=side.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0;
        int[] vertices=quad.getVertices();int stride=vertices.length/4;
        for(int i=0;i<4;i++)if(Math.abs(Float.intBitsToFloat(vertices[i*stride+component])-boundary)>0.00001f)return false;
        return true;
    }
}
