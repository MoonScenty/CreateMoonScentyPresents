package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.crank.HandCrankRenderer;
import com.simibubi.create.content.kinetics.crank.HandCrankVisual;
import com.simibubi.create.content.kinetics.gearbox.GearboxRenderer;
import com.simibubi.create.content.kinetics.gearbox.GearboxVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModGearboxBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModKineticBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModKineticVisual;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModMillstoneBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModMillstoneRenderer;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModHandCrankBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModSifterBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModPoweredShaftBlockEntity;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRackBlockEntity;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRackRenderer;

public class ModBlockEntityTypes {
    // Create's own simple kinetic type only lists Create's blocks as valid, so the
    // cogwheels need an equivalent type of their own. The block entity class,
    // renderer and Flywheel visual are reused as they are.
    public static final BlockEntityEntry<ModKineticBlockEntity> SIMPLE_KINETIC = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("simple_kinetic", ModKineticBlockEntity::new)
            // Create's visual only knows its own cogwheel blocks; ours supplies this
            // mod's models instead.
            .visual(() -> ModKineticVisual::create, false)
            .validBlocks(ModBlocks.STONE_COGWHEEL, ModBlocks.LARGE_STONE_COGWHEEL,
                    ModBlocks.WOODEN_SHAFT, ModBlocks.BRONZE_SHAFT, ModBlocks.BRONZE_COGWHEEL)
            .renderer(() -> BracketedKineticBlockEntityRenderer::new)
            .register();

    // Create's gearbox block entity, renderer and visual, reused as they are. Only the
    // type is ours, so that our block passes Create's valid-block check.
    public static final BlockEntityEntry<ModGearboxBlockEntity> GEARBOX = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("gearbox", ModGearboxBlockEntity::new)
            .visual(() -> GearboxVisual::new)
            .validBlocks(ModBlocks.PRIMITIVE_GEARBOX)
            .renderer(() -> GearboxRenderer::new)
            .register();

    // Create's millstone block entity and renderer, with this mod's turning cog.
    public static final BlockEntityEntry<ModMillstoneBlockEntity> MILLSTONE = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("millstone", ModMillstoneBlockEntity::new)
            // true: the housing still draws as a normal block model, and only the cog
            // inside is handed to Flywheel.
            .visual(() -> ModKineticVisual::millstone, true)
            .validBlocks(ModBlocks.PRIMITIVE_MILLSTONE)
            .renderer(() -> ModMillstoneRenderer::new)
            .register();

    // Holds the one item hung on a drying rack, and draws it.
    public static final BlockEntityEntry<DryingRackBlockEntity> DRYING_RACK = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("drying_rack", DryingRackBlockEntity::new)
            .validBlocks(ModBlocks.DRYING_RACK)
            .renderer(() -> DryingRackRenderer::new)
            .register();

    // Create's powered shaft type only lists Create's block. The block entity class
    // itself is reused unchanged - the steam engine finds it by class, not by block.
    public static final BlockEntityEntry<ModPoweredShaftBlockEntity> POWERED_SHAFT = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("powered_shaft", ModPoweredShaftBlockEntity::new)
            .visual(() -> ModKineticVisual::poweredShaft, false)
            .validBlocks(ModBlocks.WOODEN_POWERED_SHAFT, ModBlocks.BRONZE_POWERED_SHAFT)
            .renderer(() -> KineticBlockEntityRenderer::new)
            .register();

    // Shares the millstone's renderer and visual: both draw a housing plus one turning
    // cog, and each looks its model up by block.
    public static final BlockEntityEntry<ModSifterBlockEntity> SIFTER = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("sifter", ModSifterBlockEntity::new)
            .visual(() -> ModKineticVisual::millstone, true)
            .validBlocks(ModBlocks.PRIMITIVE_SIFTER)
            .renderer(() -> ModMillstoneRenderer::new)
            .register();

    // Create's renderer and visual are reused; both were pointed at this mod's models
    // by overriding getRenderedHandle and by HandCrankVisualMixin.
    public static final BlockEntityEntry<ModHandCrankBlockEntity> HAND_CRANK = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("hand_crank", ModHandCrankBlockEntity::new)
            .visual(() -> HandCrankVisual::new, false)
            .validBlocks(ModBlocks.PRIMITIVE_HAND_CRANK)
            .renderer(() -> HandCrankRenderer::new)
            .register();

    // Called from the mod constructor purely to load this class, which declares
    // the entries above. Registrate handles the actual registry events.
    public static void register() {
    }
}
