package me.moonscenty.createmoonscentypresents.content.bellows;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import me.moonscenty.createmoonscentypresents.content.charring.CharcoalPitBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Keeps the pit it faces blown for as long as it is turning.
 *
 * <p>Told every tick rather than once, and told to hold for only a few ticks at a time,
 * so stopping the shaft lets the fire settle back almost at once. Nothing has to notice
 * the pump stopping; the pit simply stops being told.
 *
 * <p>It does not care how fast it turns. A rung of heat is a rung of heat, and making it
 * depend on rpm would put a speed puzzle in front of the one machine whose whole point is
 * that it saves somebody standing there.
 */
public class AirPumpBlockEntity extends KineticBlockEntity {

    /** Short enough that the fire drops the moment the shaft does. */
    private static final int HOLD_TICKS = 3;

    public AirPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || getSpeed() == 0)
            return;

        BlockPos ahead = worldPosition.relative(getBlockState().getValue(AirPumpBlock.FACING));
        if (level.getBlockEntity(ahead) instanceof CharcoalPitBlockEntity pit)
            pit.blow(HOLD_TICKS);
    }
}
