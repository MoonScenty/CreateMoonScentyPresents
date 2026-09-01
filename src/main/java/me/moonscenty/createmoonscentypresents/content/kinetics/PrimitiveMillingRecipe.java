package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * What the primitive millstone grinds.
 * <p>
 * It has to be a {@link MillingRecipe} because Create's millstone stores what it found
 * in a field of that type, but it answers with this mod's recipe type - and the recipe
 * manager indexes by what {@link #getType()} returns. So Create's millstone never sees
 * these, and the primitive one never sees Create's.
 */
public class PrimitiveMillingRecipe extends MillingRecipe {

    public PrimitiveMillingRecipe(ProcessingRecipeParams params) {
        super(params);
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PRIMITIVE_MILLING.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.PRIMITIVE_MILLING_SERIALIZER.get();
    }
}
