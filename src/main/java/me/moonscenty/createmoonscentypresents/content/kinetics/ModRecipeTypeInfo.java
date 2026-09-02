package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.function.Supplier;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * What Create's processing recipe base asks for: a type, a serializer and an id, in one
 * handle. Create supplies its own through {@code AllRecipeTypes}; this is the same
 * thing over registry entries of ours, so a processing recipe can be built on Create's
 * machinery without being one of Create's recipes.
 */
public record ModRecipeTypeInfo(ResourceLocation id, Supplier<? extends RecipeType<?>> type,
        Supplier<? extends RecipeSerializer<?>> serializer) implements IRecipeTypeInfo {

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    }
}
