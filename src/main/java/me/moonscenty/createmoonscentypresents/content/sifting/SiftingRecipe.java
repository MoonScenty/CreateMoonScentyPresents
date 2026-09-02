package me.moonscenty.createmoonscentypresents.content.sifting;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * One thing shaken through a mesh, and what falls out.
 *
 * <p>Built on Create's processing recipe - the shared shape of "one input, worked for
 * a while, several outputs with weights" - but on this mod's own type info, so it is
 * not a milling recipe and the sifter is not a millstone.
 *
 * <p>It can also ask whether the sifter is standing in water. Washing gravel and
 * dry-shaking it are different jobs and give different things, so the same input can go
 * two ways depending on where the machine is built. The field is optional: a recipe
 * that leaves it out runs either way.
 */
public class SiftingRecipe extends StandardProcessingRecipe<RecipeInput> {

    /** Present to require that state, absent to not care. */
    private final Optional<Boolean> waterlogged;

    public SiftingRecipe(ProcessingRecipeParams params) {
        this(params, Optional.empty());
    }

    public SiftingRecipe(ProcessingRecipeParams params, Optional<Boolean> waterlogged) {
        super(ModRecipeTypes.SIFTING_INFO, params);
        this.waterlogged = waterlogged;
    }

    public Optional<Boolean> waterlogged() {
        return waterlogged;
    }

    /** Whether a sifter in this state may run this recipe. */
    public boolean matchesState(BlockState state) {
        if (waterlogged.isEmpty())
            return true;
        if (!state.hasProperty(SifterBlock.WATERLOGGED))
            return false;
        return state.getValue(SifterBlock.WATERLOGGED) == waterlogged.get();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.isEmpty())
            return false;
        return ingredients.get(0).test(input.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    /**
     * Create's own serializer takes the shared processing fields and nothing else, so
     * this wraps its codecs rather than restating them and adds the one field on top.
     */
    public static class Serializer implements RecipeSerializer<SiftingRecipe> {

        private static final MapCodec<SiftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ProcessingRecipeParams.CODEC.forGetter(SiftingRecipe::getParams),
                        Codec.BOOL.optionalFieldOf("waterlogged").forGetter(SiftingRecipe::waterlogged))
                        .apply(instance, SiftingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, SiftingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ProcessingRecipeParams.STREAM_CODEC, SiftingRecipe::getParams,
                        ByteBufCodecs.optional(ByteBufCodecs.BOOL), SiftingRecipe::waterlogged,
                        SiftingRecipe::new);

        @Override
        public MapCodec<SiftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SiftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
