package me.moonscenty.createmoonscentypresents.content.foundry;

import java.util.List;

import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;

import me.moonscenty.createmoonscentypresents.registry.ModTags;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * The working half of a faucet: drains the tank it is stuck to and pours the result
 * into whatever is beneath it.
 *
 * <p>The stream falls as far as the first block below and no further, and anything
 * standing in it while molten metal is running gets burned. It shuts itself when there
 * is nowhere for the fluid to go, unless redstone is holding it open.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed. Trimmed: no ladle and
 * no belt filling - neither exists this early - and the burn is vanilla fire rather
 * than a damage type of its own.
 */
public class FaucetBlockEntity extends SmartBlockEntity {

    /** Per tick. Slow enough that a pour is something you watch happen. */
    private static final int FLOW_RATE = 5;
    /** How far the stream is allowed to look for something to land on. */
    private static final int MAX_FALL = 8;
    private static final int BURNING_TICKS = 100;
    private static final float BURN_DAMAGE = 4.0F;

    public TankManipulationBehaviour attachedTank;

    private int fallingDistance;
    /** Client side only: what the stream is drawn as. */
    private FluidStack renderedFluid = FluidStack.EMPTY;

    public FaucetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(attachedTank = new TankManipulationBehaviour(this,
                CapManipulationBehaviourBase.InterfaceProvider.oppositeOfBlockFacing()));
    }

    @Override
    public void tick() {
        super.tick();
        if (!getBlockState().getValue(FaucetBlock.OPEN))
            return;

        if (level.isClientSide()) {
            if (!renderedFluid.isEmpty())
                createFlowParticles(renderedFluid);
            return;
        }

        IFluidHandler tank = attachedTank.getInventory();
        if (tank == null)
            return;

        // Simulated first: if nothing would land anywhere, the tap shuts rather than
        // quietly emptying the basin onto the floor.
        for (boolean simulate : Iterate.trueAndFalse) {
            FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
            FluidStack drained = tank.drain(FLOW_RATE, action);

            if (!simulate)
                updateRenderedFluid(drained);
            spillOnEntities(drained, action);

            int filled = drained.getAmount() > 0 ? tryFill(drained, action) : 0;
            if (filled <= 0 && simulate) {
                BlockState state = getBlockState();
                if (!state.getValue(FaucetBlock.POWERED)) {
                    FaucetBlock.toggle(state, level, worldPosition);
                    break;
                }
            }
        }
    }

    /** Whether opening it now would pour into anything. */
    public boolean canOpenFaucet() {
        IFluidHandler tank = attachedTank.getInventory();
        if (tank == null)
            return false;
        return tryFill(tank.drain(FLOW_RATE, FluidAction.SIMULATE), FluidAction.SIMULATE) > 0;
    }

    /** The first tank below, however far down the stream reaches. */
    public IFluidHandler getTargetTank() {
        if (level == null)
            return null;

        int fallDistance = 0;
        BlockPos target = worldPosition;
        for (int i = 0; i < MAX_FALL; i++) {
            target = target.below();
            if (!level.getBlockState(target).isAir()) {
                fallDistance = i + 1;
                break;
            }
        }
        updateFallDistance(fallDistance);

        IFluidHandler tank = level.getCapability(Capabilities.FluidHandler.BLOCK, target, Direction.UP);
        if (tank == null)
            return null;
        notifyUpdate();
        return tank;
    }

    private int tryFill(FluidStack drained, FluidAction action) {
        IFluidHandler target = getTargetTank();
        if (drained.isEmpty() || target == null)
            return 0;
        return target instanceof SmartFluidTankBehaviour.InternalFluidHandler internal
                ? internal.forceFill(drained.copy(), action)
                : target.fill(drained.copy(), action);
    }

    /** Standing under molten metal is a mistake worth feeling. */
    private void spillOnEntities(FluidStack drained, FluidAction action) {
        if (level == null || drained.isEmpty() || action.simulate())
            return;
        if (!drained.getFluid().defaultFluidState().is(ModTags.MOLTEN))
            return;

        List<Entity> entities = level.getEntities(null, getFluidArea());
        for (Entity entity : entities) {
            if (entity.fireImmune())
                continue;
            entity.setRemainingFireTicks(BURNING_TICKS);
            if (entity.hurt(level.damageSources().inFire(), BURN_DAMAGE))
                entity.playSound(SoundEvents.GENERIC_BURN, .4F, 3F);
        }
    }

    // --- presentation ----------------------------------------------------------

    private void createFlowParticles(FluidStack fluid) {
        if (!(getBlockState().getBlock() instanceof FaucetBlock))
            return;
        Direction direction = getBlockState().getValue(FaucetBlock.FACING);
        Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());
        Vec3 out = VecHelper.getCenterOf(worldPosition)
                .add(directionVec.scale(.65).subtract(directionVec.normalize().scale(10 / 16f)));
        Vec3 motion = directionVec.scale(1 / 96f).add(0, -1 / 16f, 0);

        for (int i = 0; i < 2; i++) {
            ParticleOptions particle = FluidFX.getFluidParticle(fluid);
            Vec3 m = VecHelper.offsetRandomly(motion, RandomSource.create(), 1 / 32f);
            level.addAlwaysVisibleParticle(particle, out.x, out.y, out.z, m.x, m.y, m.z);
        }
    }

    private void updateRenderedFluid(FluidStack drained) {
        if (FluidStack.isSameFluidSameComponents(renderedFluid, drained))
            return;
        renderedFluid = drained;
        notifyUpdate();
    }

    private void updateFallDistance(int distance) {
        if (distance == fallingDistance)
            return;
        fallingDistance = distance;
        invalidateRenderBoundingBox();
        notifyUpdate();
    }

    public FluidStack getRenderedFluid() {
        return renderedFluid;
    }

    public int getFallingDistance() {
        return fallingDistance;
    }

    private AABB getFluidArea() {
        return new AABB(worldPosition).expandTowards(0, -getFallingDistance(), 0);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return getFluidArea();
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        int previous = fallingDistance;
        fallingDistance = compound.getInt("FallingDistance");
        if (previous != fallingDistance)
            invalidateRenderBoundingBox();
        renderedFluid = FluidStack.parseOptional(registries, compound.getCompound("RenderedFluid"));
        super.read(compound, registries, clientPacket);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("FallingDistance", fallingDistance);
        compound.put("RenderedFluid", renderedFluid.saveOptional(registries));
        super.write(compound, registries, clientPacket);
    }
}
