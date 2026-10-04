package dev.kriate.catwalk.client;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

public final class StorageTextureFrame {
    private StorageTextureFrame() {}
    public static Direction tankToWorld(Axis axis,Direction local) {
        if(axis==Axis.Y)return local;
        return axis==Axis.X?Direction.fromDelta(local.getStepY(),local.getStepZ(),local.getStepX()):
            Direction.fromDelta(local.getStepX(),-local.getStepZ(),local.getStepY());
    }
    public static Direction tankToLocal(Axis axis,Direction world) {
        for(var local:Direction.values())if(tankToWorld(axis,local)==world)return local;
        throw new IllegalArgumentException();
    }
    public static Direction vaultToWorld(Direction local) {
        return Direction.fromDelta(local.getStepX(),local.getStepZ(),-local.getStepY());
    }
    public static Direction vaultToLocal(Direction world) {
        for(var local:Direction.values())if(vaultToWorld(local)==world)return local;
        throw new IllegalArgumentException();
    }
}
