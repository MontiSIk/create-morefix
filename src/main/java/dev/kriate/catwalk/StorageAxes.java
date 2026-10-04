package dev.kriate.catwalk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class StorageAxes {
    private StorageAxes() {}
    public static Axis axis(BlockState state) {
        return state.hasProperty(BlockStateProperties.AXIS) ? state.getValue(BlockStateProperties.AXIS) : Axis.Y;
    }
    // Matches Create's connectivity coordinates; the controller is always the minimum corner.
    public static BlockPos offset(BlockPos origin, Axis axis, int u, int length, int v) {
        return switch (axis) {
            case X -> origin.offset(length, u, v);
            case Y -> origin.offset(u, length, v);
            case Z -> origin.offset(u, v, length);
        };
    }
    public static int size(Axis axis, Axis dimension, int width, int length) {
        return axis == dimension ? length : width;
    }
}
