package dev.kriate.catwalk.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="catwalk_orientation",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientModels {
    @SubscribeEvent public static void barsModel(ModelEvent.RegisterAdditional event){if(dev.kriate.catwalk.OptionalMods.copycats())for(var model:BarMaterials.MODELS.values())event.register(new net.minecraft.client.resources.model.ModelResourceLocation(model,"standalone"));for(var id:dev.kriate.catwalk.VanillaConnections.PANES)event.register(new net.minecraft.client.resources.model.ModelResourceLocation(dev.kriate.catwalk.VanillaConnections.paneModel(id),"standalone"));}
    @SubscribeEvent public static void copyLinkColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event){if(!dev.kriate.catwalk.OptionalMods.copycats())return;CopycatClientModels.colors(event);}
    @SubscribeEvent public static void copyLinkModels(ModelEvent.ModifyBakingResult event) {
        if(dev.kriate.catwalk.OptionalMods.deco())for(var block:net.minecraft.core.registries.BuiltInRegistries.BLOCK)if(dev.kriate.catwalk.pipes.PipeSupports.isPipe(block))for(var state:block.getStateDefinition().getPossibleStates()){
            var key=net.minecraft.client.renderer.block.BlockModelShaper.stateToModelLocation(state);var model=event.getModels().get(key);if(model!=null)event.getModels().put(key,new PipeSupportModel(model));
        }
        MaterialConnections.prepare();
        for(var id:dev.kriate.catwalk.VanillaConnections.PANES){var key=new net.minecraft.client.resources.model.ModelResourceLocation(dev.kriate.catwalk.VanillaConnections.paneModel(id),"standalone");var model=event.getModels().get(key);if(model!=null&&!(model instanceof com.simibubi.create.foundation.block.connected.CTModel))event.getModels().put(key,new com.simibubi.create.foundation.block.connected.CTModel(model,new MaterialConnections()));}
        for(var block:dev.kriate.catwalk.PlacementModes.blocks()){for(var state:block.getStateDefinition().getPossibleStates()){var key=net.minecraft.client.renderer.block.BlockModelShaper.stateToModelLocation(state);var original=event.getModels().get(key);if(original!=null){var connected=original instanceof com.simibubi.create.foundation.block.connected.CTModel?original:new com.simibubi.create.foundation.block.connected.CTModel(original,new MaterialConnections());if(dev.kriate.catwalk.VanillaConnections.PANES.contains(dev.kriate.catwalk.VanillaConnections.original(block)))connected=new PaneCullModel(connected);event.getModels().put(key,connected);}}}
        if(dev.kriate.catwalk.OptionalMods.copycats())CopycatClientModels.links(event);
    }
    @SubscribeEvent public static void register(ModelEvent.RegisterGeometryLoaders event) { MaterialConnections.prepare();
        event.register(ResourceLocation.fromNamespaceAndPath("catwalk_orientation","railing"),
            (net.neoforged.neoforge.client.model.geometry.IGeometryLoader<RailingGeometry>)(json,context) ->
            new RailingGeometry(ResourceLocation.parse(json.get("reference").getAsString()),json.get("mount_x").getAsInt(),
                json.get("mount_y").getAsInt(),json.get("edge_y").getAsInt()));
    }
}



