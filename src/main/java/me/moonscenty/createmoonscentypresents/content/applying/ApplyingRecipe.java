package me.moonscenty.createmoonscentypresents.content.applying;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
 * A substance rubbed into a material by hand: the substance in one hand, the material
 * in the other, the way Create's sandpaper polishes.
 *
 * <p>Unlike sawing, hammering and shaping - where the recipe type is tied to one tool -
 * this one names the substance in the recipe. Resin is the only one in the stone age,
 * but tar, oil and wax are the same gesture with a different jar, and they reuse this
 * type rather than each bringing another.
 *
 * <p>{@link #find} therefore matches on the material and then filters by substance, so
 * two substances can turn the same input into different things.
 */
public record ApplyingRecipe(Ingredient substance, Ingredient input, ItemStack result)
        implements Recipe<SingleRecipeInput> {

    public static boolean canApply(Level level, ItemStack substance, ItemStack stack) {
        return find(level, substance, stack).isPresent();
    }

    /** The result for the given pair, or the material itself if nothing matches. */
    public static ItemStack apply(Level level, ItemStack substance, ItemStack stack) {
        return find(level, substance, stack).map(holder -> holder.value()
                .assemble(new SingleRecipeInput(stack), level.registryAccess())
                .copy())
                .orElse(stack);
    }

    private static Optional<RecipeHolder<ApplyingRecipe>> find(Level level, ItemStack substance, ItemStack stack) {
        if (stack.isEmpty() || substance.isEmpty())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipesFor(ModRecipeTypes.APPLYING.get(), new SingleRecipeInput(stack), level)
                .stream()
                .filter(holder -> holder.value().substance().test(substance))
                .findFirst();
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
        return NonNullList.of(Ingredient.EMPTY, substance, input);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.APPLYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.APPLYING.get();
    }

    public static class Serializer implements RecipeSerializer<ApplyingRecipe> {
        private static final MapCodec<ApplyingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC_NONEMPTY.fieldOf("substance").forGetter(ApplyingRecipe::substance),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ApplyingRecipe::input),
                        ItemStack.CODEC.fieldOf("result").forGetter(ApplyingRecipe::result))
                .apply(instance, ApplyingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ApplyingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ApplyingRecipe::substance,
                        Ingredient.CONTENTS_STREAM_CODEC, ApplyingRecipe::input,
                        ItemStack.STREAM_CODEC, ApplyingRecipe::result,
                        ApplyingRecipe::new);

        @Override
        public MapCodec<ApplyingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplyingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
