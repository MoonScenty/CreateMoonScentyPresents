package me.moonscenty.createmoonscentypresents.content.foundry;

import java.util.List;
import java.util.Optional;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * The lid that turns a basin into a furnace: closed over one, it melts what is in it.
 *
 * <p>It is the melting itself, not a container - there is nothing in here but the
 * timer. Opening the lid stops the melt where it stands, which is also the only way to
 * reach into the basin through the top.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public class FoundryLidBlockEntity extends FoundryBasinOperatingBlockEntity {

    /** Falls back to a second if a recipe somehow carries no duration of its own. */
    private static final int DEFAULT_DURATION = 20;

    private static final Object MELTING_RECIPES_KEY = new Object();

    public int processingTime;
    public boolean running;

    public FoundryLidBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("MeltingTime", processingTime);
        compound.putBoolean("Running", running);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        processingTime = compound.getInt("MeltingTime");
        running = compound.getBoolean("Running");
    }

    @Override
    protected void onBasinRemoved() {
        if (!running)
            return;
        processingTime = 0;
        currentRecipe = null;
        running = false;
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == ModRecipeTypes.MELTING.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide && (currentRecipe == null || processingTime == -1)) {
            running = false;
            processingTime = -1;
            basinChecker.scheduleUpdate();
        }

        if (!running || level == null)
            return;

        if (!level.isClientSide && processingTime <= 0) {
            processingTime = -1;
            applyBasinRecipe();
            sendData();
        }

        RandomSource random = level.getRandom();
        if (!level.isClientSide && random.nextInt(40) == 0)
            level.playSound(null, getBlockPos(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS,
                    .25f, .65f + random.nextFloat() * .1f);

        if (level.isClientSide && processingTime % 2 == 0)
            spawnParticles();

        if (processingTime > 0)
            processingTime--;
    }

    private void spawnParticles() {
        RandomSource random = level.getRandom();
        Vec3 centre = VecHelper.getCenterOf(worldPosition);
        Vec3 at = centre.add(VecHelper.offsetRandomly(Vec3.ZERO, random, .125f).multiply(1, 0, 1));
        if (random.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, at.x, at.y + .45, at.z, 0, 0, 0);
    }

    /** Only the basin directly below, and only while the lid is shut over it. */
    @Override
    protected Optional<BasinBlockEntity> getBasin() {
        if (level == null)
            return Optional.empty();
        BlockEntity below = level.getBlockEntity(worldPosition.below());
        if (!(below instanceof FoundryBasinBlockEntity basin))
            return Optional.empty();
        if (getBlockState().getValue(FoundryLidBlock.OPEN))
            return Optional.empty();
        return Optional.of(basin);
    }

    @Override
    protected boolean updateBasin() {
        if (running)
            return true;
        if (level == null || level.isClientSide)
            return true;
        if (getBasin().filter(BasinBlockEntity::canContinueProcessing).isEmpty())
            return true;

        List<Recipe<?>> recipes = getMatchingRecipes();
        if (recipes.isEmpty())
            return true;
        currentRecipe = recipes.get(0);
        startProcessingBasin();
        sendData();
        return true;
    }

    @Override
    protected boolean isRunning() {
        return running;
    }

    @Override
    public void startProcessingBasin() {
        if (running && processingTime > 0)
            return;
        super.startProcessingBasin();
        running = true;
        processingTime = currentRecipe instanceof StandardProcessingRecipe<?> recipe
                ? recipe.getProcessingDuration()
                : DEFAULT_DURATION;
    }

    @Override
    protected Object getRecipeCacheKey() {
        return MELTING_RECIPES_KEY;
    }
}
