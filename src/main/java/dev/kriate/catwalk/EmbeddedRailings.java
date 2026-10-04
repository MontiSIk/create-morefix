package dev.kriate.catwalk;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.blocks.CatwalkRailingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class EmbeddedRailings {
    public static final BooleanProperty NORTH = BooleanProperty.create("rail_north");
    public static final BooleanProperty EAST = BooleanProperty.create("rail_east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("rail_south");
    public static final BooleanProperty WEST = BooleanProperty.create("rail_west");
    public static final Direction[] EDGES = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private EmbeddedRailings() {}
    public static BooleanProperty property(Direction edge) {
        return switch(edge) {case NORTH -> NORTH; case EAST -> EAST; case SOUTH -> SOUTH; case WEST -> WEST; default -> throw new IllegalArgumentException();};
    }
    public static int count(BlockState state) {
        int count = 0; for (Direction edge : EDGES) if (state.getValue(property(edge))) count++; return count;
    }
    public static Block railing(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_railing"));
    }
    public static BlockState attach(BlockState state, ItemStack item, BlockPos pos, Vec3 hit) {
        if (!(state.getBlock() instanceof CatwalkBlock) || !(item.getItem() instanceof BlockItem bi)
            || !(bi.getBlock() instanceof CatwalkRailingBlock) || bi.getBlock() != railing(state)) return null;
        Direction edge = edge(state, pos, hit, false);
        return state.getValue(property(edge)) ? null : state.setValue(property(edge), true);
    }
    public static Direction edge(BlockState state, BlockPos pos, Vec3 hit, boolean occupiedOnly) {
        Vec3 relative = hit.subtract(pos.getCenter());
        Direction mount = state.getValue(BlockStateProperties.FACING);
        Direction best = null; double score = -Double.MAX_VALUE;
        for (Direction edge : EDGES) {
            if (occupiedOnly && !state.getValue(property(edge))) continue;
            var n = edge.getNormal();
            Vec3 axis = transform(mount, new Vec3(n.getX(), n.getY(), n.getZ()));
            double value = relative.dot(axis);
            if (value > score) {score = value; best = edge;}
        }
        return best;
    }
    // Model rotations: R_y(-mountY) * R_x(-mountX), around the block centre.
    public static Vec3 transform(Direction mount, Vec3 v) {
        double x=v.x, y=v.y, z=v.z;
        if (mount == Direction.DOWN) {y=-y; z=-z;}
        else if (mount.getAxis().isHorizontal()) {double oldY=y; y=z; z=-oldY;}
        return switch(mount) {
            case EAST -> new Vec3(-z,y,x);
            case SOUTH -> new Vec3(-x,y,-z);
            case WEST -> new Vec3(z,y,-x);
            default -> new Vec3(x,y,z);
        };
    }
    public static VoxelShape railShape(Direction mount, Direction edge) {
        double x0=0,y0=0,z0=0,x1=1,y1=0.875,z1=1;
        switch(edge) {case NORTH -> z1=0.125; case SOUTH -> z0=0.875; case EAST -> x0=0.875; case WEST -> x1=0.125; default -> throw new IllegalArgumentException();}
        Vec3 a=transform(mount,new Vec3(x0-.5,y0-.5,z0-.5)).add(.5,.5,.5);
        Vec3 b=transform(mount,new Vec3(x1-.5,y1-.5,z1-.5)).add(.5,.5,.5);
        return Shapes.box(Math.min(a.x,b.x),Math.min(a.y,b.y),Math.min(a.z,b.z),Math.max(a.x,b.x),Math.max(a.y,b.y),Math.max(a.z,b.z));
    }
    public static VoxelShape shape(BlockState state, VoxelShape plate) {
        for(Direction edge:EDGES) if(state.getValue(property(edge))) plate=Shapes.or(plate,railShape(state.getValue(BlockStateProperties.FACING),edge));
        return plate;
    }
    public static BlockState reorient(BlockState state,Direction newMount,java.util.function.UnaryOperator<Direction> operation) {
        Direction mount=state.getValue(BlockStateProperties.FACING);
        BlockState result=state.setValue(BlockStateProperties.FACING,newMount);
        for(Direction edge:EDGES)result=result.setValue(property(edge),false);
        for(Direction edge:EDGES) {
            if(!state.getValue(property(edge)))continue;
            var n=edge.getNormal(); var vector=transform(mount,new Vec3(n.getX(),n.getY(),n.getZ()));
            Direction world=Direction.getNearest(vector.x,vector.y,vector.z);
            Direction mapped=operation.apply(world);
            for(Direction candidate:EDGES) {
                var c=candidate.getNormal(); var v=transform(newMount,new Vec3(c.getX(),c.getY(),c.getZ()));
                if(Direction.getNearest(v.x,v.y,v.z)==mapped)result=result.setValue(property(candidate),true);
            }
        }
        return result;
    }
}
