package dev.kriate.catwalk;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CatwalkGeometry {
    private CatwalkGeometry() {}
    public static VoxelShape shape(Direction face) {
        return switch (face) {
            case UP -> Block.box(0, 14, 0, 16, 16, 16);
            case DOWN -> Block.box(0, 0, 0, 16, 2, 16);
            case NORTH -> Block.box(0, 0, 0, 16, 16, 2);
            case SOUTH -> Block.box(0, 0, 14, 16, 16, 16);
            case WEST -> Block.box(0, 0, 0, 2, 16, 16);
            case EAST -> Block.box(14, 0, 0, 16, 16, 16);
        };
    }
    public static Direction next(Direction face) {
        return switch (face) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.NORTH;
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.UP;
        };
    }
}
