package me.moonscenty.createmoonscentypresents.content.milling;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

/**
 * One thing ground down by a turning stone, and what is left of it.
 *
 * <p>Deliberately not Create's milling recipe. The primitive millstone is slower and
 * takes a different list, and a recipe of Create's would have to be kept apart from
 * that list by hand. Built on Create's processing recipe - the shared shape of "one
 * input, worked for a while, several outputs with weights" - but on this mod's own
 * type info.
 */
public class MillingRecipe extends StandardProcessingRecipe<RecipeInput> {

    public MillingRecipe(ProcessingRecipeParams params) {
        super(ModRecipeTypes.MILLING_INFO, params);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.isEmpty())
            return false;
        return ingredients.get(0).test(input.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }
}
