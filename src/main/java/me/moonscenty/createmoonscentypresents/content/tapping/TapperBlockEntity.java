package me.moonscenty.createmoonscentypresents.content.tapping;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Collects sap from the log the tapper is driven into, and lets it set.
 *
 * <p>Two jobs, never both at once, and the order between them is fixed. A full bucket
 * coagulates; anything less keeps drawing. Whatever has already set sits in the bucket
 * and stops both until somebody takes it out - so a tapper left alone fills once and
 * then waits, which is what makes emptying it worth the walk.
 */
public class TapperBlockEntity extends SmartBlockEntity {

    /**
     * The bucket holds one batch. A coagulating recipe asking for more than this can
     * never run, which is a datapack mistake rather than a state the block handles.
     */
    public static final int TANK_CAPACITY = 1000;

    private FluidStack tank = FluidStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;

    private int timer;
    /** Which of the two jobs the timer is counting; switching between them restarts it. */
    private boolean coagulating;

    public TapperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // None; a tapper is one tank, one slot and a timer.
    }

    public FluidStack getTank() {
        return tank;
    }

    public ItemStack getOutput() {
        return output;
    }

    /** How full the bucket is, 0 to 1, for the renderer. */
    public float getFillLevel() {
        return tank.isEmpty() ? 0 : Math.min(1f, (float) tank.getAmount() / TANK_CAPACITY);
    }

    public ItemStack takeOutput() {
        ItemStack taken = output;
        output = ItemStack.EMPTY;
        timer = 0;
        notifyUpdate();
        return taken;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide)
            return;

        // Anything already set in the bucket stops the whole thing. This is the only
        // reason a tapper ever needs visiting, so it is deliberately absolute.
        if (!output.isEmpty())
            return;

        if (coagulate())
            return;
        draw();
    }

    /** @return true if a full bucket is setting, whether or not it finished this tick. */
    private boolean coagulate() {
        RecipeHolder<CoagulatingRecipe> recipe = CoagulatingRecipe.find(level, tank).orElse(null);
        if (recipe == null)
            return false;

        if (!advance(recipe.value().processingTime(), true))
            return true;

        tank.shrink(recipe.value().fluid().getAmount());
        if (tank.getAmount() <= 0)
            tank = FluidStack.EMPTY;
        output = recipe.value().result().copy();
        notifyUpdate();
        return true;
    }

    private void draw() {
        BlockState log = level.getBlockState(worldPosition.relative(getBlockState().getValue(TapperBlock.FACING)));
        RecipeHolder<TappingRecipe> recipe = TappingRecipe.find(level, log).orElse(null);
        if (recipe == null) {
            timer = 0;
            return;
        }

        FluidStack drawn = recipe.value().result();
        boolean mismatched = !tank.isEmpty() && !FluidStack.isSameFluidSameComponents(tank, drawn);
        if (mismatched || tank.getAmount() + drawn.getAmount() > TANK_CAPACITY) {
            // Full, or holding something else entirely, and nothing will take it away.
            timer = 0;
            return;
        }

        if (!advance(recipe.value().processingTime(), false))
            return;

        if (tank.isEmpty())
            tank = drawn.copy();
        else
            tank.grow(drawn.getAmount());
        notifyUpdate();
    }

    /** @return true when the timer has reached the given time, which also resets it. */
    private boolean advance(int processingTime, boolean forCoagulation) {
        if (coagulating != forCoagulation) {
            coagulating = forCoagulation;
            timer = 0;
        }
        if (++timer < processingTime)
            return false;
        timer = 0;
        return true;
    }

    // The client is told the tank and the bucket - both are drawn - but not the timer,
    // which nothing on that side reads.
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!tank.isEmpty())
            tag.put("Tank", tank.save(registries));
        if (!output.isEmpty())
            tag.put("Output", output.save(registries));
        if (!clientPacket && timer > 0) {
            tag.putInt("Timer", timer);
            tag.putBoolean("Coagulating", coagulating);
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        FluidStack previousTank = tank;
        ItemStack previousOutput = output;

        tank = tag.contains("Tank")
                ? FluidStack.parse(registries, tag.getCompound("Tank")).orElse(FluidStack.EMPTY)
                : FluidStack.EMPTY;
        output = tag.contains("Output")
                ? ItemStack.parse(registries, tag.getCompound("Output")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;

        if (!clientPacket) {
            timer = tag.getInt("Timer");
            coagulating = tag.getBoolean("Coagulating");
            return;
        }
        if (level != null && (!ItemStack.matches(previousOutput, output)
                || !FluidStack.matches(previousTank, tank)))
            redraw();
    }

    /**
     * Rebuilds the chunk section this tapper sits in.
     *
     * <p>The list of block entities a section draws is collected while that section is
     * being compiled, and nothing recompiles it afterwards unless a block changes.
     * Sap arriving changes no block, so a tapper that was not in the list when its
     * section was last compiled would stay unrendered until something else nearby
     * forced a rebuild. Asking for one here is the same thing placing a neighbouring
     * block does.
     */
    private void redraw() {
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }
}
