package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.Map;

import com.simibubi.create.content.kinetics.base.IRotate;

import me.moonscenty.createmoonscentypresents.registry.ModBlocks;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * A second item for the gearbox that stands it on end.
 *
 * <p>There is no vertical gearbox block. The gearbox splits rotation across the four
 * sides that are not on its axis, so an axis of Y spreads power around the horizontal
 * plane and an axis of X or Z spreads it up and down instead. This item is the same
 * block, placed with one of the latter.
 *
 * <p>Reimplemented rather than extended: Create's own version names its block in the
 * constructor, which is the one thing that has to change.
 */
public class ModVerticalGearboxItem extends BlockItem {

    public ModVerticalGearboxItem(Item.Properties properties) {
        super(ModBlocks.PRIMITIVE_GEARBOX.get(), properties);
    }

    /** Without this a block item is named after its block, which is the other gearbox. */
    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    /** Leaves the block pointing at the plain gearbox item, so wrenches and picks give that. */
    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack,
            BlockState state) {
        // Turn to face a neighbouring shaft if exactly one axis of them is present;
        // two disagreeing neighbours are no help, so fall back to the player's facing.
        Direction.Axis shafts = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockPos neighbourPos = pos.relative(side);
            BlockState neighbour = level.getBlockState(neighbourPos);
            if (!(neighbour.getBlock() instanceof IRotate rotate)
                    || !rotate.hasShaftTowards(level, neighbourPos, neighbour, side.getOpposite()))
                continue;
            if (shafts != null && shafts != side.getAxis()) {
                shafts = null;
                break;
            }
            shafts = side.getAxis();
        }

        // The gearbox drives the sides that are *not* on its axis, so it has to lie
        // across the shafts it is meant to feed, not along them.
        Direction.Axis axis = shafts == null ? player.getDirection().getClockWise().getAxis()
                : shafts == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AXIS, axis));
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }
}
