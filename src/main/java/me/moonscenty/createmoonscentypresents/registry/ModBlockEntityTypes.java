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
import me.moonscenty.createmoonscentypresents.content.charring.CharcoalPitBlockEntity;
import me.moonscenty.createmoonscentypresents.content.firing.PitKilnBlockEntity;
import me.moonscenty.createmoonscentypresents.content.foundry.FaucetBlockEntity;
import me.moonscenty.createmoonscentypresents.content.foundry.FoundryBasinBlockEntity;
import me.moonscenty.createmoonscentypresents.content.foundry.FoundryLidBlockEntity;
import me.moonscenty.createmoonscentypresents.content.milling.MillstoneBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModRotatingCoreRenderer;
import me.moonscenty.createmoonscentypresents.content.sifting.SifterBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModHandCrankBlockEntity;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModPoweredShaftBlockEntity;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRackBlockEntity;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRackRenderer;
import me.moonscenty.createmoonscentypresents.content.tapping.TapperBlockEntity;
import me.moonscenty.createmoonscentypresents.content.tapping.TapperRenderer;

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

    // Its own block entity: the primitive millstone is slower, reads its own list and
    // must not hand out Create's advancement. Only the drawing is shared with the
    // sifter, since both are a housing with one turning part inside.
    public static final BlockEntityEntry<MillstoneBlockEntity> MILLSTONE = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("millstone", MillstoneBlockEntity::new)
            // true: the housing still draws as a normal block model, and only the cog
            // inside is handed to Flywheel.
            .visual(() -> ModKineticVisual::rotatingCore, true)
            .validBlocks(ModBlocks.PRIMITIVE_MILLSTONE)
            .renderer(() -> ModRotatingCoreRenderer::new)
            .register();

    // Holds the load packed into a pit kiln while it burns. No renderer: the kiln is
    // closed on top, so nothing of what is inside would be visible anyway.
    public static final BlockEntityEntry<PitKilnBlockEntity> PIT_KILN = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("pit_kiln", PitKilnBlockEntity::new)
            .validBlocks(ModBlocks.PIT_KILN)
            .register();

    // The vessel and the lid that drives it. Both are Create basin machinery, so the
    // renderers, goggle overlay and belt input come with them.
    public static final BlockEntityEntry<FoundryBasinBlockEntity> FOUNDRY_BASIN =
            CreateMoonScentyPresents.REGISTRATE
                    .blockEntity("foundry_basin", FoundryBasinBlockEntity::new)
                    .validBlocks(ModBlocks.FOUNDRY_BASIN)
                    .register();

    public static final BlockEntityEntry<FoundryLidBlockEntity> FOUNDRY_LID =
            CreateMoonScentyPresents.REGISTRATE
                    .blockEntity("foundry_lid", FoundryLidBlockEntity::new)
                    .validBlocks(ModBlocks.FOUNDRY_LID)
                    .register();

    // Drains whatever it is stuck to into whatever is under it.
    public static final BlockEntityEntry<FaucetBlockEntity> FAUCET = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("faucet", FaucetBlockEntity::new)
            .validBlocks(ModBlocks.FAUCET)
            .register();

    // Holds the wood buried in a charcoal pit while it smoulders.
    public static final BlockEntityEntry<CharcoalPitBlockEntity> CHARCOAL_PIT =
            CreateMoonScentyPresents.REGISTRATE
                    .blockEntity("charcoal_pit", CharcoalPitBlockEntity::new)
                    .validBlocks(ModBlocks.CHARCOAL_PIT)
                    .register();

    // Holds the one item hung on a drying rack, and draws it.
    public static final BlockEntityEntry<DryingRackBlockEntity> DRYING_RACK = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("drying_rack", DryingRackBlockEntity::new)
            .validBlocks(ModBlocks.DRYING_RACK)
            .renderer(() -> DryingRackRenderer::new)
            .register();

    // Holds a tapper's tank and the lump that has set in it, and draws both.
    public static final BlockEntityEntry<TapperBlockEntity> TAPPER = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("tapper", TapperBlockEntity::new)
            .validBlocks(ModBlocks.TAPPER)
            .renderer(() -> TapperRenderer::new)
            .register();

    // Create's powered shaft type only lists Create's block. The block entity class
    // itself is reused unchanged - the steam engine finds it by class, not by block.
    public static final BlockEntityEntry<ModPoweredShaftBlockEntity> POWERED_SHAFT = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("powered_shaft", ModPoweredShaftBlockEntity::new)
            .visual(() -> ModKineticVisual::poweredShaft, false)
            .validBlocks(ModBlocks.WOODEN_POWERED_SHAFT, ModBlocks.BRONZE_POWERED_SHAFT)
            .renderer(() -> KineticBlockEntityRenderer::new)
            .register();

    // Its own block entity rather than the millstone's: a sifter reads a different
    // recipe list and cares whether it is standing in water. Only the drawing is
    // shared, since both are a housing with one turning part inside.
    public static final BlockEntityEntry<SifterBlockEntity> SIFTER = CreateMoonScentyPresents.REGISTRATE
            .blockEntity("sifter", SifterBlockEntity::new)
            .visual(() -> ModKineticVisual::rotatingCore, true)
            .validBlocks(ModBlocks.PRIMITIVE_SIFTER)
            .renderer(() -> ModRotatingCoreRenderer::new)
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
