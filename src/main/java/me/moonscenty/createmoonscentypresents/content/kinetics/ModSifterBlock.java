package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillstoneBlock;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Create's millstone, rebound to this mod's block entity type.
 * <p>
 * Mechanically a millstone - one input, ground over time by rotation - so it is built
 * on the same block rather than written again. What separates it from
 * {@link ModMillstoneBlock} is the recipe list its block entity reads.
 */
public class ModSifterBlock extends MillstoneBlock {

    public ModSifterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends MillstoneBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SIFTER.get();
    }
}
