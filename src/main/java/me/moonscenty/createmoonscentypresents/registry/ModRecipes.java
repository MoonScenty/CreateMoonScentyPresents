package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.network.ToggleSatchelPayload;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.function.Supplier;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import me.moonscenty.createmoonscentypresents.content.casting.CastingRecipe;
import me.moonscenty.createmoonscentypresents.content.charring.CharringRecipe;
import me.moonscenty.createmoonscentypresents.content.firing.FiringRecipe;
import me.moonscenty.createmoonscentypresents.content.foundry.FoundryRecipe;
import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;
import me.moonscenty.createmoonscentypresents.content.milling.MillingRecipe;
import me.moonscenty.createmoonscentypresents.content.sifting.SiftingRecipe;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;

import me.moonscenty.createmoonscentypresents.content.applying.ApplyingRecipe;
import me.moonscenty.createmoonscentypresents.content.hammering.HammeringRecipe;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;
import me.moonscenty.createmoonscentypresents.content.shaping.ShapingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.CoagulatingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.TappingRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class ModRecipes {
    // Sawing by hand is deliberately no better than the vanilla yield: the stone age is
    // meant to be slow, and the gain arrives later with the Mechanical Saw. Bamboo is
    // worth half a log in vanilla, and stays worth half here.
    private static final int PLANKS_PER_LOG = 4;
    private static final int PLANKS_PER_BAMBOO_BLOCK = 2;
    private static final int STAVES_PER_PLANK = 2;
    private static final int FIBER_PER_TWINE = 3;
    /** Twice what the hammer gives, which is what the millstone is for. */
    private static final int GRIT_PER_MILLING = 2;
    /** Matches Create's own andesite milling, so the pace reads as familiar. */
    private static final int MILLING_TIME = 200;
    /** Per item in the load, not per load: eight pieces take eight times as long. */
    private static final int FIRING_TIME = 600;
    private static final int FIRE_BRICKS_PER_CRAFT = 4;
    /** Per log. Slower than a furnace, which is the price of not needing fuel. */
    private static final int CHARRING_TIME = 400;
    /** One raw ore is one ingot of metal; washing it first gets half again. */
    private static final int METAL_PER_RAW_ORE = 90;
    private static final int METAL_PER_CONCENTRATE = 135;
    private static final int METAL_PER_INGOT = 90;
    private static final int MELTING_TIME = 200;
    private static final int SIFTING_TIME = 250;
    /** Dressing without water loses half the ore. */
    private static final float DRY_DRESSING = 0.5f;
    /**
     * One raw tin in four washes of iron.
     *
     * <p>Rare enough that bronze is worked towards rather than stumbled into, common
     * enough that it is not a lottery. It is also the only source of tin the pack has,
     * so this number is what the whole bronze age is paced by.
     */
    private static final float TIN_FROM_IRON = 0.25f;
    private static final float CLAY_FROM_SAND = 0.25f;
    /** Long enough to watch it go dull, short enough not to be a wait. */
    private static final int CASTING_TIME = 120;

    // One tin to three copper, measured in the same units an ingot is worth - so a batch
    // is one tin ingot and three copper ingots, and four bronze come back out. Nothing
    // is lost in the mixing: what makes bronze expensive is the tin.
    private static final int TIN_PER_BRONZE = METAL_PER_INGOT;
    private static final int COPPER_PER_BRONZE = METAL_PER_INGOT * 3;
    /** Longer than melting - this is the last thing the age does, and it is turned by hand. */
    private static final int ALLOYING_TIME = 300;

    /** 20 seconds. Balancing comes later, like every other number in this pack. */
    private static final int DRYING_TIME = 400;

    /** Three seconds of brushing. Long enough to feel worked, short enough to hold. */
    private static final int SEALING_TIME = 60;
    /** Longer than sealing: this is the gate, and the cost is the standing there. */
    private static final int CASING_TIME = 200;

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

    public static final String SHAPING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.shaping";

    public static final String APPLYING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.applying";

    public static final String TAPPING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.tapping";

    public static final String COAGULATING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.coagulating";

    public static final String FIRING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.firing";

    public static final String CHARRING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.charring";

    public static final String MELTING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.melting";

    public static final String CASTING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.casting";

    public static final String SIFTING_CATEGORY_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".category.sifting";

    /** Whether a sifter has to be standing in water. */
    public static final String SIFTER_WET_KEY = "gui." + CreateMoonScentyPresents.MODID + ".sifter_wet";
    public static final String SIFTER_DRY_KEY = "gui." + CreateMoonScentyPresents.MODID + ".sifter_dry";

    /** What fire a recipe wants, and what a mould is left as. */
    public static final String NEEDS_HEAT_KEY = "gui." + CreateMoonScentyPresents.MODID + ".needs_heat";
    public static final String ANY_FIRE_KEY = "gui." + CreateMoonScentyPresents.MODID + ".any_fire";
    public static final String MOLD_KEPT_KEY = "gui." + CreateMoonScentyPresents.MODID + ".mold_kept";
    public static final String MOLD_CONSUMED_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".mold_consumed";

    /** How long a timed recipe takes, in seconds. Shared by every category that waits. */
    public static final String TIME_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".seconds";

    public static void register() {
        CreateMoonScentyPresents.REGISTRATE.addRawLang(SAWING_CATEGORY_KEY, "Sawing");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(HAMMERING_CATEGORY_KEY, "Hammering");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(DRYING_CATEGORY_KEY, "Drying");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(SHAPING_CATEGORY_KEY, "Shaping");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(APPLYING_CATEGORY_KEY, "Applying");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(TAPPING_CATEGORY_KEY, "Tapping");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(COAGULATING_CATEGORY_KEY, "Coagulating");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(FIRING_CATEGORY_KEY, "Firing");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(CHARRING_CATEGORY_KEY, "Charring");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(MELTING_CATEGORY_KEY, "Melting");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(CASTING_CATEGORY_KEY, "Casting");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(SIFTING_CATEGORY_KEY, "Sifting");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(SIFTER_WET_KEY, "The sifter must stand in water");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(SIFTER_DRY_KEY, "The sifter must be dry");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(NEEDS_HEAT_KEY, "Needs %s");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(ANY_FIRE_KEY, "a fire");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(MOLD_KEPT_KEY, "The mould is kept");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(MOLD_CONSUMED_KEY, "The mould is broken");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(TIME_KEY, "%ss");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(ToggleSatchelPayload.KEY_CATEGORY, "Create: MoonScenty Presents");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(ToggleSatchelPayload.KEY_NAME,
                "Toggle Gatherer's Satchel");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(ToggleSatchelPayload.ON_KEY,
                "Gatherer's Satchel: drawing items in");
        CreateMoonScentyPresents.REGISTRATE.addRawLang(ToggleSatchelPayload.OFF_KEY,
                "Gatherer's Satchel: off");
        // The only drying recipe so far. It is the plainest case of what the rack is
        // for - a wet thing left out until it is not - and it makes the rack testable
        // before the stone age materials that will really use it exist.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "drying/sponge"),
                new DryingRecipe(Ingredient.of(Items.WET_SPONGE), new ItemStack(Items.SPONGE), DRYING_TIME),
                null));
        // The very start of the pack: everything a bare hand can pull off a plant. Three
        // recipes rather than one compound ingredient so JEI names each source.
        fiber("vine", Ingredient.of(Items.VINE));
        fiber("grass", Ingredient.of(Items.SHORT_GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN));
        fiber("sapling", Ingredient.of(ItemTags.SAPLINGS));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.TWINE.get())
                        .requires(ModItems.PLANT_FIBER.get(), FIBER_PER_TWINE)
                        .unlockedBy("has_plant_fiber", prov.has(ModItems.PLANT_FIBER.get()))
                        .save(prov));

        // The three worked-by-hand tools. All of them are a working end bound to a handle
        // with twine, and they share that column so the family reads at a glance.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WOODEN_SAW.get())
                        .pattern("FP")
                        .pattern("TP")
                        .define('F', Items.FLINT)
                        .define('P', ItemTags.PLANKS)
                        .define('T', ModItems.TWINE.get())
                        .unlockedBy("has_twine", prov.has(ModItems.TWINE.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STONE_HAMMER.get())
                        .pattern("ATA")
                        .pattern(" S ")
                        .pattern(" S ")
                        .define('A', Blocks.ANDESITE)
                        .define('S', Items.STICK)
                        .define('T', ModItems.TWINE.get())
                        .unlockedBy("has_twine", prov.has(ModItems.TWINE.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STONE_CHISEL.get())
                        .pattern("F")
                        .pattern("T")
                        .pattern("S")
                        .define('F', Items.FLINT)
                        .define('S', Items.STICK)
                        .define('T', ModItems.TWINE.get())
                        .unlockedBy("has_twine", prov.has(ModItems.TWINE.get()))
                        .save(prov));

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
        registerKinetics();
        registerMilling();
        registerSifting();
        registerClay();
        registerFoundry();
        registerMelting();
        registerAlloying();
        registerCasting();
        registerRewards();
        registerGates();
    }

    // --- stone age kinetics ---------------------------------------------------

    /**
     * The parts the age actually turns on. They mirror Create's own shapes so the two
     * sets read as the same family, with our materials in place of alloy and casing -
     * a wooden shaft where Create uses its own, stone where it uses planks, and a
     * wooden bearing where it uses andesite casing.
     */
    private static void registerKinetics() {
        // The one part of the age that takes metal. A bearing is where a machine wears
        // out, so it is where the alloy belongs - and putting it here is what keeps the
        // hammering worth doing: shafts and cogwheels stay cheap, but every gearbox and
        // every millstone goes through a bearing, and so through the alloy line.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOODEN_BEARING.get())
                        .pattern(" T ")
                        .pattern("PAP")
                        .pattern(" T ")
                        .define('P', ItemTags.PLANKS)
                        .define('T', ModItems.TWINE.get())
                        .define('A', AllItems.ANDESITE_ALLOY.get())
                        .unlockedBy("has_andesite_alloy", prov.has(AllItems.ANDESITE_ALLOY.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.STONE_COGWHEEL.get())
                        .requires(ModBlocks.WOODEN_SHAFT.get())
                        .requires(Tags.Items.STONES)
                        .unlockedBy("has_wooden_shaft", prov.has(ModBlocks.WOODEN_SHAFT.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.LARGE_STONE_COGWHEEL.get())
                        .requires(ModBlocks.WOODEN_SHAFT.get())
                        .requires(Tags.Items.STONES)
                        .requires(Tags.Items.STONES)
                        .unlockedBy("has_wooden_shaft", prov.has(ModBlocks.WOODEN_SHAFT.get()))
                        .save(prov));

        // A bearing cased in wood and packed with stone. The gearbox is the only thing
        // that needs one, but it is what makes the gearbox more than four cogwheels
        // around a bearing - the same step bronze will take with its own component.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WOODEN_GEARBOX_COMPONENT.get())
                        .requires(ModItems.WOODEN_BEARING.get())
                        .requires(Tags.Items.STONES)
                        .unlockedBy("has_wooden_bearing", prov.has(ModItems.WOODEN_BEARING.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PRIMITIVE_GEARBOX.get())
                        .pattern(" C ")
                        .pattern("CGC")
                        .pattern(" C ")
                        .define('C', ModBlocks.STONE_COGWHEEL.get())
                        .define('G', ModItems.WOODEN_GEARBOX_COMPONENT.get())
                        .unlockedBy("has_wooden_gearbox_component",
                                prov.has(ModItems.WOODEN_GEARBOX_COMPONENT.get()))
                        .save(prov));

        // The vertical one is the same block on a different axis, so it is a conversion
        // rather than a build, and it goes both ways.
        gearboxConversion("primitive_vertical_gearbox_from_conversion",
                ModItems.PRIMITIVE_VERTICAL_GEARBOX, ModBlocks.PRIMITIVE_GEARBOX);
        gearboxConversion("primitive_gearbox_from_conversion",
                ModBlocks.PRIMITIVE_GEARBOX, ModItems.PRIMITIVE_VERTICAL_GEARBOX);

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PRIMITIVE_HAND_CRANK.get())
                        .pattern("PPP")
                        .pattern("  S")
                        .define('P', ItemTags.PLANKS)
                        .define('S', ModBlocks.WOODEN_SHAFT.get())
                        .unlockedBy("has_wooden_shaft", prov.has(ModBlocks.WOODEN_SHAFT.get()))
                        .save(prov));

        // Same bones as the millstone with a mesh instead of a stone: a frame, a
        // bearing under it and something to shake.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PRIMITIVE_SIFTER.get())
                        .pattern("C")
                        .pattern("B")
                        .pattern("P")
                        .define('C', ModBlocks.STONE_COGWHEEL.get())
                        .define('B', ModItems.WOODEN_BEARING.get())
                        .define('P', ItemTags.PLANKS)
                        .unlockedBy("has_stone_cogwheel", prov.has(ModBlocks.STONE_COGWHEEL.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PRIMITIVE_MILLSTONE.get())
                        .pattern("C")
                        .pattern("B")
                        .pattern("S")
                        .define('C', ModBlocks.STONE_COGWHEEL.get())
                        .define('B', ModItems.WOODEN_BEARING.get())
                        .define('S', Tags.Items.STONES)
                        .unlockedBy("has_stone_cogwheel", prov.has(ModBlocks.STONE_COGWHEEL.get()))
                        .save(prov));
    }

    // --- the sifter -----------------------------------------------------------

    /**
     * What the primitive sifter separates, and why standing it in water matters.
     *
     * <p>Washing and dry shaking are different jobs. Water carries the light stuff away
     * and leaves what is heavy, so a sifter in a stream is how metal is found; shaken
     * dry it only breaks gravel down into what gravel is made of.
     *
     * <p>The wet side gives <em>raw</em> zinc, never nuggets. Nuggets would let a sifter
     * feed the alloy directly and put the whole foundry out of a job; raw ore still has
     * to be melted, so the sifter is a second way to find zinc rather than a way around
     * smelting it.
     *
     * <p>Sand into clay is the one that matters most. Every vessel, mould and fire brick
     * is clay, and clay is a thing you find rather than grow - without this the foundry
     * line runs out with the nearest lake bed.
     */
    private static void registerSifting() {
        // Washed properly, the whole ore comes through as concentrate.
        sifting("zinc_concentrate", true, Ingredient.of(AllTags.commonItemTag("raw_materials/zinc")),
                builder -> builder.output(ModItems.ZINC_CONCENTRATE.get()));

        // Shaken dry, half of it goes over the side as dust.
        sifting("zinc_concentrate_dry", false, Ingredient.of(AllTags.commonItemTag("raw_materials/zinc")),
                builder -> builder.output(DRY_DRESSING, ModItems.ZINC_CONCENTRATE.get()));

        // Iron goes through the same washing, and this is where tin comes from.
        //
        // Tin has no ore of its own to find, so it is found beside something else - and
        // iron is the ore this age already has every reason to be digging. Washing it is
        // therefore the whole route to bronze, which is why the byproduct is on the wet
        // side only: water is what separates one metal from another, and a dry shake
        // just breaks the rock up.
        //
        // Raw tin rather than a concentrate or an ingot. It goes back through this same
        // sifter to be washed like any other ore, so finding tin and refining tin stay
        // two different jobs.
        sifting("iron_concentrate", true, Ingredient.of(Tags.Items.RAW_MATERIALS_IRON),
                builder -> builder.output(ModItems.IRON_CONCENTRATE.get())
                        .output(TIN_FROM_IRON, ModItems.RAW_TIN.get()));

        sifting("iron_concentrate_dry", false, Ingredient.of(Tags.Items.RAW_MATERIALS_IRON),
                builder -> builder.output(DRY_DRESSING, ModItems.IRON_CONCENTRATE.get()));

        sifting("tin_concentrate", true, Ingredient.of(CommonMetal.TIN.rawOres),
                builder -> builder.output(ModItems.TIN_CONCENTRATE.get()));

        sifting("tin_concentrate_dry", false, Ingredient.of(CommonMetal.TIN.rawOres),
                builder -> builder.output(DRY_DRESSING, ModItems.TIN_CONCENTRATE.get()));

        // Copper is the bulk of bronze, so washing it is where the last of the age is
        // spent - three of these for every one tin.
        sifting("copper_concentrate", true, Ingredient.of(Tags.Items.RAW_MATERIALS_COPPER),
                builder -> builder.output(ModItems.COPPER_CONCENTRATE.get()));

        sifting("copper_concentrate_dry", false, Ingredient.of(Tags.Items.RAW_MATERIALS_COPPER),
                builder -> builder.output(DRY_DRESSING, ModItems.COPPER_CONCENTRATE.get()));

        // Not ore dressing, but the same washing: sand gives up the clay in it, which is
        // what keeps a foundry in crucibles once the nearest lake bed is dug out.
        sifting("clay_from_sand", true, Ingredient.of(ItemTags.SAND),
                builder -> builder.output(CLAY_FROM_SAND, Items.CLAY_BALL));
    }

    /**
     * @param wet whether the sifter has to be standing in water for this
     */
    private static void sifting(String name, boolean wet, Ingredient input,
            UnaryOperator<StandardProcessingRecipe.Builder<SiftingRecipe>> outputs) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    CreateMoonScentyPresents.MODID, "sifting/" + name);
            StandardProcessingRecipe.Builder<SiftingRecipe> builder =
                    new StandardProcessingRecipe.Builder<>(SiftingRecipe::new, id)
                            .withItemIngredients(input)
                            .duration(SIFTING_TIME);
            // The water condition is ours, so the recipe is rebuilt around the params
            // Create's builder produces rather than being set on the builder itself.
            SiftingRecipe base = outputs.apply(builder).build();
            prov.accept(id, new SiftingRecipe(base.getParams(), Optional.of(wet)), null);
        });
    }

    // --- clay and the kiln ----------------------------------------------------

    /**
     * The clay line: shaped wet by hand, then packed into a pit kiln and burnt hard.
     *
     * <p>Every vessel the age melts metal in comes from here, which is what gives the
     * kiln something to do before the crucible exists.
     */
    private static void registerClay() {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UNFIRED_CRUCIBLE.get())
                        .pattern("C C")
                        .pattern("C C")
                        .pattern("CCC")
                        .define('C', Items.CLAY_BALL)
                        .unlockedBy("has_clay_ball", prov.has(Items.CLAY_BALL))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UNFIRED_INGOT_MOLD.get())
                        .pattern("C C")
                        .pattern("CCC")
                        .define('C', Items.CLAY_BALL)
                        .unlockedBy("has_clay_ball", prov.has(Items.CLAY_BALL))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UNFIRED_FIRE_BRICK.get(),
                                FIRE_BRICKS_PER_CRAFT)
                        .pattern("CC")
                        .pattern("CC")
                        .define('C', Items.CLAY_BALL)
                        .unlockedBy("has_clay_ball", prov.has(Items.CLAY_BALL))
                        .save(prov));

        // Wood is charred rather than fired, so it needs the buried pit and cannot be
        // shortcut through the open kiln. One log, one piece of charcoal.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "charring/charcoal"),
                new CharringRecipe(Ingredient.of(ItemTags.LOGS_THAT_BURN),
                        new ItemStack(Items.CHARCOAL), CHARRING_TIME, HeatLevel.WARM),
                null));

        // The unfired crucible fires into the foundry basin itself: the basin is what a
        // crucible turned out to be once it had to actually hold and pour metal.
        firing("foundry_basin", ModItems.UNFIRED_CRUCIBLE, () -> new ItemStack(ModBlocks.FOUNDRY_BASIN.get()));
        firing("ingot_mold", ModItems.UNFIRED_INGOT_MOLD, () -> new ItemStack(ModItems.INGOT_MOLD.get()));
        firing("fire_brick", ModItems.UNFIRED_FIRE_BRICK, () -> new ItemStack(ModItems.FIRE_BRICK.get()));
    }

    /** The result is a supplier: items are not bound yet when this is called. */
    private static void firing(String name, ItemEntry<?> input, Supplier<ItemStack> result) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "firing/" + name),
                new FiringRecipe(Ingredient.of(input.get()), result.get(), FIRING_TIME, HeatLevel.WARM),
                null));
    }

    /**
     * What the primitive millstone grinds. The one recipe the age needs: the same
     * andesite the hammer breaks up, for twice as much - which is the whole reason to
     * build the crank and the millstone rather than keep swinging.
     */
    private static void registerMilling() {
        milling("andesite_grit", Ingredient.of(Blocks.ANDESITE),
                () -> new ItemStack(ModItems.ANDESITE_GRIT.get(), GRIT_PER_MILLING), MILLING_TIME);
    }

    /** The result is a supplier: items are not bound yet when this is called. */
    private static void milling(String name, Ingredient input, Supplier<ItemStack> result, int duration) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    CreateMoonScentyPresents.MODID, "milling/" + name);
            prov.accept(id, new StandardProcessingRecipe.Builder<>(MillingRecipe::new, id)
                    .withItemIngredients(input)
                    .withSingleItemOutput(result.get())
                    .duration(duration)
                    .build(), null);
        });
    }

    private static void gearboxConversion(String name, Supplier<? extends ItemLike> result,
            Supplier<? extends ItemLike> from) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get())
                        .requires(from.get())
                        .unlockedBy("has_primitive_gearbox", prov.has(ModBlocks.PRIMITIVE_GEARBOX.get()))
                        .save(prov, ResourceLocation.fromNamespaceAndPath(
                                CreateMoonScentyPresents.MODID, name)));
    }

    // --- the foundry ----------------------------------------------------------

    /**
     * What the foundry basin melts under a closed lid.
     *
     * <p>Zinc is the metal this age turns on: the alloy needs its nuggets, and Create
     * ordinarily smelts them in a furnace - a path this pack closes, so the foundry is
     * the only way to any. It melts over a plain fire, which is true of the metal and
     * also what makes the age possible: a blaze burner is on the far side of the Nether,
     * and gating zinc behind one would gate the whole age behind it.
     */
    private static void registerMelting() {
        melting("zinc_from_raw", AllTags.commonItemTag("raw_materials/zinc"), HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_ZINC.get().getSource(), METAL_PER_RAW_ORE));
        // Half again as much for having washed it first. That is what a sifter is for -
        // not finding ore, but getting more out of the ore already dug.
        melting("zinc_from_concentrate", ModTags.ZINC_CONCENTRATES, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_ZINC.get().getSource(), METAL_PER_CONCENTRATE));
        melting("zinc_from_ingot", AllTags.commonItemTag("ingots/zinc"), HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_ZINC.get().getSource(), METAL_PER_INGOT));

        // Tin melts lower than zinc does, so the same fire that makes the alloy makes
        // this. Nothing new is needed to pour it - only the tin itself, which is the
        // hard part.
        melting("tin_from_raw", CommonMetal.TIN.rawOres, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_TIN.get().getSource(), METAL_PER_RAW_ORE));
        melting("tin_from_concentrate", ModTags.TIN_CONCENTRATES, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_TIN.get().getSource(), METAL_PER_CONCENTRATE));
        melting("tin_from_ingot", CommonMetal.TIN.ingots, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_TIN.get().getSource(), METAL_PER_INGOT));

        // Copper on a plain fire, which is the one place the real melting point is not
        // followed: 1085 degrees is a blaze burner, and a blaze burner is the Nether.
        // Gating bronze behind the Nether would end the age exactly the way gating zinc
        // behind it would have, so copper is let through on the same grounds as zinc and
        // the ladder keeps its shape everywhere else.
        melting("copper_from_raw", Tags.Items.RAW_MATERIALS_COPPER, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_COPPER.get().getSource(), METAL_PER_RAW_ORE));
        melting("copper_from_concentrate", ModTags.COPPER_CONCENTRATES, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_COPPER.get().getSource(), METAL_PER_CONCENTRATE));
        melting("copper_from_ingot", Tags.Items.INGOTS_COPPER, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_COPPER.get().getSource(), METAL_PER_INGOT));

        // Bronze back into the pot. Nothing produces bronze but the mixer, so this is
        // only a way to undo a casting - which is worth having when the tin in it is the
        // scarcest thing in the age.
        melting("bronze_from_ingot", ModTags.BRONZE_INGOTS, HeatCondition.NONE,
                () -> new FluidStack(ModFluids.MOLTEN_BRONZE.get().getSource(), METAL_PER_INGOT));

        // Iron asks for a blaze burner and so belongs to the next age. It is written now
        // because the rest of its line is: washing iron is how tin is found, and the
        // concentrate that comes out of that has to be worth keeping.
        //
        // Vanilla smelting is untouched, so nobody is waiting on this for iron - it is
        // the better path, not the only one.
        melting("iron_from_raw", Tags.Items.RAW_MATERIALS_IRON, HeatCondition.HEATED,
                () -> new FluidStack(ModFluids.MOLTEN_IRON.get().getSource(), METAL_PER_RAW_ORE));
        melting("iron_from_concentrate", ModTags.IRON_CONCENTRATES, HeatCondition.HEATED,
                () -> new FluidStack(ModFluids.MOLTEN_IRON.get().getSource(), METAL_PER_CONCENTRATE));
        melting("iron_from_ingot", Tags.Items.INGOTS_IRON, HeatCondition.HEATED,
                () -> new FluidStack(ModFluids.MOLTEN_IRON.get().getSource(), METAL_PER_INGOT));
    }

    /** The result is a supplier: fluids are not bound yet when this is called. */
    private static void melting(String name, TagKey<Item> input, HeatCondition heat,
            Supplier<FluidStack> result) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    CreateMoonScentyPresents.MODID, "melting/" + name);
            prov.accept(id, new StandardProcessingRecipe.Builder<>(
                    params -> new FoundryRecipe(ModRecipeTypes.MELTING_INFO, params), id)
                            .require(input)
                            .output(result.get())
                            .requiresHeat(heat)
                            .duration(MELTING_TIME)
                            .build(), null);
        });
    }

    /**
     * The foundry itself, built out of fired clay and iron.
     *
     * <p>All of it has to be reachable before any metal is: the foundry is what makes
     * the first zinc, so nothing here can ask for zinc or for the alloy. Fire brick is
     * what it is made of, which is what fire brick is for - until now the kiln fired it
     * and nothing wanted it.
     *
     * <p>The basin is not here: an unfired crucible fires into one, so the clay line
     * reaches it without a bench recipe.
     */
    private static void registerFoundry() {
        // Boards, a hide between them and a metal nozzle - which is what the three
        // textures on the model are. It belongs to the charcoal pit rather than the
        // foundry, but it is a bench recipe for a station and this is where those live.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BELLOWS.get())
                        .pattern("PLP")
                        .pattern("PLP")
                        .pattern("PNP")
                        .define('P', ItemTags.PLANKS)
                        .define('L', Items.LEATHER)
                        .define('N', Items.IRON_NUGGET)
                        .unlockedBy("has_leather", prov.has(Items.LEATHER))
                        .save(prov));

        // A heavy flat cap with an iron hinge, to shut the heat in.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FOUNDRY_LID.get())
                        .pattern("FFF")
                        .pattern("FIF")
                        .define('F', ModItems.FIRE_BRICK.get())
                        .define('I', Items.IRON_INGOT)
                        .unlockedBy("has_fire_brick", prov.has(ModItems.FIRE_BRICK.get()))
                        .save(prov));

        // A short lined spout on an iron neck.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FAUCET.get())
                        .pattern(" F")
                        .pattern("II")
                        .define('F', ModItems.FIRE_BRICK.get())
                        .define('I', Items.IRON_INGOT)
                        .unlockedBy("has_fire_brick", prov.has(ModItems.FIRE_BRICK.get()))
                        .save(prov));

        // A lined top on iron legs.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CASTING_TABLE.get())
                        .pattern("FFF")
                        .pattern("I I")
                        .define('F', ModItems.FIRE_BRICK.get())
                        .define('I', Items.IRON_INGOT)
                        .unlockedBy("has_fire_brick", prov.has(ModItems.FIRE_BRICK.get()))
                        .save(prov));

        // Built from the age's own parts rather than by lining Create's mixer. Lining
        // one read well but could never be made here: Create's mixer wants a shaft, and
        // shafts are what the second gate closes. This is the same machine described in
        // stone age terms - a fire brick body, an iron whisk, and the shaft and bearing
        // every other turning thing in the age is built from.
        //
        // The bearing is what makes it late: it costs an andesite alloy, so the mixer
        // cannot be reached before the first gate has been worked through by hand.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FOUNDRY_MIXER.get())
                        .pattern("FSF")
                        .pattern("FIF")
                        .pattern(" B ")
                        .define('F', ModItems.FIRE_BRICK.get())
                        .define('S', ModBlocks.WOODEN_SHAFT.get())
                        .define('I', Items.IRON_INGOT)
                        .define('B', ModItems.WOODEN_BEARING.get())
                        .unlockedBy("has_wooden_bearing", prov.has(ModItems.WOODEN_BEARING.get()))
                        .save(prov));
    }

    // --- casting ---------------------------------------------------------------

    /**
     * What the moulds on a casting table are worth.
     *
     * <p>The ingot mould is fired clay and survives being knocked out, so it is not
     * consumed - one mould casts as many ingots as you can pour into it.
     */
    // --- alloying --------------------------------------------------------------

    /**
     * The only thing the foundry mixer makes, and the end of the age.
     *
     * <p>One part tin to three of copper, poured rather than stirred: both go in as
     * metal that is already melted, which is why the mixer sits on a basin and not on a
     * bench. Nothing is lost - four ingots in, four out - because the cost is not in the
     * mixing but in the tin, which only comes out of washed iron.
     *
     * <p>It needs turning, and a hand crank at 32 RPM is exactly enough for one mixer.
     * That is the last thing the age asks for: everything built up to here, used at once.
     */
    private static void registerAlloying() {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    CreateMoonScentyPresents.MODID, "alloying/bronze");
            prov.accept(id, new StandardProcessingRecipe.Builder<>(
                    params -> new FoundryRecipe(ModRecipeTypes.ALLOYING_INFO, params), id)
                            .require(SizedFluidIngredient.of(
                                    ModFluids.MOLTEN_TIN.get().getSource(), TIN_PER_BRONZE))
                            .require(SizedFluidIngredient.of(
                                    ModFluids.MOLTEN_COPPER.get().getSource(), COPPER_PER_BRONZE))
                            .output(new FluidStack(ModFluids.MOLTEN_BRONZE.get().getSource(),
                                    TIN_PER_BRONZE + COPPER_PER_BRONZE))
                            .duration(ALLOYING_TIME)
                            .build(), null);
        });
    }

    // --- casting ---------------------------------------------------------------

    private static void registerCasting() {
        casting("zinc_ingot", () -> ModFluids.MOLTEN_ZINC.get().getSource(),
                () -> new ItemStack(AllItems.ZINC_INGOT.get()));
        casting("bronze_ingot", () -> ModFluids.MOLTEN_BRONZE.get().getSource(),
                () -> new ItemStack(ModItems.BRONZE_INGOT.get()));
        casting("copper_ingot", () -> ModFluids.MOLTEN_COPPER.get().getSource(),
                () -> new ItemStack(Items.COPPER_INGOT));
        casting("tin_ingot", () -> ModFluids.MOLTEN_TIN.get().getSource(),
                () -> new ItemStack(ModItems.TIN_INGOT.get()));
        // For the age that can melt it; see registerMelting.
        casting("iron_ingot", () -> ModFluids.MOLTEN_IRON.get().getSource(),
                () -> new ItemStack(Items.IRON_INGOT));
    }

    /** Suppliers again: neither fluids nor items are bound when this is called. */
    private static void casting(String name, Supplier<Fluid> metal, Supplier<ItemStack> result) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "casting/" + name),
                new CastingRecipe(
                        SizedFluidIngredient.of(metal.get(), METAL_PER_INGOT),
                        Ingredient.of(ModItems.INGOT_MOLD.get()),
                        result.get(),
                        CASTING_TIME, false),
                null));
    }

    // --- age rewards ----------------------------------------------------------

    /** Not handed out - made from what this age already gathers. */
    private static void registerRewards() {
        // Buildable before the first gear ratio is: it asks for nothing that turns, so
        // the warning is in hand before anything can be overspun.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.APPRENTICE_GOGGLES.get())
                        .pattern("TGT")
                        .pattern("AGA")
                        .define('T', ModItems.TWINE.get())
                        .define('G', Items.GLASS_PANE)
                        .define('A', ModItems.ANDESITE_GRIT.get())
                        .unlockedBy("has_andesite_grit", prov.has(ModItems.ANDESITE_GRIT.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GATHERERS_SATCHEL.get())
                        .pattern("T T")
                        .pattern("LGL")
                        .pattern("TTT")
                        .define('T', ModItems.TWINE.get())
                        .define('L', Items.LEATHER)
                        .define('G', ModItems.ANDESITE_GRIT.get())
                        .unlockedBy("has_andesite_grit", prov.has(ModItems.ANDESITE_GRIT.get()))
                        .save(prov));
    }

    // --- Create gates --------------------------------------------------------

    /**
     * The four gates do not overwrite Create's files - shipping a file at the same path
     * only wins if our pack happens to sort on top. Create's recipes are dropped by id in
     * {@link ModRecipeRemovals} and the replacements below are ordinary recipes of ours
     * that happen to produce Create's items.
     */
    private static void registerGates() {
        // Gate 1: andesite alloy. The 2x2 pattern is full, so there is no room to add an
        // ingredient - the andesite in it becomes grit instead. Same shape, same count,
        // same look in JEI; the cost is the hammering that now sits in front of it.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "hammering/andesite_grit"),
                new HammeringRecipe(Ingredient.of(Blocks.ANDESITE),
                        new ItemStack(ModItems.ANDESITE_GRIT.get())),
                null));

        // Only the zinc variant comes back. Iron nuggets are not a stone age material,
        // and leaving that route open would let the alloy skip zinc entirely.
        andesiteAlloy("andesite_alloy_from_zinc", AllTags.commonItemTag("nuggets/zinc"));
        // The mixer route has to move with it, or a mixer would put the gate back to
        // plain andesite. Same two ingredients as the bench recipe.
        andesiteAlloyMixing("mixing/andesite_alloy_from_zinc", AllTags.commonItemTag("nuggets/zinc"));

        // Gate 2: the shaft. Create stacks two alloy in a column; the lower one becomes a
        // stave, so the count and the shape hold and only the lower half changes hands.
        //
        // Cut from planks rather than from stripped logs as first sketched: the per-wood
        // log tags already include stripped logs, so a stave recipe on those would race
        // the planks recipe for the same input and one of the two would win at random.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "sawing/wooden_stave"),
                new SawingRecipe(Ingredient.of(ItemTags.PLANKS),
                        new ItemStack(ModItems.WOODEN_STAVE.get(), STAVES_PER_PLANK)),
                null));

        // create:shaft gets no recipe, here or anywhere. ModKineticLimits is keyed by
        // block id, so Create's shaft is UNLIMITED - and every age has a ceiling, not
        // just this one, so there is no later point at which handing it over is safe.
        // Each age drives on its own shaft; Create's cogwheels follow it out, since
        // their recipes only ask for a shaft.

        // Gate 3: the casing. Create hands one over the moment alloy touches a stripped
        // log; both of those recipes are gone. What stands in their place is the same
        // gesture stretched out - cement in the brush, held against the log until it
        // takes. The gate asks for a minute of standing there, not for another item.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANDESITE_CEMENT.get())
                        .requires(AllItems.ANDESITE_ALLOY.get())
                        .requires(ModItems.RESIN.get())
                        .unlockedBy("has_resin", prov.has(ModItems.RESIN.get()))
                        .save(prov));

        andesiteCasing("from_log", AllTags.commonItemTag("stripped_logs"));
        andesiteCasing("from_wood", AllTags.commonItemTag("stripped_woods"));

        // Gate 4: the press, and with it the end of the age. Create stacks shaft, casing
        // and an iron block in a column; the pattern widens to five so a die sits on
        // either side of the casing. Casings alone no longer reach it.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.MECHANICAL_PRESS.get())
                        .pattern(" S ")
                        .pattern("DCD")
                        .pattern(" I ")
                        .define('S', ModBlocks.WOODEN_SHAFT.get())
                        .define('C', AllBlocks.ANDESITE_CASING.get())
                        .define('D', ModItems.STONE_DIE.get())
                        .define('I', AllTags.commonItemTag("storage_blocks/iron"))
                        .unlockedBy("has_stone_die", prov.has(ModItems.STONE_DIE.get()))
                        .save(prov, ResourceLocation.fromNamespaceAndPath(
                                CreateMoonScentyPresents.MODID, "mechanical_press")));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "shaping/stone_die"),
                new ShapingRecipe(Ingredient.of(Blocks.STONE), new ItemStack(ModItems.STONE_DIE.get())),
                null));

        // What the saw opens instead: our own shaft, which the limit does cover.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.WOODEN_SHAFT.get())
                        .requires(ModItems.WOODEN_STAVE.get())
                        .requires(ModItems.TWINE.get())
                        .unlockedBy("has_wooden_stave", prov.has(ModItems.WOODEN_STAVE.get()))
                        .save(prov));
    }

    private static void andesiteCasing(String name, TagKey<Item> stripped) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> prov.accept(
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID,
                        "applying/andesite_casing_" + name),
                new ApplyingRecipe(Ingredient.of(ModItems.ANDESITE_CEMENT.get()),
                        Ingredient.of(stripped), AllBlocks.ANDESITE_CASING.get(), CASING_TIME),
                null));
    }

    /** One fibre off whatever the source is; no tool, no station. */
    private static void fiber(String source, Ingredient from) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PLANT_FIBER.get())
                        .requires(from)
                        .unlockedBy("has_" + source, prov.has(ModItems.PLANT_FIBER.get()))
                        .save(prov, ResourceLocation.fromNamespaceAndPath(
                                CreateMoonScentyPresents.MODID, "plant_fiber_from_" + source)));
    }

    private static void andesiteAlloyMixing(String name, TagKey<Item> nugget) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            ResourceLocation id =
                    ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, name);
            prov.accept(id, new StandardProcessingRecipe.Builder<>(MixingRecipe::new, id)
                    .require(ModItems.ANDESITE_GRIT.get())
                    .require(nugget)
                    .output(AllItems.ANDESITE_ALLOY.get())
                    .build(), null);
        });
    }

    private static void andesiteAlloy(String name, TagKey<Item> nugget) {
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.ANDESITE_ALLOY.get())
                        .pattern("BA")
                        .pattern("AB")
                        .define('A', ModItems.ANDESITE_GRIT.get())
                        .define('B', nugget)
                        .unlockedBy("has_andesite_grit", prov.has(ModItems.ANDESITE_GRIT.get()))
                        .save(prov, ResourceLocation.fromNamespaceAndPath(
                                CreateMoonScentyPresents.MODID, name)));
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
