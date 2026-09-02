package me.moonscenty.createmoonscentypresents.content.foundry;

import java.util.Optional;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Stirs one molten metal into another in the basin two blocks below it.
 *
 * <p>Where Create's mixer looks straight down, this looks past a shut foundry lid - the
 * lid is what keeps the heat in, so a mixer that displaced it would be no use.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public class FoundryMixerBlockEntity extends MechanicalMixerBlockEntity {

    private static final Object ALLOYING_RECIPES_KEY = new Object();

    public FoundryMixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == ModRecipeTypes.ALLOYING.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && !running && !level.isClientSide)
            basinChecker.scheduleUpdate();
    }

    @Override
    protected <I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe) {
        if (recipe == null)
            return false;
        return getBasin().filter(basin -> BasinRecipe.match(basin, recipe)).isPresent();
    }

    @Override
    protected void applyBasinRecipe() {
        if (currentRecipe == null)
            return;

        Optional<BasinBlockEntity> optional = getBasin();
        if (optional.isEmpty() || !(optional.get() instanceof FoundryBasinBlockEntity basin))
            return;

        boolean wasEmpty = basin.canContinueProcessing();
        if (!FoundryRecipe.apply(basin, currentRecipe))
            return;
        getProcessedRecipeTrigger().ifPresent(this::award);
        basin.inputTank.sendDataImmediately();

        if (wasEmpty && matchBasinRecipe(currentRecipe)) {
            continueWithPreviousRecipe();
            sendData();
        }

        basin.notifyChangeOfContents();
    }

    /** Splashes out of the basin two blocks down rather than the one right below. */
    @Override
    protected void spillParticle(ParticleOptions data) {
        float angle = level.getRandom().nextFloat() * 360;
        Vec3 offset = VecHelper.rotate(new Vec3(0, 0, 0.25f), angle, Direction.Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y).add(0, .25f, 0);
        Vec3 centre = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.getRandom(), 1 / 128f);
        level.addParticle(data, centre.x, centre.y - 1.65f, centre.z, target.x, target.y, target.z);
    }

    /** A basin two down, with a shut foundry lid between. */
    @Override
    protected Optional<BasinBlockEntity> getBasin() {
        if (level == null)
            return Optional.empty();
        BlockEntity below = level.getBlockEntity(worldPosition.below(2));
        BlockState lid = level.getBlockState(worldPosition.below());
        Block lidBlock = lid.getBlock();
        if (!(below instanceof FoundryBasinBlockEntity basin) || !(lidBlock instanceof FoundryLidBlock))
            return Optional.empty();
        if (lid.getValue(FoundryLidBlock.OPEN))
            return Optional.empty();
        return Optional.of(basin);
    }

    @Override
    protected Object getRecipeCacheKey() {
        return ALLOYING_RECIPES_KEY;
    }
}
