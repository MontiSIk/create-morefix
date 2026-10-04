package dev.kriate.catwalk.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;

/** Tiny cropped parts must not disappear when their entire UV region is a gap in the bars. */
public final class BarQuadVisibility {
    public static List<BakedQuad> repair(List<BakedQuad> quads) {
        ArrayList<BakedQuad> result=null;
        for(int i=0;i<quads.size();i++){
            BakedQuad original=quads.get(i),fixed=repair(original);
            if(fixed!=original){if(result==null)result=new ArrayList<>(quads);result.set(i,fixed);}
        }
        return result==null?quads:result;
    }
    private static BakedQuad repair(BakedQuad quad) {
        var sprite=quad.getSprite();
        if(!BarMaterials.TEXTURES.contains(sprite.contents().name()))return quad;
        var image=sprite.contents();int w=image.width(),h=image.height();
        int[] vertices=quad.getVertices();int stride=vertices.length/4;
        float minX=w,minY=h,maxX=0,maxY=0;
        for(int v=0;v<4;v++){
            float x=(Float.intBitsToFloat(vertices[v*stride+4])-sprite.getU0())/(sprite.getU1()-sprite.getU0())*w;
            float y=(Float.intBitsToFloat(vertices[v*stride+5])-sprite.getV0())/(sprite.getV1()-sprite.getV0())*h;
            minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);
        }
        int left=Math.max(0,Math.min(w-1,(int)Math.floor(minX+0.001f)));
        int right=Math.max(left,Math.min(w-1,(int)Math.ceil(maxX-0.001f)-1));
        int top=Math.max(0,Math.min(h-1,(int)Math.floor(minY+0.001f)));
        int bottom=Math.max(top,Math.min(h-1,(int)Math.ceil(maxY-0.001f)-1));
        for(int y=top;y<=bottom;y++)for(int x=left;x<=right;x++)if(!image.isTransparent(0,x,y))return quad;
        int chosenX=-1,chosenY=-1;float distance=Float.MAX_VALUE;
        for(int y=0;y<h;y++)for(int x=0;x<w;x++)if(!image.isTransparent(0,x,y)){
            float dx=x+0.5f-(minX+maxX)/2,dy=y+0.5f-(minY+maxY)/2,d=dx*dx+dy*dy;
            if(d<distance){distance=d;chosenX=x;chosenY=y;}
        }
        if(chosenX<0)return quad;
        int[] repaired=vertices.clone();
        float u=sprite.getU0()+(sprite.getU1()-sprite.getU0())*(chosenX+0.5f)/w;
        float v=sprite.getV0()+(sprite.getV1()-sprite.getV0())*(chosenY+0.5f)/h;
        for(int vertex=0;vertex<4;vertex++){repaired[vertex*stride+4]=Float.floatToRawIntBits(u);repaired[vertex*stride+5]=Float.floatToRawIntBits(v);}
        return new BakedQuad(repaired,quad.getTintIndex(),quad.getDirection(),sprite,quad.isShade());
    }
}
