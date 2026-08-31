package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;
import com.simibubi.create.foundation.placement.PoleHelper;

import me.moonscenty.createmoonscentypresents.ModBlockEntityTypes;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Create's shaft, rebound to this mod's block entity type and placement helper.
 * <p>
 * Each shaft names the powered shaft it turns into beside an engine, so the engine
 * mixins never need to know which age they are dealing with.
 */
public class ModShaftBlock extends ShaftBlock {

    // One helper serves every shaft this mod adds; it matches on type, not on block.
    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    private final Supplier<? extends Block> poweredVariant;

    public ModShaftBlock(Properties properties, Supplier<? extends Block> poweredVariant) {
        super(properties);
        this.poweredVariant = poweredVariant;
    }

    /** The powered shaft of the same age. */
    public Block getPoweredVariant() {
        return poweredVariant.get();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        // Deliberately skips Create's encasing and metal girder branches: both would
        // replace this block with a Create one, skipping several ages of progression.
        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(stack))
            return helper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SIMPLE_KINETIC.get();
    }

    /** Extends a shaft along its axis, the way Create's shaft and piston poles do. */
    private static class PlacementHelper extends PoleHelper<Direction.Axis> {

        private PlacementHelper() {
            // Powered shafts are not AbstractSimpleShaftBlock, so they need naming
            // separately or a line of shafts stops counting where one sits.
            super(state -> state.getBlock() instanceof AbstractSimpleShaftBlock
                    || state.getBlock() instanceof PoweredShaftBlock,
                    state -> state.getValue(AXIS), AXIS);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.getItem() instanceof BlockItem item
                    && item.getBlock() instanceof AbstractSimpleShaftBlock;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> state.getBlock() instanceof ModShaftBlock
                    || state.getBlock() instanceof ModPoweredShaftBlock;
        }
    }
}
