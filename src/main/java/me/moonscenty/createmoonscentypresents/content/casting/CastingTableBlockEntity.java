package me.moonscenty.createmoonscentypresents.content.casting;

import java.util.List;
import java.util.Optional;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * A table with a mould on it that molten metal is poured into.
 *
 * <p>It takes fluid only when it could do something with it: the tank refuses anything
 * no recipe matches against the mould that is there. That is what stops a faucet from
 * quietly filling a table that will never set, and it is why the mould has to go on
 * first.
 *
 * <p>Reworked from Create: Metallurgy by Lucreeper74, MIT licensed. Kept the shape of
 * the thing - mould slot, result slot, a tank that asks the recipe how much it wants -
 * and left out the lock modes, the fan cooling and the slag.
 */
public class CastingTableBlockEntity extends SmartBlockEntity implements Clearable {

    /** Enough for the largest pour a single mould could ask for. */
    private static final int TANK_CAPACITY = 1000;

    public final SmartInventory moldInv = new SmartInventory(1, this, 1, true);
    public final SmartInventory resultInv = new SmartInventory(1, this, 64, false);

    private final FluidTank inputTank = new FluidTank(TANK_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return findRecipe(stack, true).isPresent();
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            sendData();
        }
    };

    private CastingRecipe currentRecipe;
    private int processingTicks = -1;

    public CastingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        resultInv.forbidInsertion();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // None; the pouring is simple enough to live here.
    }

    public FluidTank getFluidTank() {
        return inputTank;
    }

    public ItemStack getMold() {
        return moldInv.getStackInSlot(0);
    }

    public ItemStack getResult() {
        return resultInv.getStackInSlot(0);
    }

    public boolean isRunning() {
        return processingTicks >= 0;
    }

    /** The recipe this table could run with the mould on it and this fluid in it. */
    private Optional<CastingRecipe> findRecipe(FluidStack fluid, boolean ignoreAmount) {
        if (level == null || fluid.isEmpty() || !getResult().isEmpty())
            return Optional.empty();
        ItemStack mold = getMold();
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.CASTING.get())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.matches(fluid, mold, ignoreAmount))
                .findFirst();
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null)
            return;

        if (level.isClientSide) {
            if (isRunning())
                spawnParticles();
            return;
        }

        if (!isRunning()) {
            // Nothing is setting yet: start as soon as there is enough in the tank.
            Optional<CastingRecipe> recipe = findRecipe(inputTank.getFluid(), false);
            if (recipe.isEmpty())
                return;
            currentRecipe = recipe.get();
            processingTicks = currentRecipe.processingTime();
            moldInv.forbidExtraction();
            sendData();
            return;
        }

        if (processingTicks > 0) {
            processingTicks--;
            return;
        }

        apply();
    }

    private void apply() {
        // The mould could have been swapped while it set; check again before paying out.
        if (currentRecipe == null || !currentRecipe.matches(inputTank.getFluid(), getMold(), false)) {
            reset();
            return;
        }

        inputTank.drain(currentRecipe.fluid().amount(), FluidTank.FluidAction.EXECUTE);
        resultInv.allowInsertion();
        resultInv.setStackInSlot(0, currentRecipe.result().copy());
        resultInv.forbidInsertion();
        if (currentRecipe.moldConsumed())
            moldInv.setStackInSlot(0, ItemStack.EMPTY);

        reset();
        setChanged();
    }

    /** Wipes what is in progress; the mould and the casting stay where they are. */
    public void reset() {
        processingTicks = -1;
        currentRecipe = null;
        moldInv.allowExtraction();
        sendData();
    }

    /** Empties the table onto the floor, mould and all. */
    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, moldInv);
        ItemHelper.dropContents(level, worldPosition, resultInv);
    }

    @Override
    public void clearContent() {
        moldInv.clearContent();
        resultInv.clearContent();
    }

    private void spawnParticles() {
        RandomSource random = level.getRandom();
        Vec3 centre = VecHelper.getCenterOf(worldPosition);
        Vec3 at = centre.add(VecHelper.offsetRandomly(Vec3.ZERO, random, .25f).multiply(1, 0, 1));
        if (random.nextInt(8) == 0)
            level.addParticle(ParticleTypes.SMOKE, at.x, at.y + .45, at.z, 0, 0, 0);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("MoldInv", moldInv.serializeNBT(registries));
        compound.put("ResultInv", resultInv.serializeNBT(registries));
        compound.put("Tank", inputTank.writeToNBT(registries, new CompoundTag()));
        compound.putInt("ProcessingTicks", processingTicks);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        moldInv.deserializeNBT(registries, compound.getCompound("MoldInv"));
        resultInv.deserializeNBT(registries, compound.getCompound("ResultInv"));
        inputTank.readFromNBT(registries, compound.getCompound("Tank"));
        processingTicks = compound.getInt("ProcessingTicks");
    }
}
