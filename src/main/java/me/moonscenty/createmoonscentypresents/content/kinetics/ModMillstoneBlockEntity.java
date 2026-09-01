package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The millstone.
 *
 * <p>Create's own class with the age's speed limit applied on top; see
 * {@link ModKineticLimits}.
 */
public class ModMillstoneBlockEntity extends MillstoneBlockEntity {

    public ModMillstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        ModKineticLimits.enforce(this);
    }
}
