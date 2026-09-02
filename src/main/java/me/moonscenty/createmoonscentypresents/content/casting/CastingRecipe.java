package me.moonscenty.createmoonscentypresents.content.casting;

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
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Molten metal poured into a mould and left to set.
 *
 * <p>Three things decide it: the fluid and how much of it, the mould sitting on the
 * table, and how long it takes to go hard. The mould is the shape - the same metal over
 * a different mould is a different thing - which is why a casting table is worth having
 * rather than the metal simply setting into ingots wherever it lands.
 *
 * @param moldConsumed whether the mould is broken getting the casting out of it, which
 *                     is what separates a clay one-shot from a stone one you keep
 */
public record CastingRecipe(SizedFluidIngredient fluid, Ingredient mold, ItemStack result,
        int processingTime, boolean moldConsumed) implements Recipe<RecipeInput> {

    /** Whether this table, as it stands, could run this recipe. */
    public boolean matches(FluidStack held, ItemStack moldOnTable, boolean ignoreAmount) {
        boolean fluidMatches = ignoreAmount ? fluid.ingredient().test(held) : fluid.test(held);
        return fluidMatches && mold.test(moldOnTable);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        // Matched through the table, which knows both halves; this is never the way in.
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
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
        return NonNullList.of(Ingredient.EMPTY, mold);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CASTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CASTING.get();
    }

    public static class Serializer implements RecipeSerializer<CastingRecipe> {

        private static final MapCodec<CastingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(SizedFluidIngredient.NESTED_CODEC.fieldOf("fluid").forGetter(CastingRecipe::fluid),
                        Ingredient.CODEC_NONEMPTY.fieldOf("mold").forGetter(CastingRecipe::mold),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(CastingRecipe::result),
                        ExtraCodecs.POSITIVE_INT.fieldOf("processing_time")
                                .forGetter(CastingRecipe::processingTime),
                        com.mojang.serialization.Codec.BOOL.optionalFieldOf("mold_consumed", false)
                                .forGetter(CastingRecipe::moldConsumed))
                .apply(instance, CastingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        SizedFluidIngredient.STREAM_CODEC, CastingRecipe::fluid,
                        Ingredient.CONTENTS_STREAM_CODEC, CastingRecipe::mold,
                        ItemStack.STREAM_CODEC, CastingRecipe::result,
                        ByteBufCodecs.VAR_INT, CastingRecipe::processingTime,
                        ByteBufCodecs.BOOL, CastingRecipe::moldConsumed,
                        CastingRecipe::new);

        @Override
        public MapCodec<CastingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
