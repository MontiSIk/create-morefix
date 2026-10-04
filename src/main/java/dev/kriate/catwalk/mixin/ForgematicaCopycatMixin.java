package dev.kriate.catwalk.mixin;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.extensions.IBakedModelExtension;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Pseudo
@Mixin(targets="fi.dy.masa.litematica.render.schematic.BlockModelRendererSchematic",remap=false)
public abstract class ForgematicaCopycatMixin {
    @ModifyVariable(method="renderModel",at=@At("HEAD"),argsOnly=true)
    private BakedModel morefix$materialModel(BakedModel model,BlockAndTintGetter world,BakedModel ignored,BlockState state,BlockPos pos,PoseStack matrices,VertexConsumer vertices,long seed) {
        if (!(state.getBlock() instanceof ICopycatBlock))return model;
        var entity=world.getBlockEntity(pos);
        var source=entity==null?ModelData.EMPTY:((IBlockEntityExtension)entity).getModelData();
        var data=((IBakedModelExtension)model).getModelData(world,pos,state,source);
        // Forgematica uses the three-argument vanilla API, which otherwise loses Copycat data.
        return new BakedModelWrapper<BakedModel>(model) {
            @Override public java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(BlockState blockState,Direction side,RandomSource random) {
                return ((IBakedModelExtension)originalModel).getQuads(blockState,side,random,data,null);
            }
        };
    }
}
