package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * The block half of {@link GrinderBlockEntity}: something standing on the floor, driven
 * from below, that items are dropped into and taken back out of by hand.
 *
 * <p>Follows Create's millstone in how it is handled, since that is how a machine of
 * this shape works, but is not built on it - see the block entity for why.
 *
 * @param <T> the block entity this block carries
 */
public abstract class GrinderBlock<T extends GrinderBlockEntity<?>> extends KineticBlock
        implements IBE<T>, ICogWheel {

    protected GrinderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.MILLSTONE;
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    /** An empty hand takes what is finished, and then what is waiting to be worked. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        withBlockEntityDo(level, pos, machine -> {
            boolean emptyOutput = true;
            IItemHandlerModifiable inv = machine.outputInv;
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack held = inv.getStackInSlot(slot);
                if (!held.isEmpty())
                    emptyOutput = false;
                player.getInventory().placeItemBackInInventory(held);
                inv.setStackInSlot(slot, ItemStack.EMPTY);
            }

            if (emptyOutput) {
                inv = machine.inputInv;
                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    player.getInventory().placeItemBackInInventory(inv.getStackInSlot(slot));
                    inv.setStackInSlot(slot, ItemStack.EMPTY);
                }
            }

            machine.setChanged();
            machine.sendData();
        });

        return ItemInteractionResult.SUCCESS;
    }

    /** Items dropped onto it fall in. */
    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        super.updateEntityAfterFallOn(level, entity);

        if (entity.level().isClientSide || !entity.isAlive())
            return;
        if (!(entity instanceof ItemEntity itemEntity))
            return;

        T machine = null;
        for (BlockPos pos : Iterate.hereAndBelow(entity.blockPosition()))
            if (machine == null)
                machine = getBlockEntity(level, pos);
        if (machine == null)
            return;

        IItemHandler handler = machine.getLevel()
                .getCapability(Capabilities.ItemHandler.BLOCK, machine.getBlockPos(), null);
        if (handler == null)
            return;

        ItemStack remainder = handler.insertItem(0, itemEntity.getItem(), false);
        if (remainder.isEmpty())
            itemEntity.discard();
        if (remainder.getCount() < itemEntity.getItem().getCount())
            itemEntity.setItem(remainder);
    }
}
