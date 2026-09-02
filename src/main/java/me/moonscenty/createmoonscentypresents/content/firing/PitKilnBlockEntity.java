package me.moonscenty.createmoonscentypresents.content.firing;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Holds the load packed into a pit kiln and burns it.
 *
 * <p>A kiln is not a furnace. It is packed by hand, kept over a fire and emptied by
 * hand; there is no fuel slot and no hopper face, because the stone age has no way to
 * automate either.
 *
 * <p>It works through the load <em>one piece at a time</em> and sets each finished
 * piece aside, so a batch is a queue rather than a lump: eight pieces take eight times
 * as long as one, and whatever is already done can be taken out while the rest carries
 * on. A kiln that turned the whole load over at once would fire eight things for the
 * price of one.
 *
 * <p>The load can be changed at any time, burning or not. Refusing to open a hot kiln
 * sounds right but plays badly: a campfire is lit the moment it is placed, so the fire
 * under a kiln is nearly always going, and the rule would mean dousing it to load
 * anything. Adding to the load restarts only the piece in the fire.
 *
 * <p>The heat is not held here either. It is whatever is under the block, so letting
 * the fire go out only pauses the burn rather than spoiling the load.
 */
public class PitKilnBlockEntity extends SmartBlockEntity {

    /** One load. Small enough that a kiln is a commitment, big enough to be worth it. */
    public static final int CAPACITY = 8;

    private ItemStack load = ItemStack.EMPTY;
    private ItemStack fired = ItemStack.EMPTY;
    private int firingTicks;

    // Server side only. Resolving a recipe walks every firing recipe there is, which is
    // not worth doing for every kiln on every tick.
    private RecipeHolder<FiringRecipe> cachedRecipe;
    private boolean recipeResolved;

    public PitKilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // None; the firing is simple enough to live here.
    }

    /** What is still waiting to be fired. */
    public ItemStack getLoad() {
        return load;
    }

    /** What has come out of the fire and is waiting to be taken. */
    public ItemStack getFired() {
        return fired;
    }

    public boolean isEmpty() {
        return load.isEmpty() && fired.isEmpty();
    }

    /** Being fired: something is burning under it right now. */
    public boolean isHeated() {
        return level != null && PitKilnBlock.isHeated(level, worldPosition);
    }

    /**
     * Packs as much of the offered stack in as will fit, and says how many were taken.
     * A kiln holds one kind of thing at a time; the piece in the fire starts over.
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
        restartFiring();
        notifyUpdate();
        return taken;
    }

    /**
     * Reaches in and takes out what is there: whatever has been fired first, and only
     * once that is gone the raw load with it. Part of a piece in the fire is lost.
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
        restartFiring();
        notifyUpdate();
        return removed;
    }

    /** Whether setting a fire under it now would come to anything. */
    public boolean canFire() {
        return !load.isEmpty() && FiringRecipe.find(level, load).isPresent();
    }

    public int getFiringTicks() {
        return firingTicks;
    }

    /** How long the piece in the fire takes, or 0 if the fire would do nothing to it. */
    public int getFiringDuration() {
        RecipeHolder<FiringRecipe> holder = recipe();
        return holder == null ? 0 : holder.value().processingTime();
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || load.isEmpty() || !isHeated())
            return;
        if (level.isClientSide) {
            spawnSmoke();
            return;
        }

        RecipeHolder<FiringRecipe> recipe = recipe();
        if (recipe == null)
            return;

        ItemStack result = recipe.value().assemble(new SingleRecipeInput(load), level.registryAccess());
        // Nowhere to put the next piece: it has to be taken out before the kiln goes on.
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
        restartFiring();
        notifyUpdate();
    }

    /** Picks up recipe changes from a datapack reload without re-resolving every tick. */
    @Override
    public void lazyTick() {
        super.lazyTick();
        recipeResolved = false;
    }

    private RecipeHolder<FiringRecipe> recipe() {
        if (!recipeResolved) {
            cachedRecipe = FiringRecipe.find(level, load).orElse(null);
            recipeResolved = true;
        }
        return cachedRecipe;
    }

    private void restartFiring() {
        firingTicks = 0;
        cachedRecipe = null;
        recipeResolved = false;
    }

    private void spawnSmoke() {
        if (level.random.nextInt(4) != 0)
            return;
        double x = worldPosition.getX() + 0.3 + level.random.nextDouble() * 0.4;
        double z = worldPosition.getZ() + 0.3 + level.random.nextDouble() * 0.4;
        level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, worldPosition.getY() + 1.0, z,
                0, 0.02, 0);
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
