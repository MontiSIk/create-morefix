package dev.kriate.catwalk.client;

import dev.kriate.catwalk.VanillaConnections;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/** Generates assets from the user's installed game, rather than bundling Minecraft assets. */
@EventBusSubscriber(modid="catwalk_orientation",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class VanillaConnectionResources {
    private static InputStream original(ResourceLocation id,String kind,String suffix)throws IOException {
        var resource=ResourceLocation.fromNamespaceAndPath(id.getNamespace(),kind+"/"+id.getPath()+suffix);
        if(id.getNamespace().equals("minecraft")){
            var supplier=Minecraft.getInstance().getVanillaPackResources().getResource(PackType.CLIENT_RESOURCES,resource);
            if(supplier==null)throw new IOException("Missing vanilla asset "+resource);return supplier.get();
        }
        return Files.newInputStream(ModList.get().getModFileById(id.getNamespace()).getFile().findResource("assets",id.getNamespace(),resource.getPath()));
    }
    private static void wrapModels(com.google.gson.JsonElement json,Path assets,String prefix,Map<String,String> models)throws IOException {
        if(json.isJsonArray()){for(var child:json.getAsJsonArray())wrapModels(child,assets,prefix,models);return;}
        if(!json.isJsonObject())return;var object=json.getAsJsonObject();
        if(object.has("model")){
            String original=object.get("model").getAsString(),own=models.get(original);
            if(own==null){String path="block/connected_"+prefix+"_part_"+models.size();own="catwalk_orientation:"+path;models.put(original,own);Files.writeString(assets.resolve("models/"+path+".json"),"{\"parent\":\""+original+"\",\"render_type\":\"minecraft:translucent\"}");}
            object.addProperty("model",own);
        }
        for(var entry:object.entrySet())wrapModels(entry.getValue(),assets,prefix,models);
    }
    @SubscribeEvent public static void packs(AddPackFindersEvent event){
        if(event.getPackType()!=PackType.CLIENT_RESOURCES)return;
        try{
            Path root=FMLPaths.CONFIGDIR.get().resolve("morefix-generated-resources"),assets=root.resolve("assets/catwalk_orientation");
            Files.createDirectories(assets.resolve("blockstates"));Files.createDirectories(assets.resolve("textures/block"));
            Files.writeString(root.resolve("pack.mcmeta"),"{\"pack\":{\"pack_format\":34,\"description\":\"MoreFix generated connected textures\"}}");
            Files.createDirectories(assets.resolve("models/block"));
            Files.writeString(assets.resolve("models/block/connected_glass.json"),"{\"parent\":\"minecraft:block/glass\",\"render_type\":\"minecraft:translucent\"}");
            Set<String> generated=new HashSet<>();
            for(var entry:VanillaConnections.IDS.entrySet()){
                var id=entry.getKey();
                boolean glass=id.getPath().endsWith("glass")||id.getPath().endsWith("_window")||VanillaConnections.PANES.contains(id);
                try(var stream=original(id,"blockstates",".json")){
                    var stateJson=com.google.gson.JsonParser.parseReader(new InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8));
                    if(glass)wrapModels(stateJson,assets,entry.getValue(),new LinkedHashMap<>());
                    Files.writeString(assets.resolve("blockstates/connected_"+entry.getValue()+".json"),stateJson.toString());
                }
                if(VanillaConnections.PANES.contains(id))Files.writeString(assets.resolve("models/"+VanillaConnections.paneModel(id).getPath()+".json"),"{\"parent\":\"minecraft:block/cube_all\",\"render_type\":\"minecraft:translucent\",\"textures\":{\"all\":\""+VanillaConnections.texture(id)+"\"}}");
                var textures=id.getPath().endsWith("froglight")?List.of(VanillaConnections.texture(id)+"_side",VanillaConnections.texture(id)+"_top"):List.of(VanillaConnections.texture(id));
                for(String texture:textures){if(!generated.add(texture))continue;
                    var textureId=ResourceLocation.parse(texture);
                    BufferedImage source;try(var stream=original(textureId,"textures",".png")){source=ImageIO.read(stream);}
                    int w=source.getWidth(),h=w,frames=source.getHeight()/h,border=(texture.contains("polished")||texture.endsWith("smooth_stone"))?Math.max(1,w/16):Math.max(1,w/8);
                    BufferedImage sheet=new BufferedImage(w*4,h*4*frames,BufferedImage.TYPE_INT_ARGB);
                    for(int frame=0;frame<frames;frame++)for(int tile=0;tile<16;tile++)for(int y=0;y<h;y++)for(int x=0;x<w;x++){
                        int sx=x,sy=y;
                        if((tile&1)!=0&&y<border||(tile&2)!=0&&y>=h-border)sy=border+Math.floorMod(y-border,h-2*border);
                        if((tile&4)!=0&&x<border||(tile&8)!=0&&x>=w-border)sx=border+Math.floorMod(x-border,w-2*border);
                        int pixel=source.getRGB(sx,frame*h+sy);
                        sheet.setRGB(tile%4*w+x,frame*h*4+tile/4*h+y,pixel);
                    }
                    ImageIO.write(sheet,"png",assets.resolve("textures/block/vanilla_"+textureId.getPath().replace('/','_')+"_connected.png").toFile());
                    if(frames>1){
                        com.google.gson.JsonObject metadata;
                        try(var stream=original(textureId,"textures",".png.mcmeta")){metadata=com.google.gson.JsonParser.parseReader(new InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}
                        catch(IOException missing){metadata=new com.google.gson.JsonObject();metadata.add("animation",new com.google.gson.JsonObject());}
                        var animation=metadata.getAsJsonObject("animation");animation.addProperty("width",w*4);animation.addProperty("height",h*4);
                        Files.writeString(assets.resolve("textures/block/vanilla_"+textureId.getPath().replace('/','_')+"_connected.png.mcmeta"),metadata.toString());
                    }
                }
            }
            // Create Deco panel bars have two texture layers; preserve the lattice beneath the panel.
            if(ModList.get().isLoaded("createdeco"))for(String metal:List.of("iron","industrial_iron","andesite","brass","zinc","copper")){
                var baseId=ResourceLocation.parse(metal.equals("iron")?"minecraft:block/iron_bars":"createdeco:block/palettes/metal_bars/"+metal+"_bars");
                var overlayId=ResourceLocation.parse("createdeco:block/palettes/metal_bars/"+metal+"_bars_overlay");
                BufferedImage base,overlay;
                try(var stream=original(baseId,"textures",".png")){base=ImageIO.read(stream);}
                try(var stream=original(overlayId,"textures",".png")){overlay=ImageIO.read(stream);}
                BufferedImage combined=new BufferedImage(base.getWidth(),base.getHeight(),BufferedImage.TYPE_INT_ARGB);
                var graphics=combined.createGraphics();graphics.drawImage(base,0,0,null);graphics.drawImage(overlay,0,0,base.getWidth(),base.getHeight(),null);graphics.dispose();
                ImageIO.write(combined,"png",assets.resolve("textures/block/copycat_"+metal+"_bars_overlay.png").toFile());
            }
            event.addRepositorySource(consumer->{var info=new PackLocationInfo("morefix_generated",Component.literal("MoreFix: connected blocks"),PackSource.BUILT_IN,Optional.empty());var pack=Pack.readMetaAndCreate(info,new PathPackResources.PathResourcesSupplier(root),PackType.CLIENT_RESOURCES,new PackSelectionConfig(true,Pack.Position.TOP,true));if(pack!=null)consumer.accept(pack);});
        }catch(IOException e){throw new IllegalStateException("Failed to generate MoreFix connected textures",e);}
    }

}

