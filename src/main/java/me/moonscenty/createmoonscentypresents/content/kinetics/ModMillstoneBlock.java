package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillstoneBlock;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Create's millstone, rebound to this mod's block entity type.
 * <p>
 * Everything else is inherited: the shape, the way it meshes with cogwheels, dropping
 * items in by hand, and the milling recipes it runs. Only
 * {@link #getBlockEntityType()} has to be overridden, because Create's own type only
 * accepts Create's blocks.
 *
 * @see ModGearboxBlock the same arrangement for the gearbox
 */
public class ModMillstoneBlock extends MillstoneBlock {

    public ModMillstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends MillstoneBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.MILLSTONE.get();
    }
}
