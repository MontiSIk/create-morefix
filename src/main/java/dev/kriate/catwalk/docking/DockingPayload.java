package dev.kriate.catwalk.docking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
public record DockingPayload(CompoundTag data) implements CustomPacketPayload {
    public static final Type<DockingPayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("catwalk_orientation","moving_dock"));
    public static final StreamCodec<RegistryFriendlyByteBuf,DockingPayload> CODEC=ByteBufCodecs.COMPOUND_TAG.map(DockingPayload::new,DockingPayload::data).cast();
    public Type<DockingPayload> type(){return TYPE;}
    public static void register(IEventBus bus){
        bus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event)->
            event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->dev.kriate.catwalk.client.DockingRender.receivePacket(packet.data()))));
    }
}
