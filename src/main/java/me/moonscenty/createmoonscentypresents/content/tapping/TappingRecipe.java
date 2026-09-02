package me.moonscenty.createmoonscentypresents.content.tapping;

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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * What a tapper draws out of the log it is driven into, and how long each draw takes.
 *
 * <p>The yield is small on purpose. A tapper is not a machine that turns a log into
 * resin; it is a tree slowly giving up sap, and the wait is the point - it runs while
 * the player is elsewhere, which is the only work in the stone age that does.
 *
 * <p>The log is matched through its item form, the same way {@code ApplyingRecipe}
 * matches its target, so a recipe can name a tag instead of one block at a time.
 */
public record TappingRecipe(Ingredient log, FluidStack result, int processingTime)
        implements Recipe<SingleRecipeInput> {

    /** The recipe for the block a tapper is looking at, if there is one. */
    public static Optional<RecipeHolder<TappingRecipe>> find(Level level, BlockState state) {
        ItemStack asItem = new ItemStack(state.getBlock());
        if (asItem.isEmpty())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipesFor(ModRecipeTypes.TAPPING.get(), new SingleRecipeInput(asItem), level)
                .stream()
                .findFirst();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return log.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        // The output is a fluid; nothing asks this for an item.
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, log);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.TAPPING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.TAPPING.get();
    }

    public static class Serializer implements RecipeSerializer<TappingRecipe> {
        private static final MapCodec<TappingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC_NONEMPTY.fieldOf("log").forGetter(TappingRecipe::log),
                        FluidStack.CODEC.fieldOf("result").forGetter(TappingRecipe::result),
                        ExtraCodecs.POSITIVE_INT.fieldOf("processing_time")
                                .forGetter(TappingRecipe::processingTime))
                .apply(instance, TappingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TappingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, TappingRecipe::log,
                        FluidStack.STREAM_CODEC, TappingRecipe::result,
                        ByteBufCodecs.VAR_INT, TappingRecipe::processingTime,
                        TappingRecipe::new);

        @Override
        public MapCodec<TappingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TappingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
