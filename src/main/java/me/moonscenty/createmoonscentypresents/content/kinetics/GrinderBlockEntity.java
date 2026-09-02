package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.List;
import java.util.Optional;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.foundation.sound.SoundScapes.AmbienceGroup;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

/**
 * A floor-standing machine that works one item at a time while something turns it.
 *
 * <p>The shape is Create's millstone - one input slot, a timer that counts down at the
 * network's speed, results rolled into an output buffer that has to be taken out by
 * hand - but it is written here rather than inherited from Create's block entity. That
 * class names {@code AllRecipeTypes.MILLING} directly and stores the match in a field
 * typed as Create's own recipe, so anything built on it has to pretend to be a milling
 * recipe and have its lookup redirected by a mixin. Owning the loop instead means each
 * machine names its own recipe type, and one of them can add a condition of its own
 * through {@link #accepts}.
 *
 * @param <R> the recipe this machine runs
 */
public abstract class GrinderBlockEntity<R extends ProcessingRecipe<RecipeInput, ?>>
        extends KineticBlockEntity implements Clearable {

    public ItemStackHandler inputInv;
    public ItemStackHandler outputInv;
    public IItemHandler capability;
    public int timer;

    private R lastRecipe;

    /** Ticks to wait before looking again when nothing matches. */
    private static final int IDLE_RETRY = 100;

    protected GrinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInv = new ItemStackHandler(1);
        outputInv = new ItemStackHandler(9);
        capability = new GrinderInventoryHandler();
    }

    /** The list this machine reads. */
    protected abstract RecipeType<R> recipeType();

    /**
     * A further condition on a recipe that already matches the input - whether this
     * machine, as it stands, may run it. Answers yes unless a machine says otherwise.
     */
    protected boolean accepts(R recipe) {
        return true;
    }

    /** The sound while it is working. */
    protected AmbienceGroup ambience() {
        return AmbienceGroup.MILLING;
    }

    // --- working --------------------------------------------------------------

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        super.addBehaviours(behaviours);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if (getSpeed() == 0 || inputInv.getStackInSlot(0).isEmpty())
            return;
        float pitch = Mth.clamp((Math.abs(getSpeed()) / 256f) + .45f, .85f, 1f);
        SoundScapes.play(ambience(), worldPosition, pitch);
    }

    @Override
    public void tick() {
        super.tick();
        ModKineticLimits.enforce(this);

        if (getSpeed() == 0)
            return;
        for (int slot = 0; slot < outputInv.getSlots(); slot++)
            if (outputInv.getStackInSlot(slot).getCount() == outputInv.getSlotLimit(slot))
                return;

        if (timer > 0) {
            timer -= getProcessingSpeed();
            if (level.isClientSide) {
                spawnParticles();
                return;
            }
            if (timer <= 0)
                process();
            return;
        }

        if (inputInv.getStackInSlot(0).isEmpty())
            return;

        RecipeWrapper contents = new RecipeWrapper(inputInv);
        if (lastRecipe == null || !matches(lastRecipe, contents)) {
            Optional<R> recipe = find(contents);
            if (recipe.isEmpty()) {
                timer = IDLE_RETRY;
            } else {
                lastRecipe = recipe.get();
                timer = lastRecipe.getProcessingDuration();
            }
            sendData();
            return;
        }

        timer = lastRecipe.getProcessingDuration();
        sendData();
    }

    private void process() {
        RecipeWrapper contents = new RecipeWrapper(inputInv);
        if (lastRecipe == null || !matches(lastRecipe, contents)) {
            Optional<R> recipe = find(contents);
            if (recipe.isEmpty())
                return;
            lastRecipe = recipe.get();
        }

        ItemStack input = inputInv.getStackInSlot(0);
        ItemStack remainder = input.getCraftingRemainingItem();
        input.shrink(1);
        inputInv.setStackInSlot(0, input);
        lastRecipe.rollResults(level.random)
                .forEach(stack -> ItemHandlerHelper.insertItemStacked(outputInv, stack, false));
        if (!remainder.isEmpty())
            ItemHandlerHelper.insertItemStacked(outputInv, remainder, false);

        sendData();
        setChanged();
    }

    private Optional<R> find(RecipeWrapper contents) {
        return level.getRecipeManager()
                .getRecipesFor(recipeType(), contents, level)
                .stream()
                .map(RecipeHolder::value)
                .filter(this::accepts)
                .findFirst();
    }

    private boolean matches(R recipe, RecipeWrapper contents) {
        return recipe.matches(contents, level) && accepts(recipe);
    }

    public int getProcessingSpeed() {
        return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
    }

    // --- contents -------------------------------------------------------------

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    @Override
    public void clearContent() {
        for (int slot = 0; slot < inputInv.getSlots(); slot++)
            inputInv.setStackInSlot(slot, ItemStack.EMPTY);
        for (int slot = 0; slot < outputInv.getSlots(); slot++)
            outputInv.setStackInSlot(slot, ItemStack.EMPTY);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputInv);
        ItemHelper.dropContents(level, worldPosition, outputInv);
    }

    private boolean canProcess(ItemStack stack) {
        ItemStackHandler tester = new ItemStackHandler(1);
        tester.setStackInSlot(0, stack);
        RecipeWrapper contents = new RecipeWrapper(tester);
        if (lastRecipe != null && matches(lastRecipe, contents))
            return true;
        return find(contents).isPresent();
    }

    // --- presentation ---------------------------------------------------------

    public void spawnParticles() {
        ItemStack input = inputInv.getStackInSlot(0);
        if (input.isEmpty())
            return;

        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, input);
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = VecHelper.rotate(new Vec3(0, 0, 0.5f), angle, Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Timer", timer);
        compound.put("InputInventory", inputInv.serializeNBT(registries));
        compound.put("OutputInventory", outputInv.serializeNBT(registries));
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        timer = compound.getInt("Timer");
        inputInv.deserializeNBT(registries, compound.getCompound("InputInventory"));
        outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
        super.read(compound, registries, clientPacket);
    }

    /** Takes only what it can work, and gives back only what it has finished. */
    private class GrinderInventoryHandler extends CombinedInvWrapper {

        public GrinderInventoryHandler() {
            super(inputInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return false;
            return canProcess(stack) && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return stack;
            if (!isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    }
}
