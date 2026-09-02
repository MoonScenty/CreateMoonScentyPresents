package me.moonscenty.createmoonscentypresents.content.processing;

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
 * One item left on a drying rack for a while, becoming one other item.
 *
 * <p>Both sides are a single item and neither carries a count. An {@link Ingredient}
 * has no count of its own, and the result is read with
 * {@link ItemStack#STRICT_SINGLE_ITEM_CODEC}, which has no count field to write - so a
 * recipe cannot ask for two of anything, rather than being allowed to ask and then
 * quietly ignored. The rack holds one item, so that is all either side can ever be.
 *
 * @param processingTime how long the item hangs there, in ticks
 */
public record DryingRecipe(Ingredient input, ItemStack result, int processingTime)
        implements Recipe<SingleRecipeInput>, TimedItemRecipe {

    /** The recipe for what is hanging on the rack, if anything dries it. */
    public static Optional<RecipeHolder<DryingRecipe>> find(Level level, ItemStack stack) {
        if (stack.isEmpty())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.DRYING.get(), new SingleRecipeInput(stack), level);
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
        return ModRecipeTypes.DRYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.DRYING.get();
    }

    public static class Serializer implements RecipeSerializer<DryingRecipe> {
        private static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(DryingRecipe::input),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(DryingRecipe::result),
                        ExtraCodecs.POSITIVE_INT.fieldOf("processing_time")
                                .forGetter(DryingRecipe::processingTime))
                .apply(instance, DryingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, DryingRecipe::input,
                ItemStack.STREAM_CODEC, DryingRecipe::result,
                ByteBufCodecs.VAR_INT, DryingRecipe::processingTime,
                DryingRecipe::new);

        @Override
        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
