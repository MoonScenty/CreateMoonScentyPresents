package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;

import me.moonscenty.createmoonscentypresents.ModBlockEntityTypes;

import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Create's cogwheel, rebound to this mod's block entity type.
 * <p>
 * Everything else - rotation, cogwheel adjacency rules, brackets, encasing - is
 * inherited unchanged. Only {@link #getBlockEntityType()} has to be overridden,
 * because Create's own type only accepts Create's blocks.
 * <p>
 * Nothing here is tied to a material: every age's cogwheel uses this class, and the
 * models are looked up per block in {@link ModPartialModels}.
 */
public class ModCogwheelBlock extends CogWheelBlock {

    protected ModCogwheelBlock(boolean large, Properties properties) {
        super(large, properties);
    }

    public static ModCogwheelBlock small(Properties properties) {
        return new ModCogwheelBlock(false, properties);
    }

    public static ModCogwheelBlock large(Properties properties) {
        return new ModCogwheelBlock(true, properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SIMPLE_KINETIC.get();
    }
}
