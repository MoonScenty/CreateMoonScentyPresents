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

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "block/" + path));
    }

    public static PartialModel rotating(Block block) {
        return require(ROTATING, block, "rotating");
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
