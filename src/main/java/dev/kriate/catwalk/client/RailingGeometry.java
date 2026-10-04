package dev.kriate.catwalk.client;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.function.Function;

public record RailingGeometry(ResourceLocation reference, int mountX, int mountY, int edgeY) implements IUnbakedGeometry<RailingGeometry> {
    @Override public BakedModel bake(IGeometryBakingContext context,ModelBaker baker,
        Function<Material,TextureAtlasSprite> sprites,ModelState state,ItemOverrides overrides) {
        Quaternionf rotation=new Quaternionf().rotationY((float)Math.toRadians(-mountY))
            .rotateX((float)Math.toRadians(-mountX)).rotateY((float)Math.toRadians(-edgeY))
            .rotateX((float)Math.PI);
        // The handrail must be at the free end of the posts, away from the
        // catwalk plate. Flip Deco's upright railing and fit it below y=14.
        Vector3f translation=new Vector3f(0,-.125f,0);
        new Quaternionf().rotationY((float)Math.toRadians(-mountY))
            .rotateX((float)Math.toRadians(-mountX)).transform(translation);
        Transformation transform=new Transformation(translation,rotation,null,null);
        ModelState combined=new ModelState() {
            public Transformation getRotation() {return state.getRotation().compose(transform);}
            public boolean isUvLocked() {return false;}
        };
        return baker.bake(reference,combined);
    }
    @Override public void resolveParents(Function<ResourceLocation,UnbakedModel> getter,IGeometryBakingContext context) {
        getter.apply(reference).resolveParents(getter);
    }
}
