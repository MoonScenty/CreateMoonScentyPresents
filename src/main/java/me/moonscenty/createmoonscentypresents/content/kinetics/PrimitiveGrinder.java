package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillingRecipe;

import net.minecraft.world.item.crafting.RecipeType;

/**
 * A block entity built on Create's millstone that grinds its own recipe list.
 * <p>
 * Create names {@code AllRecipeTypes.MILLING} directly, so the lookup is redirected by
 * {@code MillstoneBlockEntityMixin}; this is how it asks which list to use. Adding
 * another grinder means implementing this, not touching the mixin.
 */
public interface PrimitiveGrinder {
    RecipeType<? extends MillingRecipe> primitiveRecipeType();
}
