package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import java.util.List;

import com.tterrag.registrate.providers.ProviderType;

import me.moonscenty.createmoonscentypresents.content.applying.ApplyingRecipe;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.CoagulatingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.TappingRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;

public class ModRecipes {
    // Sawing by hand is deliberately no better than the vanilla yield: the stone age is
    // meant to be slow, and the gain arrives later with the Mechanical Saw. Bamboo is
    // worth half a log in vanilla, and stays worth half here.
    private static final int PLANKS_PER_LOG = 4;
    private static final int PLANKS_PER_BAMBOO_BLOCK = 2;

    /** 20 seconds. Balancing comes later, like every other number in this pack. */
    private static final int DRYING_TIME = 400;

    /** Three seconds of brushing. Long enough to feel worked, short enough to hold. */
    private static final int SEALING_TIME = 60;

    // A tapper is the one thing in the age that works unattended, so it is allowed to be
    // slow: ten batches of sap at twenty five seconds each, then another fifty for it to
    // set, is five minutes for one lump of resin. That is a walk away and come back,
    // which is the point of it.
    private static final int RESIN_PER_DRAW = 100;
    private static final int DRAW_TIME = 500;
    private static final int RESIN_PER_LUMP = 1000;
    private static final int SETTING_TIME = 1000;

    private record Wood(String name, TagKey<Item> logs, ItemLike planks, int count) {
        Wood(String name, TagKey<Item> logs, ItemLike planks) {
            this(name, logs, planks, PLANKS_PER_LOG);
        }
    }

    // The per-wood log tags already cover stripped logs and wood blocks.
    private static final List<Wood> WOODS = List.of(
            new Wood("oak", ItemTags.OAK_LOGS, Items.OAK_PLANKS),
            new Wood("spruce", ItemTags.SPRUCE_LOGS, Items.SPRUCE_PLANKS),
            new Wood("birch", ItemTags.BIRCH_LOGS, Items.BIRCH_PLANKS),
            new Wood("jungle", ItemTags.JUNGLE_LOGS, Items.JUNGLE_PLANKS),
            new Wood("acacia", ItemTags.ACACIA_LOGS, Items.ACACIA_PLANKS),
            new Wood("dark_oak", ItemTags.DARK_OAK_LOGS, Items.DARK_OAK_PLANKS),
            new Wood("mangrove", ItemTags.MANGROVE_LOGS, Items.MANGROVE_PLANKS),
            new Wood("cherry", ItemTags.CHERRY_LOGS, Items.CHERRY_PLANKS),
            new Wood("crimson", ItemTags.CRIMSON_STEMS, Items.CRIMSON_PLANKS),
            new Wood("warped", ItemTags.WARPED_STEMS, Items.WARPED_PLANKS),
            new Wood("bamboo", ItemTags.BAMBOO_BLOCKS, Items.BAMBOO_PLANKS, PLANKS_PER_BAMBOO_BLOCK));

    /** Title of the JEI page for these recipes. */
    public static final String SAWING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.sawing";

    public static final String HAMMERING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.hammering";

    public static final String DRYING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.drying";

    /** How long a drying recipe takes, in seconds. */
    public static final String DRYING_TIME_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".drying.seconds";

    public static void register() {
        CreateMoonScentyPresents.REGISTRATE.addRawLang(SAWING_CATEGORY_KEY, "Sawing");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(HAMMERING_CATEGORY_KEY, "Hammering");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(DRYING_CATEGORY_KEY, "Drying");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(DRYING_TIME_KEY, "%ss");
        // The only drying recipe so far. It is the plainest case of what the rack is
        // for - a wet thing left out until it is not - and it makes the rack testable
        // before the stone age materials that will really use it exist.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "drying/sponge"),
                new DryingRecipe(Ingredient.of(Items.WET_SPONGE), new ItemStack(Items.SPONGE), DRYING_TIME),
                null));
        // The brush itself. Vanilla stacks bristles, binding and handle in a column; this
        // is the same shape in stone age materials.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.APPLICATOR_BRUSH.get())
                        .pattern("F")
                        .pattern("T")
                        .pattern("S")
                        .define('F', ModItems.PLANT_FIBER.get())
                        .define('T', ModItems.TWINE.get())
                        .define('S', Items.STICK)
                        .unlockedBy("has_twine", prov.has(ModItems.TWINE.get()))
                        .save(prov));

        // The only applying recipe so far, and the counterpart of the sponge above: the
        // plainest thing a coat of resin can do to a placed block, which makes the brush
        // testable before the stone age substances it is really for exist.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "applying/sealed_stone_bricks"),
                new ApplyingRecipe(Ingredient.of(ModItems.RESIN.get()),
                        Ingredient.of(Items.CRACKED_STONE_BRICKS), Blocks.STONE_BRICKS, SEALING_TIME),
                null));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            for (Wood wood : WOODS)
                prov.accept(ResourceLocation.fromNamespaceAndPath(
                                CreateMoonScentyPresents.MODID, "sawing/" + wood.name() + "_planks"),
                        new SawingRecipe(Ingredient.of(wood.logs()), new ItemStack(wood.planks(), wood.count())),
                        null);
        });

        registerTapping();
    }

    // --- tapping -------------------------------------------------------------

    private static void registerTapping() {
        // The drill and the bucket that hangs off it.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HAND_DRILL.get())
                        .pattern("  I")
                        .pattern(" I ")
                        .pattern("S  ")
                        .define('I', Items.IRON_INGOT)
                        .define('S', Items.STICK)
                        .unlockedBy("has_iron", prov.has(Items.IRON_INGOT))
                        .save(prov));

        // A bucket of staves with a metal spout through one side, which is what the
        // model shows and what the textures are.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TAPPER.get())
                        .pattern("P P")
                        .pattern("PNP")
                        .pattern("PPP")
                        .define('P', ItemTags.PLANKS)
                        .define('N', Items.IRON_NUGGET)
                        .unlockedBy("has_hand_drill", prov.has(ModItems.HAND_DRILL.get()))
                        .save(prov));

        // One recipe for all eight woods: sap is sap, and the tag is what the drill
        // fills. Every wood gives the same, so nothing is gained by chasing a species.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "tapping/liquid_resin"),
                new TappingRecipe(Ingredient.of(ModTags.HOLED_LOGS),
                        new FluidStack(ModFluids.LIQUID_RESIN.get().getSource(), RESIN_PER_DRAW), DRAW_TIME),
                null));

        // A full bucket sets into one lump. This is the whole of the resin supply.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "coagulating/resin"),
                new CoagulatingRecipe(
                        new FluidStack(ModFluids.LIQUID_RESIN.get().getSource(), RESIN_PER_LUMP),
                        new ItemStack(ModItems.RESIN.get()), SETTING_TIME),
                null));

        // A bored log is spent, and sawing it up is all that is left to do with it. The
        // yield is the same as a whole log: the hole cost the tree, not the timber.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            for (ModBlocks.HoledLog holed : ModBlocks.HOLED_LOGS)
                prov.accept(ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID,
                                "sawing/holed_" + holed.wood() + "_planks"),
                        new SawingRecipe(Ingredient.of(holed.block().get()),
                                new ItemStack(holed.planks(), PLANKS_PER_LOG)),
                        null);
        });
    }
}
