package me.moonscenty.createmoonscentypresents.content.firing;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Something packed into a pit kiln and burnt hard, becoming one other thing.
 *
 * <p>The furnace is not this. A furnace is a box that anyone can light and walk away
 * from; a pit kiln is filled, sealed, lit once and finished when it burns out - it
 * takes a whole load at a time and gives nothing back until it is done. That is why
 * this is its own type rather than a smelting recipe.
 *
 * <p>Both sides are a single item and neither carries a count; the kiln fires whatever
 * stack is in it, one item's worth per item.
 *
 * @param processingTime how long the load burns, in ticks
 */
public record FiringRecipe(Ingredient input, ItemStack result, int processingTime)
        implements Recipe<SingleRecipeInput> {

    /** The recipe for what is in the kiln, if the fire changes it into anything. */
    public static Optional<RecipeHolder<FiringRecipe>> find(Level level, ItemStack stack) {
        if (stack.isEmpty())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.FIRING.get(), new SingleRecipeInput(stack), level);
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
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.FIRING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.FIRING.get();
    }

    public static class Serializer implements RecipeSerializer<FiringRecipe> {
        private static final MapCodec<FiringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FiringRecipe::input),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(FiringRecipe::result),
                        ExtraCodecs.POSITIVE_INT.fieldOf("processing_time")
                                .forGetter(FiringRecipe::processingTime))
                .apply(instance, FiringRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FiringRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, FiringRecipe::input,
                        ItemStack.STREAM_CODEC, FiringRecipe::result,
                        ByteBufCodecs.VAR_INT, FiringRecipe::processingTime,
                        FiringRecipe::new);

        @Override
        public MapCodec<FiringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FiringRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
