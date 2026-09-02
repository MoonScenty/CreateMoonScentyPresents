package me.moonscenty.createmoonscentypresents.content.hammering;


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
 * One item worked into another by hand, the way Create's sandpaper polishes: the hammer
 * in one hand, the material in the other.
 */
public record HammeringRecipe(Ingredient input, ItemStack result) implements Recipe<SingleRecipeInput> {

    public static boolean canHammer(Level level, ItemStack stack) {
        return find(level, stack).isPresent();
    }

    /** The result for the given stack, or the stack itself if nothing matches. */
    public static ItemStack hammer(Level level, ItemStack stack) {
        return find(level, stack).map(holder -> holder.value()
                .assemble(new SingleRecipeInput(stack), level.registryAccess())
                .copy())
                .orElse(stack);
    }

    private static java.util.Optional<RecipeHolder<HammeringRecipe>> find(Level level, ItemStack stack) {
        if (stack.isEmpty())
            return java.util.Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.HAMMERING.get(), new SingleRecipeInput(stack), level);
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
        return ModRecipeTypes.HAMMERING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.HAMMERING.get();
    }

    public static class Serializer implements RecipeSerializer<HammeringRecipe> {
        private static final MapCodec<HammeringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(HammeringRecipe::input),
                        ItemStack.CODEC.fieldOf("result").forGetter(HammeringRecipe::result))
                .apply(instance, HammeringRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, HammeringRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, HammeringRecipe::input,
                ItemStack.STREAM_CODEC, HammeringRecipe::result,
                HammeringRecipe::new);

        @Override
        public MapCodec<HammeringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HammeringRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
