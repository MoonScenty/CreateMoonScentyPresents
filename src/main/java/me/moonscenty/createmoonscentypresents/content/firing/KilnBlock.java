package me.moonscenty.createmoonscentypresents.content.firing;

import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The hand work shared by the stations that only wait: pack it, and reach in.
 *
 * <p>An interface rather than a base class, because these blocks have nothing else in
 * common - one is a plain cube and one has a front to face - and Java only lets a block
 * inherit one shape.
 *
 * @param <T> the block entity this block carries
 */
public interface KilnBlock<T extends KilnBlockEntity<?>> extends IBE<T> {

    /** Loading it is done by hand, whether the fire under it is going or not. */
    default ItemInteractionResult load(ItemStack stack, Level level, BlockPos pos, Player player) {
        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        T station = getBlockEntity(level, pos);
        if (station == null)
            return ItemInteractionResult.CONSUME;
        int taken = station.insert(stack);
        if (taken == 0)
            return ItemInteractionResult.CONSUME;
        if (!player.isCreative())
            stack.shrink(taken);
        level.playSound(null, pos, SoundEvents.SAND_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
        return ItemInteractionResult.CONSUME;
    }

    /** An empty hand takes what is finished, and only then what is still waiting. */
    default InteractionResult unload(Level level, BlockPos pos, Player player) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        T station = getBlockEntity(level, pos);
        if (station == null)
            return InteractionResult.CONSUME;

        ItemStack removed = station.removeFired();
        if (removed.isEmpty())
            return InteractionResult.CONSUME;
        player.getInventory().placeItemBackInInventory(removed);
        level.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
        return InteractionResult.CONSUME;
    }
}
