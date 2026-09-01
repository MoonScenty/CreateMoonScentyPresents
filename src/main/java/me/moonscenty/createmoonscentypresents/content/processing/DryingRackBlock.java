package me.moonscenty.createmoonscentypresents.content.processing;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A pair of A-frames with a pole slung between them, for drying leather and plants.
 *
 * <p>Nothing dries yet - this is the block and its shape only. The frames stand at
 * two opposite edges of the cell and the pole runs between them, so the block has to
 * be orientable.
 *
 * <p>FACING is the side the rack is meant to be looked at from - the broad side, with
 * the pole running left to right across it. The model is drawn with that side lying
 * along east-west, so the blockstate carries a quarter turn to line the two up; see
 * the angleOffset where the block is registered.
 */
public class DryingRackBlock extends HorizontalDirectionalBlock implements IBE<DryingRackBlockEntity> {

    public static final MapCodec<DryingRackBlock> CODEC = simpleCodec(DryingRackBlock::new);

    // The model's own geometry, coarsened to three boxes: a frame at either end and
    // the pole across the top. Left as a full cube you could not walk into the rack
    // and the open sides would still stop arrows.
    //
    // The frames are the width of the cobble feet, 2 to 14. The legs lean 22.5 degrees
    // out of 15 tall, which puts them inside that at 3.1 to 12.9, and tops them out at
    // 14.3 - hence the 14.5 rather than the full 16. A box per frame rather than one
    // per leg: the legs cross, so following them would take a stack of boxes to say
    // little more than "the ends are solid".
    /** Looked at from the north or south: the frames stand at the east and west edges. */
    private static final VoxelShape FACING_NORTH_SOUTH = Shapes.or(
            Block.box(0, 0, 2, 3, 14.5, 14),
            Block.box(13, 0, 2, 16, 14.5, 14),
            Block.box(0, 13, 7, 16, 14, 9));

    /** Looked at from the east or west: the frames stand at the north and south edges. */
    private static final VoxelShape FACING_EAST_WEST = Shapes.or(
            Block.box(2, 0, 0, 14, 14.5, 3),
            Block.box(2, 0, 13, 14, 14.5, 16),
            Block.box(7, 13, 0, 9, 14, 16));

    public DryingRackBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /** The rack is symmetric front to back, so only the axis it faces along matters. */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? FACING_NORTH_SOUTH : FACING_EAST_WEST;
    }

    /**
     * Hangs one item on an empty rack.
     *
     * <p>An occupied rack consumes the click rather than passing it on. Passing it on
     * would reach {@link #useWithoutItem} and quietly take the item back down, which
     * is meant to be the bare-handed gesture; and letting it through entirely would
     * place whatever block is being held into the rack's own space.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        DryingRackBlockEntity rack = getBlockEntity(level, pos);
        if (rack == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!rack.isEmpty())
            return ItemInteractionResult.CONSUME;

        if (!level.isClientSide) {
            rack.insert(stack);
            if (!player.isCreative())
                stack.shrink(1);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.7f, 1.2f);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Bare handed: takes the item back down. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        DryingRackBlockEntity rack = getBlockEntity(level, pos);
        if (rack == null || rack.isEmpty())
            return InteractionResult.PASS;

        if (!level.isClientSide) {
            ItemStack removed = rack.removeHeldItem();
            if (!player.addItem(removed))
                player.drop(removed, false);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.7f, 1.2f);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Broken or replaced: the hung item drops instead of vanishing with the block. */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()))
            withBlockEntityDo(level, pos, rack -> {
                if (!rack.isEmpty())
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            rack.getHeldItem());
            });
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public Class<DryingRackBlockEntity> getBlockEntityClass() {
        return DryingRackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DryingRackBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.DRYING_RACK.get();
    }
}
