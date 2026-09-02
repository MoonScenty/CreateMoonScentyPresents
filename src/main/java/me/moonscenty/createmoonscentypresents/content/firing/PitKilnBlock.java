package me.moonscenty.createmoonscentypresents.content.firing;

import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A pit of clay that is packed, set over a fire and left.
 *
 * <p>The heat comes from underneath: a lit campfire below is what fires it. Nothing is
 * struck and nothing is fed in - the fire is a block you build, and keeping it going is
 * the whole of the work. There is no hopper face and no fuel slot, because the stone
 * age has no way to automate either.
 */
public class PitKilnBlock extends Block implements IBE<PitKilnBlockEntity> {

    public PitKilnBlock(Properties properties) {
        super(properties);
    }

    /** Whether a kiln here is being fired: a lit campfire directly below. */
    public static boolean isHeated(BlockGetter level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(BlockTags.CAMPFIRES) && below.hasProperty(BlockStateProperties.LIT)
                && below.getValue(BlockStateProperties.LIT);
    }

    /** Loading it is done by hand, and only while the fire under it is out. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        PitKilnBlockEntity kiln = getBlockEntity(level, pos);
        if (kiln == null)
            return ItemInteractionResult.CONSUME;
        int taken = kiln.insert(stack);
        if (taken == 0)
            return ItemInteractionResult.CONSUME;
        if (!player.isCreative())
            stack.shrink(taken);
        level.playSound(null, pos, SoundEvents.SAND_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
        return ItemInteractionResult.CONSUME;
    }

    /** An empty hand takes what is finished, and only then what is still waiting. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        PitKilnBlockEntity kiln = getBlockEntity(level, pos);
        if (kiln == null)
            return InteractionResult.CONSUME;

        ItemStack removed = kiln.removeFired();
        if (removed.isEmpty())
            return InteractionResult.CONSUME;
        player.getInventory().placeItemBackInInventory(removed);
        level.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()))
            IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public Class<PitKilnBlockEntity> getBlockEntityClass() {
        return PitKilnBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PitKilnBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.PIT_KILN.get();
    }
}
