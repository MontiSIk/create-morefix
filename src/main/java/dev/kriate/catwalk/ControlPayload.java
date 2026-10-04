package dev.kriate.catwalk;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

public record ControlPayload(boolean down) implements CustomPacketPayload {
    public static final Type<ControlPayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("catwalk_orientation","placement_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ControlPayload> CODEC=ByteBufCodecs.BOOL.map(ControlPayload::new,ControlPayload::down).cast();
    public Type<ControlPayload> type(){return TYPE;}
    public static void register(IEventBus bus){bus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent e)->e.registrar("1").playToServer(TYPE,CODEC,(p,c)->c.enqueueWork(()->PlacementModes.CONTROL.put(c.player().getUUID(),p.down()))));}
}
