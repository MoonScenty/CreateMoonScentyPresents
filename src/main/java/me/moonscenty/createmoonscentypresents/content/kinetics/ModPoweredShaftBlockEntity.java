package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The shaft an engine drives directly.
 *
 * <p>Create's own class with the age's speed limit applied on top; see
 * {@link ModKineticLimits}.
 */
public class ModPoweredShaftBlockEntity extends PoweredShaftBlockEntity {

    public ModPoweredShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        ModKineticLimits.enforce(this);
    }
}
