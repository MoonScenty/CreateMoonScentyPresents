package me.moonscenty.createmoonscentypresents;

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModKineticVisual;

public class ModBlockEntityTypes {
    // Create's own simple kinetic type only lists Create's blocks as valid, so the
    // cogwheels need an equivalent type of their own. The block entity class,
    // renderer and Flywheel visual are reused as they are.
    public static final BlockEntityEntry<BracketedKineticBlockEntity> SIMPLE_KINETIC = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("simple_kinetic", BracketedKineticBlockEntity::new)
            // Create's visual only knows its own cogwheel blocks; ours supplies this
            // mod's models instead.
            .visual(() -> ModKineticVisual::create, false)
            .validBlocks(ModBlocks.STONE_COGWHEEL, ModBlocks.LARGE_STONE_COGWHEEL,
                    ModBlocks.WOODEN_SHAFT, ModBlocks.BRONZE_SHAFT, ModBlocks.BRONZE_COGWHEEL)
            .renderer(() -> BracketedKineticBlockEntityRenderer::new)
            .register();

    // Create's powered shaft type only lists Create's block. The block entity class
    // itself is reused unchanged - the steam engine finds it by class, not by block.
    public static final BlockEntityEntry<PoweredShaftBlockEntity> POWERED_SHAFT = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("powered_shaft", PoweredShaftBlockEntity::new)
            .visual(() -> ModKineticVisual::poweredShaft, false)
            .validBlocks(ModBlocks.WOODEN_POWERED_SHAFT, ModBlocks.BRONZE_POWERED_SHAFT)
            .renderer(() -> KineticBlockEntityRenderer::new)
            .register();

    // Called from the mod constructor purely to load this class, which declares
    // the entries above. Registrate handles the actual registry events.
    public static void register() {
    }
}
