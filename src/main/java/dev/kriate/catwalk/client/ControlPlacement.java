package dev.kriate.catwalk.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import dev.kriate.catwalk.ControlPayload;

@EventBusSubscriber(modid="catwalk_orientation",value=Dist.CLIENT)
public final class ControlPlacement {
    public static final KeyMapping CONNECT=new KeyMapping("key.catwalk_orientation.connect_blocks",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_LEFT_CONTROL,"key.categories.catwalk_orientation");
    private static Boolean sent;
    public static boolean current(){var mc=Minecraft.getInstance();boolean down=mc.screen==null&&CONNECT.isDown();if(mc.getConnection()!=null&&(sent==null||sent!=down)){PacketDistributor.sendToServer(new ControlPayload(down));sent=down;}return down;}
    @SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Pre e){current();}
    @SubscribeEvent public static void interact(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock e){if(e.getLevel().isClientSide)current();}
    @SubscribeEvent public static void logout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut e){sent=null;}
}

