package me.moonscenty.createmoonscentypresents.content.shaping;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * One item worked into another by hand, the way Create's sandpaper polishes: the saw
 * in one hand, the material in the other.
 */
public record ShapingRecipe(Ingredient input, ItemStack result) implements Recipe<SingleRecipeInput> {

    public static boolean canShape(Level level, ItemStack stack) {
        return find(level, stack).isPresent();
    }

    /** The result for the given stack, or the stack itself if nothing matches. */
    public static ItemStack shape(Level level, ItemStack stack) {
        return find(level, stack).map(holder -> holder.value()
                .assemble(new SingleRecipeInput(stack), level.registryAccess())
                .copy())
                .orElse(stack);
    }

    private static java.util.Optional<RecipeHolder<ShapingRecipe>> find(Level level, ItemStack stack) {
        if (stack.isEmpty())
            return java.util.Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.SHAPING.get(), new SingleRecipeInput(stack), level);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SHAPING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SHAPING.get();
    }

    public static class Serializer implements RecipeSerializer<ShapingRecipe> {
        private static final MapCodec<ShapingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ShapingRecipe::input),
                        ItemStack.CODEC.fieldOf("result").forGetter(ShapingRecipe::result))
                .apply(instance, ShapingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ShapingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ShapingRecipe::input,
                ItemStack.STREAM_CODEC, ShapingRecipe::result,
                ShapingRecipe::new);

        @Override
        public MapCodec<ShapingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
