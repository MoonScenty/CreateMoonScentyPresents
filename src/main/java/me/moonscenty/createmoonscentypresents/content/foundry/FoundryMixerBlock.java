package me.moonscenty.createmoonscentypresents.content.foundry;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A mixer built to stand over a foundry, for stirring one molten metal into another.
 *
 * <p>It reaches two blocks down rather than one, because a foundry has a lid between
 * the mixer and the basin - so it refuses to sit directly on a basin, which would put
 * the lid nowhere.
 *
 * <p>Nothing alloys in the stone age; this is here so the block and its recipe type
 * exist for the age that does.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public class FoundryMixerBlock extends MechanicalMixerBlock implements ICogWheel {

    public FoundryMixerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !ModBlocks.FOUNDRY_BASIN.has(level.getBlockState(pos.below()));
    }

    @Override
    public BlockEntityType<? extends FoundryMixerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.FOUNDRY_MIXER.get();
    }
}
