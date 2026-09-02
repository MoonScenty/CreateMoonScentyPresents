package me.moonscenty.createmoonscentypresents.content.charring;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.moonscenty.createmoonscentypresents.content.processing.TimedItemRecipe;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;

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
 * Wood smothered under a cover and left to smoulder, becoming charcoal.
 *
 * <p>Its own type rather than a firing recipe, because a charcoal pit is not a kiln
 * with different contents: it has to be buried to work, and letting logs be charred in
 * the open kiln would make the cover pointless.
 *
 * @param processingTime how long one piece smoulders, in ticks
 */
public record CharringRecipe(Ingredient input, ItemStack result, int processingTime, HeatLevel heat)
        implements Recipe<SingleRecipeInput>, TimedItemRecipe {

    /** The recipe for what is packed in, if smouldering turns it into anything. */
    public static Optional<RecipeHolder<CharringRecipe>> find(Level level, ItemStack stack) {
        if (stack.isEmpty())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.CHARRING.get(), new SingleRecipeInput(stack), level);
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
        return ModRecipeTypes.CHARRING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CHARRING.get();
    }

    public static class Serializer implements RecipeSerializer<CharringRecipe> {
        private static final MapCodec<CharringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CharringRecipe::input),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(CharringRecipe::result),
                        ExtraCodecs.POSITIVE_INT.fieldOf("processing_time")
                                .forGetter(CharringRecipe::processingTime),
                        HeatLevel.CODEC.optionalFieldOf("heat", HeatLevel.WARM)
                                .forGetter(CharringRecipe::heat))
                .apply(instance, CharringRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, CharringRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CharringRecipe::input,
                        ItemStack.STREAM_CODEC, CharringRecipe::result,
                        ByteBufCodecs.VAR_INT, CharringRecipe::processingTime,
                        HeatLevel.STREAM_CODEC, CharringRecipe::heat,
                        CharringRecipe::new);

        @Override
        public MapCodec<CharringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CharringRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
