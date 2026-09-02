package me.moonscenty.createmoonscentypresents.content.foundry;

import java.util.List;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

/**
 * The vessel metal is melted in: a basin that holds a fluid worth keeping rather than
 * one passing through.
 *
 * <p>Built on Create's basin, so it is filled and emptied by every means a basin
 * already is, and heated the way a basin already is - from the block underneath. Create
 * reads a lit campfire as a smouldering blaze burner, which is what lets the same block
 * serve a stone age fire now and a blaze burner later; see the melting recipes for
 * which of those a given metal needs.
 *
 * <p>The basin holds nothing back on its own. Something has to sit on top of it and
 * drive a recipe - a lid to melt, a mixer to alloy.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed. Trimmed to what this
 * pack uses: no output animation, no gauge, no ladle.
 */
public class FoundryBasinBlockEntity extends BasinBlockEntity {

    /** Three things can go in at once - enough for an alloy, not enough for a factory. */
    private static final int INPUT_SLOTS = 3;
    private static final int OUTPUT_SLOTS = 4;
    private static final int TANKS = 4;
    private static final int TANK_CAPACITY = 1000;
    private static final int MAX_STACK = 9;

    public FoundryBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInventory = (BasinInventory) new BasinInventory(INPUT_SLOTS, this).withMaxStackSize(MAX_STACK);
        outputInventory = new BasinInventory(OUTPUT_SLOTS, this).forbidInsertion().withMaxStackSize(MAX_STACK);
        itemCapability = new CombinedInvWrapper(inputInventory, outputInventory);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, TANKS, TANK_CAPACITY, true);
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, TANKS, TANK_CAPACITY, true)
                .forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTank);

        fluidCapability = new CombinedTankWrapper(outputTank.getCapability(), inputTank.getCapability());
    }

    @Override
    public void lazyTick() {
        if (level.isClientSide || isEmpty())
            return;
        notifyChangeOfContents();
    }

    @Override
    public void tick() {
        // Pours into whatever the spout is aimed at before anything else runs, so a
        // finished melt leaves room for the next one.
        if (!level.isClientSide && !outputTank.isEmpty()
                && getBlockState().getValue(FoundryBasinBlock.FACING) != Direction.DOWN)
            tryEmptyingWithSpoutput();
        super.tick();
    }

    /** A wrench turns the spout from side to side, or points it back down to shut it. */
    @Override
    public void onWrenched(Direction clickedFace) {
        if (clickedFace.getAxis().isVertical())
            return;
        BlockState blockState = getBlockState();
        Direction facing = blockState.getValue(FoundryBasinBlock.FACING);
        if (facing == clickedFace) {
            level.setBlockAndUpdate(worldPosition, blockState.setValue(FoundryBasinBlock.FACING, Direction.DOWN));
            level.playSound(null, worldPosition, SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS,
                    .5f, .5f + level.getRandom().nextFloat());
            return;
        }
        level.setBlockAndUpdate(worldPosition, blockState.setValue(FoundryBasinBlock.FACING, clickedFace));
        level.playSound(null, worldPosition, SoundEvents.NETHERITE_BLOCK_STEP, SoundSource.BLOCKS,
                .5f, .5f + level.getRandom().nextFloat());
    }

    /** Empties the output tank into whatever sits under the spout. */
    public void tryEmptyingWithSpoutput() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof FoundryBasinBlock))
            return;
        Direction direction = blockState.getValue(FoundryBasinBlock.FACING);
        BlockPos output = worldPosition.below().relative(direction);
        BlockEntity be = level.getBlockEntity(output);

        DirectBeltInputBehaviour belt = BlockEntityBehaviour.get(level, output, DirectBeltInputBehaviour.TYPE);
        if (belt == null || !belt.canInsertFromSide(direction))
            return;

        IFluidHandler targetTank = be == null ? null
                : level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), direction.getOpposite());
        IFluidHandler basinTank = getOutputTank().getCapability();
        if (targetTank == null || basinTank == null)
            return;

        FluidStack drained = basinTank.drain(basinTank.getTankCapacity(0), IFluidHandler.FluidAction.SIMULATE);
        if (drained.isEmpty())
            return;

        int filled = forceFill(targetTank, drained, IFluidHandler.FluidAction.SIMULATE);
        if (filled <= 0)
            return;

        drained = basinTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
        forceFill(targetTank, drained, IFluidHandler.FluidAction.EXECUTE);
        notifyChangeOfContents();
        sendData();
    }

    /** Create's own tanks refuse ordinary fills while an output is locked. */
    private static int forceFill(IFluidHandler tank, FluidStack stack, IFluidHandler.FluidAction action) {
        return tank instanceof SmartFluidTankBehaviour.InternalFluidHandler internal
                ? internal.forceFill(stack, action)
                : tank.fill(stack, action);
    }

    // --- results ---------------------------------------------------------------

    public boolean acceptOutputs(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        outputInventory.allowInsertion();
        outputTank.allowInsertion();
        boolean accepted = acceptOutputsInner(outputItems, outputFluids, simulate);
        outputInventory.forbidInsertion();
        outputTank.forbidInsertion();
        return accepted;
    }

    private boolean acceptOutputsInner(List<ItemStack> outputItems, List<FluidStack> outputFluids,
            boolean simulate) {
        if (!(getBlockState().getBlock() instanceof BasinBlock))
            return false;

        IItemHandler targetInv = outputInventory;
        if (targetInv == null && !outputItems.isEmpty())
            return false;
        for (ItemStack outputStack : outputItems)
            if (!ItemHandlerHelper.insertItemStacked(targetInv, outputStack.copy(), simulate).isEmpty())
                return false;

        if (outputFluids.isEmpty())
            return true;

        IFluidHandler targetTank = outputTank.getCapability();
        IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE
                : IFluidHandler.FluidAction.EXECUTE;
        for (FluidStack fluidResult : outputFluids)
            if (forceFill(targetTank, fluidResult.copy(), action) != fluidResult.getAmount())
                return false;
        return true;
    }

    /**
     * The fire under it. Create keeps its own accessor package private, so this reads
     * the block below through the static it exposes instead.
     */
    public BlazeBurnerBlock.HeatLevel heatLevel() {
        if (level == null)
            return BlazeBurnerBlock.HeatLevel.NONE;
        return getHeatLevelOf(level.getBlockState(worldPosition.below()));
    }

    /** Create keeps both of these protected; a capability registration needs them. */
    public IItemHandler getItemCapability() {
        return itemCapability;
    }

    public IFluidHandler getFluidCapability() {
        return fluidCapability;
    }

    public SmartFluidTankBehaviour getOutputTank() {
        return outputTank;
    }

    // --- goggles ---------------------------------------------------------------

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("gui.goggles.basin_contents").forGoggles(tooltip);
        boolean isEmpty = true;

        for (int slot = 0; slot < itemCapability.getSlots(); slot++) {
            ItemStack stack = itemCapability.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;
            CreateLang.text("")
                    .add(Component.translatable(stack.getDescriptionId()).withStyle(ChatFormatting.GRAY))
                    .add(CreateLang.text(" x" + stack.getCount()).style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        for (int tank = 0; tank < fluidCapability.getTanks(); tank++) {
            FluidStack fluid = fluidCapability.getFluidInTank(tank);
            if (fluid.isEmpty())
                continue;
            CreateLang.text("")
                    .add(CreateLang.fluidName(fluid)
                            .add(CreateLang.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(CreateLang.number(fluid.getAmount()).add(mb).style(ChatFormatting.BLUE)))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        if (isEmpty)
            tooltip.remove(0);
        return true;
    }
}
