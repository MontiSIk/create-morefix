package dev.kriate.catwalk.client;

import com.simibubi.create.content.contraptions.gantry.GantryContraptionEntity;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import java.util.*;

/** Uses one client movement timeline for both halves of a moving docking joint. */
public final class DockingRender {
    private record Binding(net.minecraft.world.level.Level level,BlockEntity port,UUID owner,Vector3d local,Vector3d peer) {}
    private static final Map<UUID,Binding> BINDINGS=new java.util.concurrent.ConcurrentHashMap<>();
    public static void clear(){BINDINGS.clear();}
    public static boolean isBound(UUID sub){return BINDINGS.containsKey(sub);}
    public static void receivePacket(CompoundTag data){
        var level=net.minecraft.client.Minecraft.getInstance().level;if(level==null||!data.hasUUID("SubLevel"))return;
        BINDINGS.entrySet().removeIf(e->e.getValue().level()!=level);
        if(data.getBoolean("Remove")){BINDINGS.remove(data.getUUID("SubLevel"));return;}
        if(!data.hasUUID("Owner"))return;
        BINDINGS.put(data.getUUID("SubLevel"),new Binding(level,null,data.getUUID("Owner"),
            new Vector3d(data.getDouble("Local0"),data.getDouble("Local1"),data.getDouble("Local2")),
            new Vector3d(data.getDouble("Peer0"),data.getDouble("Peer1"),data.getDouble("Peer2"))));
    }
    public static boolean align(ClientSubLevel sub,Pose3d pose,float partial){
        var binding=BINDINGS.get(sub.getUniqueId());if(binding==null)return false;
        if(binding.level()!=sub.getLevel()||(binding.port()!=null&&binding.port().isRemoved())){BINDINGS.remove(sub.getUniqueId());return false;}
        GantryContraptionEntity owner=null;
        for(var entity:sub.getLevel().entitiesForRendering())if(entity instanceof GantryContraptionEntity g&&g.getUUID().equals(binding.owner())){owner=g;break;}
        if(owner==null||owner.isRemoved())return false;
        var local=binding.local();
        var anchor=owner.toGlobalVector(new Vec3(local.x,local.y,local.z),partial)
            .add(owner.getPosition(partial).subtract(owner.position()));
        var peer=pose.transformPosition(new Vector3d(binding.peer()));
        pose.position().add(anchor.x-peer.x,anchor.y-peer.y,anchor.z-peer.z);
        return true;
    }
}
