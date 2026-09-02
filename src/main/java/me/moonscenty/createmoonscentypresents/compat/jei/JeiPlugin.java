package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.List;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;
import me.moonscenty.createmoonscentypresents.registry.ModItems;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;
import me.moonscenty.createmoonscentypresents.content.applying.ApplyingRecipe;
import me.moonscenty.createmoonscentypresents.content.hammering.HammeringRecipe;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;
import me.moonscenty.createmoonscentypresents.content.shaping.ShapingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.CoagulatingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.TappingRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new SawingCategory(guiHelper), new HammeringCategory(guiHelper),
                new ShapingCategory(guiHelper), new ApplyingCategory(guiHelper),
                new TappingCategory(guiHelper), new CoagulatingCategory(guiHelper),
                new DryingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level == null)
            return;
        RecipeManager recipes = level.getRecipeManager();
        List<RecipeHolder<SawingRecipe>> sawing = recipes.getAllRecipesFor(ModRecipeTypes.SAWING.get());
        registration.addRecipes(SawingCategory.TYPE, sawing);
        List<RecipeHolder<HammeringRecipe>> hammering = recipes.getAllRecipesFor(ModRecipeTypes.HAMMERING.get());
        registration.addRecipes(HammeringCategory.TYPE, hammering);
        List<RecipeHolder<ShapingRecipe>> shaping = recipes.getAllRecipesFor(ModRecipeTypes.SHAPING.get());
        registration.addRecipes(ShapingCategory.TYPE, shaping);
        List<RecipeHolder<ApplyingRecipe>> applying = recipes.getAllRecipesFor(ModRecipeTypes.APPLYING.get());
        registration.addRecipes(ApplyingCategory.TYPE, applying);
        List<RecipeHolder<TappingRecipe>> tapping = recipes.getAllRecipesFor(ModRecipeTypes.TAPPING.get());
        registration.addRecipes(TappingCategory.TYPE, tapping);
        List<RecipeHolder<CoagulatingRecipe>> coagulating =
                recipes.getAllRecipesFor(ModRecipeTypes.COAGULATING.get());
        registration.addRecipes(CoagulatingCategory.TYPE, coagulating);
        List<RecipeHolder<DryingRecipe>> drying = recipes.getAllRecipesFor(ModRecipeTypes.DRYING.get());
        registration.addRecipes(DryingCategory.TYPE, drying);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Lets players click the saw and land on these recipes.
        registration.addRecipeCatalyst(ModItems.WOODEN_SAW.get(), SawingCategory.TYPE);
        registration.addRecipeCatalyst(ModItems.STONE_HAMMER.get(), HammeringCategory.TYPE);
        registration.addRecipeCatalyst(ModItems.STONE_CHISEL.get(), ShapingCategory.TYPE);
        registration.addRecipeCatalyst(ModItems.APPLICATOR_BRUSH.get(), ApplyingCategory.TYPE);
        // One block does both halves of the sap line, so it catalyses both pages.
        registration.addRecipeCatalyst(ModBlocks.TAPPER.get(), TappingCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.TAPPER.get(), CoagulatingCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.DRYING_RACK.get(), DryingCategory.TYPE);
    }
}
