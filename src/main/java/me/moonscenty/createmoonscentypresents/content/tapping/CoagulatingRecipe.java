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
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * A pool of fluid left alone until it sets into something you can pick up.
 *
 * <p>This is the second half of a tapper: sap collects, and once there is enough of it
 * the tapper stops drawing and lets it harden. Nothing is stirred and nothing is heated
 * - the only ingredient is time, which is why a tapper is worth leaving behind.
 */
public record CoagulatingRecipe(FluidStack fluid, ItemStack result, int processingTime)
        implements Recipe<SingleRecipeInput> {

    /** The recipe a tank of this fluid could finish, if it holds enough of it. */
    public static Optional<RecipeHolder<CoagulatingRecipe>> find(Level level, FluidStack held) {
        if (held.isEmpty())
            return Optional.empty();
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.COAGULATING.get())
                .stream()
                .filter(holder -> holder.value().accepts(held))
                .findFirst();
    }

    public boolean accepts(FluidStack held) {
        return FluidStack.isSameFluidSameComponents(held, fluid) && held.getAmount() >= fluid.getAmount();
    }

    /**
     * Recipes are found by {@link #find}, which asks about a tank rather than a slot.
     * The recipe book has no way to express a fluid, so this never matches.
     */
    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
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
        return NonNullList.create();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.COAGULATING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.COAGULATING.get();
    }

    public static class Serializer implements RecipeSerializer<CoagulatingRecipe> {
        private static final MapCodec<CoagulatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(FluidStack.CODEC.fieldOf("fluid").forGetter(CoagulatingRecipe::fluid),
                        ItemStack.CODEC.fieldOf("result").forGetter(CoagulatingRecipe::result),
                        ExtraCodecs.POSITIVE_INT.fieldOf("processing_time")
                                .forGetter(CoagulatingRecipe::processingTime))
                .apply(instance, CoagulatingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, CoagulatingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        FluidStack.STREAM_CODEC, CoagulatingRecipe::fluid,
                        ItemStack.STREAM_CODEC, CoagulatingRecipe::result,
                        ByteBufCodecs.VAR_INT, CoagulatingRecipe::processingTime,
                        CoagulatingRecipe::new);

        @Override
        public MapCodec<CoagulatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CoagulatingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
