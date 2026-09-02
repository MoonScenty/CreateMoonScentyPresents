package me.moonscenty.createmoonscentypresents.content.applying;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * A substance worked into a block that is already standing in the world: the brush in
 * hand, the block under the cursor, held down until the coat has taken.
 *
 * <p>The other three hand methods turn an item into another item. This one does not -
 * a coating is something you put <em>on</em> a thing, and the thing is a block. That is
 * also what makes it the natural way to reach a casing, which Create likewise builds by
 * treating a placed log rather than by crafting one.
 *
 * <p>Unlike sawing, hammering and shaping - where the recipe type is tied to one tool -
 * this one names the substance in the recipe, because the brush is only the applicator.
 * Resin is the only substance in the stone age, but tar, oil and wax are the same
 * gesture with a different load and they reuse this type rather than each bringing
 * another. {@link #find} therefore matches on the block and then filters by substance,
 * so two substances can turn the same block into different things.
 *
 * <p>The target block is matched through its item form, the way Create's item
 * application recipes do, which is what lets a recipe name a tag such as
 * {@code #minecraft:stripped_logs} instead of one block at a time.
 */
public record ApplyingRecipe(Ingredient substance, Ingredient block, Block result, int processingTime)
        implements Recipe<SingleRecipeInput> {

    /** The recipe for this substance and this block, if there is one. */
    public static Optional<RecipeHolder<ApplyingRecipe>> find(Level level, ItemStack substance, BlockState state) {
        if (substance.isEmpty())
            return Optional.empty();
        ItemStack asItem = new ItemStack(state.getBlock());
        if (asItem.isEmpty())
            return Optional.empty();
        return level.getRecipeManager()
                .getRecipesFor(ModRecipeTypes.APPLYING.get(), new SingleRecipeInput(asItem), level)
                .stream()
                .filter(holder -> holder.value().substance.test(substance))
                .findFirst();
    }

    /**
     * Whether any recipe would accept this as a load. The brush refuses everything else,
     * so it cannot be filled with something that will never do anything.
     */
    public static boolean isSubstance(Level level, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.APPLYING.get())
                .stream()
                .anyMatch(holder -> holder.value().substance.test(stack));
    }

    /**
     * The state the treated block becomes. Properties the two blocks share are carried
     * over, so a log lying east-west becomes a casing lying east-west rather than
     * snapping upright.
     */
    public BlockState resultFor(BlockState state) {
        BlockState applied = result.defaultBlockState();
        for (Property<?> property : state.getProperties())
            applied = carry(state, applied, property);
        return applied;
    }

    private static <T extends Comparable<T>> BlockState carry(BlockState from, BlockState to, Property<T> property) {
        return to.hasProperty(property) ? to.setValue(property, from.getValue(property)) : to;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return block.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return new ItemStack(result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(result);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, substance, block);
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
                        Ingredient.CODEC_NONEMPTY.fieldOf("block").forGetter(ApplyingRecipe::block),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("result").forGetter(ApplyingRecipe::result),
                        ExtraCodecs.POSITIVE_INT.fieldOf("processing_time")
                                .forGetter(ApplyingRecipe::processingTime))
                .apply(instance, ApplyingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ApplyingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ApplyingRecipe::substance,
                        Ingredient.CONTENTS_STREAM_CODEC, ApplyingRecipe::block,
                        ByteBufCodecs.registry(Registries.BLOCK), ApplyingRecipe::result,
                        ByteBufCodecs.VAR_INT, ApplyingRecipe::processingTime,
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
