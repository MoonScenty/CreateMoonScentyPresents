package me.moonscenty.createmoonscentypresents.content.heat;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Reading what fire, if any, is under a station. */
public class HeatSources {

    /** The fire directly below this position. */
    public static HeatLevel below(BlockGetter level, BlockPos pos) {
        return level == null ? HeatLevel.NONE : of(level.getBlockState(pos.below()));
    }

    /**
     * What a block is worth as a fire.
     *
     * <p>The blaze burner rungs follow Create's own reading of them: a burner with
     * nothing in it smoulders and is no better than a campfire, ordinary fuel is
     * {@link HeatLevel#HEATED}, and only a blaze cake gets to
     * {@link HeatLevel#SUPERHEATED}.
     */
    public static HeatLevel of(BlockState state) {
        if (state.is(BlockTags.CAMPFIRES))
            return state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)
                    ? HeatLevel.WARM
                    : HeatLevel.NONE;

        BlazeBurnerBlock.HeatLevel burner = BlazeBurnerBlock.getHeatLevelOf(state);
        return switch (burner) {
            case NONE -> HeatLevel.NONE;
            case SMOULDERING -> HeatLevel.WARM;
            case FADING, KINDLED -> HeatLevel.HEATED;
            case SEETHING -> HeatLevel.SUPERHEATED;
        };
    }
}
