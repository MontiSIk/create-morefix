package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.pipes.*;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenuSubmitPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=RadialWrenchMenuSubmitPacket.class,remap=false)
public abstract class PipeSupportMenuPacketMixin {
    @Inject(method="handle",at=@At("HEAD"),cancellable=true)
    private void morefix$applySupport(ServerPlayer player,CallbackInfo ci){
        var packet=(RadialWrenchMenuSubmitPacket)(Object)this;var b=PipeSupports.behaviour(player.level(),packet.blockPos());
        if(b!=null&&b.support!=null){SupportRotation.submit(player,packet.blockPos(),packet.newState());ci.cancel();}
    }
}
