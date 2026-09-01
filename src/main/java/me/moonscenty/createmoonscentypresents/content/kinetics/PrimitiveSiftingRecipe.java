package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * What the primitive sifter separates. Same arrangement as
 * {@link PrimitiveMillingRecipe}: a {@link MillingRecipe} so Create's field can hold
 * it, reporting this mod's type so the recipe manager files it apart.
 */
public class PrimitiveSiftingRecipe extends MillingRecipe {

    public PrimitiveSiftingRecipe(ProcessingRecipeParams params) {
        super(params);
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PRIMITIVE_SIFTING.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.PRIMITIVE_SIFTING_SERIALIZER.get();
    }
}
