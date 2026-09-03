package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.AllPartialModels;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Flywheel renders kinetic blocks from partial models rather than the block's own
 * baked model, so the rotating parts need their own entries here.
 * <p>
 * Models are keyed by block id, which is what keeps the visual free of any per-age
 * branching: a new cogwheel or shaft only has to be listed below.
 * <p>
 * Client only - {@link PartialModel} must not be touched on a dedicated server.
 */
public class ModPartialModels {
    /** The part that spins: the whole model for shafts and small cogs. */
    private static final Map<String, PartialModel> ROTATING = new HashMap<>();
    /** Large cogs spin a shaftless body plus this shaft, offset so the teeth mesh. */
    private static final Map<String, PartialModel> COG_SHAFTS = new HashMap<>();
    /** The swinging handle of a hand crank, and the base it turns on. */
    private static final Map<String, PartialModel> CRANK_HANDLES = new HashMap<>();
    private static final Map<String, PartialModel> CRANK_BASES = new HashMap<>();
    /** The stub of shaft a gearbox draws on each face it can drive. */
    private static final Map<String, PartialModel> GEARBOX_SHAFTS = new HashMap<>();

    static {
        simple("stone_cogwheel");
        simple("wooden_shaft");
        simple("wooden_powered_shaft");
        simple("bronze_shaft");
        simple("bronze_powered_shaft");
        simple("bronze_cogwheel");
        largeCogwheel("large_stone_cogwheel", "cogwheel_shaft");
        ROTATING.put("primitive_millstone", block("primitive_millstone/inner"));
        ROTATING.put("primitive_sifter", block("primitive_sifter/inner"));
        ROTATING.put("mechanical_air_pump", block("mechanical_air_pump/cog"));
        CRANK_HANDLES.put("primitive_hand_crank", block("primitive_hand_crank/handle"));
        CRANK_BASES.put("primitive_hand_crank", block("primitive_hand_crank/block"));
        GEARBOX_SHAFTS.put("primitive_gearbox", block("wooden_shaft_half"));
    }

    /** A block whose rotating model is just its own block model. */
    private static void simple(String name) {
        ROTATING.put(name, block(name));
    }

    private static void largeCogwheel(String name, String shaftModel) {
        ROTATING.put(name, block(name + "_shaftless"));
        COG_SHAFTS.put(name, block(shaftModel));
    }

    // The bellows is not kinetic, but partial models all have to be declared in the same
    // window before the bake, so they are declared here with the rest.
    private static final PartialModel BELLOWS_BAG = block("bellows/bag");
    private static final PartialModel BELLOWS_TOP = block("bellows/top");
    private static final PartialModel BELLOWS_BOTTOM = block("bellows/bottom");

    public static PartialModel bellowsBag() {
        return BELLOWS_BAG;
    }

    public static PartialModel bellowsTop() {
        return BELLOWS_TOP;
    }

    public static PartialModel bellowsBottom() {
        return BELLOWS_BOTTOM;
    }

    // The three parts of the foundry mixer that move: the cog on top, the pole that
    // drops, and the head on the end of it.
    private static final PartialModel MIXER_COG = block("foundry_mixer/cog");
    private static final PartialModel MIXER_POLE = block("foundry_mixer/pole");
    private static final PartialModel MIXER_HEAD = block("foundry_mixer/head");

    public static PartialModel mixerCog() {
        return MIXER_COG;
    }

    public static PartialModel mixerPole() {
        return MIXER_POLE;
    }

    public static PartialModel mixerHead() {
        return MIXER_HEAD;
    }

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "block/" + path));
    }

    public static PartialModel rotating(Block block) {
        return require(ROTATING, block, "rotating");
    }

    public static PartialModel crankHandle(Block block) {
        return require(CRANK_HANDLES, block, "crank handle");
    }

    public static PartialModel crankBase(Block block) {
        return require(CRANK_BASES, block, "crank base");
    }

    public static PartialModel cogShaft(Block block) {
        return require(COG_SHAFTS, block, "cogwheel shaft");
    }

    /**
     * The shaft stub a gearbox draws on the faces it can drive.
     *
     * <p>Create draws its own gearbox with the same code this is hooked into, so the
     * question gets asked for every gearbox in the game. Anything that is not one of
     * this mod's keeps Create's own shaft.
     */
    public static PartialModel gearboxShaft(BlockState state) {
        Block block = state.getBlock();
        return block instanceof ModGearboxBlock ? require(GEARBOX_SHAFTS, block, "gearbox shaft")
                : AllPartialModels.SHAFT_HALF;
    }

    private static PartialModel require(Map<String, PartialModel> models, Block block, String what) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        PartialModel model = models.get(id.getPath());
        if (model == null)
            throw new IllegalStateException("No " + what + " model registered for " + id
                    + " - add it to ModPartialModels");
        return model;
    }

    // Must run before models are baked, so this is called from the client mod
    // constructor. Loading the class is what actually registers the models above.
    public static void init() {
    }
}
