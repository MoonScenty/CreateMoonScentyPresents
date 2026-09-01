package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.List;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;
import me.moonscenty.createmoonscentypresents.registry.ModItems;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;

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
        registration.addRecipeCategories(new SawingCategory(guiHelper), new DryingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level == null)
            return;
        RecipeManager recipes = level.getRecipeManager();
        List<RecipeHolder<SawingRecipe>> sawing = recipes.getAllRecipesFor(ModRecipeTypes.SAWING.get());
        registration.addRecipes(SawingCategory.TYPE, sawing);
        List<RecipeHolder<DryingRecipe>> drying = recipes.getAllRecipesFor(ModRecipeTypes.DRYING.get());
        registration.addRecipes(DryingCategory.TYPE, drying);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Lets players click the saw and land on these recipes.
        registration.addRecipeCatalyst(ModItems.WOODEN_SAW.get(), SawingCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.DRYING_RACK.get(), DryingCategory.TYPE);
    }
}
