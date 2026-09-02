package me.moonscenty.createmoonscentypresents.content.charring;

import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.content.firing.KilnBlock;
import me.moonscenty.createmoonscentypresents.content.processing.HorizontalCubeBlock;
import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Wood packed into a pit, buried, and left over a fire to smoulder.
 *
 * <p>Same hand work as the kiln, and heated the same way, but it will not run
 * uncovered: charcoal is wood kept from the air, so something has to be sitting on top.
 */
public class CharcoalPitBlock extends HorizontalCubeBlock implements KilnBlock<CharcoalPitBlockEntity> {

    public CharcoalPitBlock(Properties properties) {
        super(properties);
    }

    /** Buried: whatever is directly above keeps the air off. */
    public static boolean isCovered(BlockGetter level, BlockPos pos) {
        if (level == null)
            return false;
        BlockPos above = pos.above();
        return level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        return load(stack, level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        return unload(level, pos, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()))
            IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public Class<CharcoalPitBlockEntity> getBlockEntityClass() {
        return CharcoalPitBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CharcoalPitBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.CHARCOAL_PIT.get();
    }
}
