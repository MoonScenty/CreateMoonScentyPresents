package me.moonscenty.createmoonscentypresents.content.firing;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import me.moonscenty.createmoonscentypresents.content.processing.TimedItemRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Something packed by hand, left over a fire and emptied by hand.
 *
 * <p>There is no fuel slot and no hopper face, because the stone age has no way to
 * automate either. The heat is not held here either - it is whatever is under the
 * block, so letting the fire go out only pauses the burn rather than spoiling the load.
 *
 * <p>It works through the load <em>one piece at a time</em> and sets each finished
 * piece aside, so a batch is a queue rather than a lump: eight pieces take eight times
 * as long as one, and whatever is already done can be taken out while the rest carries
 * on. Turning the whole load over at once would fire eight things for the price of one.
 *
 * <p>The load can be changed at any time, burning or not. Refusing to open a hot kiln
 * sounds right but plays badly: a campfire is lit the moment it is placed, so the fire
 * under one of these is nearly always going, and the rule would mean dousing it to load
 * anything. Adding to the load restarts only the piece in the fire.
 *
 * @param <R> the recipe this station reads
 */
public abstract class KilnBlockEntity<R extends Recipe<SingleRecipeInput> & TimedItemRecipe>
        extends SmartBlockEntity {

    /** One load. Small enough that a batch is a commitment, big enough to be worth it. */
    public static final int CAPACITY = 8;

    private ItemStack load = ItemStack.EMPTY;
    private ItemStack fired = ItemStack.EMPTY;
    private int firingTicks;

    // Server side only. Resolving a recipe walks every recipe of the type, which is not
    // worth doing for every station on every tick.
    private RecipeHolder<R> cachedRecipe;
    private boolean recipeResolved;

    protected KilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** The list this station reads. */
    protected abstract RecipeType<R> recipeType();

    /** Whether it is working right now: heat, and whatever else the station needs. */
    public boolean isRunning() {
        return level != null && KilnBlock.isHeated(level, worldPosition);
    }

    /** The smoke it gives off while working. */
    protected net.minecraft.core.particles.ParticleOptions smoke() {
        return ParticleTypes.CAMPFIRE_COSY_SMOKE;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // None; the waiting is simple enough to live here.
    }

    /** What is still waiting to be worked. */
    public ItemStack getLoad() {
        return load;
    }

    /** What has come out and is waiting to be taken. */
    public ItemStack getFired() {
        return fired;
    }

    public boolean isEmpty() {
        return load.isEmpty() && fired.isEmpty();
    }

    /**
     * Packs as much of the offered stack in as will fit, and says how many were taken.
     * It holds one kind of thing at a time; the piece in the fire starts over.
     */
    public int insert(ItemStack from) {
        if (from.isEmpty())
            return 0;
        if (!load.isEmpty() && !ItemStack.isSameItemSameComponents(load, from))
            return 0;

        int taken = Math.min(from.getCount(), CAPACITY - load.getCount());
        if (taken <= 0)
            return 0;

        if (load.isEmpty())
            load = from.copyWithCount(taken);
        else
            load.grow(taken);
        restart();
        notifyUpdate();
        return taken;
    }

    /**
     * Reaches in and takes out what is there: whatever is finished first, and only once
     * that is gone the raw load with it. Part of a piece in the fire is lost.
     */
    public ItemStack removeFired() {
        ItemStack removed = fired;
        fired = ItemStack.EMPTY;
        if (!removed.isEmpty()) {
            notifyUpdate();
            return removed;
        }
        removed = load;
        load = ItemStack.EMPTY;
        restart();
        notifyUpdate();
        return removed;
    }

    public int getFiringTicks() {
        return firingTicks;
    }

    /** How long the piece being worked takes, or 0 if nothing here can be worked. */
    public int getFiringDuration() {
        RecipeHolder<R> holder = recipe();
        return holder == null ? 0 : holder.value().processingTime();
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || load.isEmpty() || !isRunning())
            return;
        if (level.isClientSide) {
            spawnSmoke();
            return;
        }

        RecipeHolder<R> recipe = recipe();
        if (recipe == null)
            return;

        ItemStack result = recipe.value().result().copy();
        // Nowhere to put the next piece: it has to be taken out before this goes on.
        if (!fired.isEmpty() && (!ItemStack.isSameItemSameComponents(fired, result)
                || fired.getCount() + result.getCount() > fired.getMaxStackSize()))
            return;

        if (++firingTicks < recipe.value().processingTime())
            return;

        // One piece out of the load, one piece onto the pile.
        load.shrink(1);
        if (fired.isEmpty())
            fired = result;
        else
            fired.grow(result.getCount());
        restart();
        notifyUpdate();
    }

    /** Picks up recipe changes from a datapack reload without re-resolving every tick. */
    @Override
    public void lazyTick() {
        super.lazyTick();
        recipeResolved = false;
    }

    private RecipeHolder<R> recipe() {
        if (!recipeResolved) {
            cachedRecipe = load.isEmpty() || level == null ? null
                    : level.getRecipeManager()
                            .getRecipeFor(recipeType(), new SingleRecipeInput(load), level)
                            .orElse(null);
            recipeResolved = true;
        }
        return cachedRecipe;
    }

    private void restart() {
        firingTicks = 0;
        cachedRecipe = null;
        recipeResolved = false;
    }

    private void spawnSmoke() {
        if (level.random.nextInt(4) != 0)
            return;
        double x = worldPosition.getX() + 0.3 + level.random.nextDouble() * 0.4;
        double z = worldPosition.getZ() + 0.3 + level.random.nextDouble() * 0.4;
        level.addParticle(smoke(), x, worldPosition.getY() + 1.0, z, 0, 0.02, 0);
    }

    // Both stacks are worth syncing so a tooltip can read them; the progress is not,
    // since nothing on the client draws it.
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!load.isEmpty())
            tag.put("Load", load.save(registries));
        if (!fired.isEmpty())
            tag.put("Fired", fired.save(registries));
        if (!clientPacket && firingTicks > 0)
            tag.putInt("FiringTicks", firingTicks);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        load = tag.contains("Load")
                ? ItemStack.parse(registries, tag.getCompound("Load")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        fired = tag.contains("Fired")
                ? ItemStack.parse(registries, tag.getCompound("Fired")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        if (!clientPacket) {
            firingTicks = tag.getInt("FiringTicks");
            cachedRecipe = null;
            recipeResolved = false;
        }
    }
}
