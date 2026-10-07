package dev.kriate.catwalk.client;
import dev.kriate.catwalk.ladders.*;
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
public final class LadderAttachmentModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<List<BlockState>> PARTS=new ModelProperty<>();
    public LadderAttachmentModel(BakedModel model){super(model);}
    public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){var b=LadderAttachments.behaviour(world,pos);return ((IBakedModelExtension)originalModel).getModelData(world,pos,state,data).derive().with(PARTS,b==null?List.of():b.parts()).build();}
    public List<BakedQuad> getQuads(BlockState state,Direction face,RandomSource random,ModelData data,RenderType type){var quads=new ArrayList<>(((IBakedModelExtension)originalModel).getQuads(state,face,random,data,type));var parts=data.get(PARTS);if(parts!=null)for(var part:parts){var model=(IBakedModelExtension)Minecraft.getInstance().getBlockRenderer().getBlockModel(part);if(type==null||model.getRenderTypes(part,random,ModelData.EMPTY).contains(type))quads.addAll(model.getQuads(part,face,random,ModelData.EMPTY,type));}return quads;}
    public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){var types=((IBakedModelExtension)originalModel).getRenderTypes(state,random,data);var parts=data.get(PARTS);if(parts!=null)for(var part:parts)types=ChunkRenderTypeSet.union(types,((IBakedModelExtension)Minecraft.getInstance().getBlockRenderer().getBlockModel(part)).getRenderTypes(part,random,ModelData.EMPTY));return types;}
}
