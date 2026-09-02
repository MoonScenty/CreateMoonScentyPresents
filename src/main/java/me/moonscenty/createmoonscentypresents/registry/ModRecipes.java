package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import java.util.List;

import com.tterrag.registrate.providers.ProviderType;

import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;

public class ModRecipes {
    // Sawing by hand is deliberately no better than the vanilla yield: the stone age is
    // meant to be slow, and the gain arrives later with the Mechanical Saw. Bamboo is
    // worth half a log in vanilla, and stays worth half here.
    private static final int PLANKS_PER_LOG = 4;
    private static final int PLANKS_PER_BAMBOO_BLOCK = 2;

    /** 20 seconds. Balancing comes later, like every other number in this pack. */
    private static final int DRYING_TIME = 400;

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
        // Loading a brush with resin. The brush survives; the resin does not.
        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.RESIN_DIPPED_BRUSH.get())
                        .requires(Items.BRUSH)
                        .requires(ModItems.RESIN.get())
                        .unlockedBy("has_resin", prov.has(ModItems.RESIN.get()))
                        .save(prov));

        CreateMoonScentyPresents.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            for (Wood wood : WOODS)
                prov.accept(ResourceLocation.fromNamespaceAndPath(
                                CreateMoonScentyPresents.MODID, "sawing/" + wood.name() + "_planks"),
                        new SawingRecipe(Ingredient.of(wood.logs()), new ItemStack(wood.planks(), wood.count())),
                        null);
        });
    }
}
