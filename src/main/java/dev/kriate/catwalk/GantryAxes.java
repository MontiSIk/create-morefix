package dev.kriate.catwalk;

import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class GantryAxes {
    public static final BooleanProperty SHAFT_ALONG_MOUNT=BooleanProperty.create("shaft_along_mount");
    private GantryAxes() {}
    /** The rail and its pinion retain their original orientation in both shaft modes. */
    public static Direction.Axis pinionAxis(BlockState state) {
        return ((GantryCarriageBlock)state.getBlock()).getRotationAxis(state.setValue(SHAFT_ALONG_MOUNT,false));
    }
}
