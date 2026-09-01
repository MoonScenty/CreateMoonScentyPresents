package me.moonscenty.createmoonscentypresents.content.processing;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Holds the single item hung on the rack and dries it.
 *
 * <p>One item, not one stack: the rack is a display as much as a machine, and the
 * renderer draws exactly what is in here. Anything handed to it is split down to a
 * count of one and the remainder stays with the player.
 *
 * <p>Drying is a plain tick count against the recipe's time. It only runs on the
 * server, and the client is told when the item changes rather than as it progresses -
 * the item turning into its dried form is the visible result, so there is nothing in
 * between for the client to draw.
 */
public class DryingRackBlockEntity extends SmartBlockEntity {

    private ItemStack heldItem = ItemStack.EMPTY;
    private int dryingTicks;

    // Server side only. Resolving a recipe walks every drying recipe there is, which
    // is not worth doing for every rack on every tick.
    private RecipeHolder<DryingRecipe> cachedRecipe;
    private boolean recipeResolved;

    public DryingRackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // None; the drying is simple enough to live here.
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public boolean isEmpty() {
        return heldItem.isEmpty();
    }

    /**
     * Hangs one item on the rack.
     *
     * @param from the stack being offered; left untouched, the caller decides whether
     *             to charge the player for it
     * @return false if the rack is already occupied or nothing was offered
     */
    public boolean insert(ItemStack from) {
        if (!heldItem.isEmpty() || from.isEmpty())
            return false;
        heldItem = from.copyWithCount(1);
        restartDrying();
        notifyUpdate();
        return true;
    }

    /** Takes the item back off the rack, empty if there was none. Part dried is lost. */
    public ItemStack removeHeldItem() {
        ItemStack removed = heldItem;
        heldItem = ItemStack.EMPTY;
        restartDrying();
        notifyUpdate();
        return removed;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || heldItem.isEmpty())
            return;

        RecipeHolder<DryingRecipe> recipe = recipe();
        if (recipe == null)
            return;

        if (++dryingTicks < recipe.value().processingTime())
            return;

        // Straight back into drying afterwards: if the dried item is itself the input
        // to another recipe, the rack carries on with it.
        heldItem = recipe.value().assemble(new SingleRecipeInput(heldItem), level.registryAccess());
        restartDrying();
        notifyUpdate();
    }

    /** Picks up recipe changes from a datapack reload without re-resolving every tick. */
    @Override
    public void lazyTick() {
        super.lazyTick();
        recipeResolved = false;
    }

    private RecipeHolder<DryingRecipe> recipe() {
        if (!recipeResolved) {
            cachedRecipe = DryingRecipe.find(level, heldItem).orElse(null);
            recipeResolved = true;
        }
        return cachedRecipe;
    }

    private void restartDrying() {
        dryingTicks = 0;
        cachedRecipe = null;
        recipeResolved = false;
    }

    // The client needs the item - it is what the renderer draws - but not the progress,
    // which nothing on that side reads.
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!heldItem.isEmpty())
            tag.put("HeldItem", heldItem.save(registries));
        if (!clientPacket && dryingTicks > 0)
            tag.putInt("DryingTicks", dryingTicks);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        ItemStack previous = heldItem;
        heldItem = tag.contains("HeldItem")
                ? ItemStack.parse(registries, tag.getCompound("HeldItem")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;

        if (!clientPacket) {
            dryingTicks = tag.getInt("DryingTicks");
            cachedRecipe = null;
            recipeResolved = false;
            return;
        }
        if (level != null && !ItemStack.matches(previous, heldItem))
            redraw();
    }

    /**
     * Rebuilds the chunk section this rack sits in.
     *
     * <p>The list of block entities a section draws is collected while that section is
     * being compiled, and nothing recompiles it afterwards unless a block changes.
     * Hanging an item changes no block, so a rack that was not in the list when its
     * section was last compiled stays unrendered - the item only appears once
     * something else nearby forces a rebuild. Asking for that rebuild here is the same
     * thing placing a neighbouring block does.
     */
    private void redraw() {
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }
}
