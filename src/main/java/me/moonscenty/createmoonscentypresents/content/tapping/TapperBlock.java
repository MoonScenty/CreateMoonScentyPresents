package me.moonscenty.createmoonscentypresents.content.tapping;

import java.util.EnumMap;
import java.util.Map;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;
import me.moonscenty.createmoonscentypresents.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A bucket with a spout, driven into a log that has already been bored.
 *
 * <p>FACING points at the log, not away from it: the spout is the part that matters and
 * it has to go into the wood. The tapper therefore stands in the cell beside the log
 * rather than on it, and comes off the moment that log does.
 */
public class TapperBlock extends HorizontalDirectionalBlock implements IBE<TapperBlockEntity> {

    public static final MapCodec<TapperBlock> CODEC = simpleCodec(TapperBlock::new);

    /** The model as drawn: the bucket, and the spout jutting out of its south face. */
    private static final double[] BUCKET = { 2, 0, 2, 14, 12, 14 };
    private static final double[] SPOUT = { 5.5, 12, 12, 10.5, 16, 16 };

    private static final Map<Direction, VoxelShape> SHAPES = shapes();

    public TapperBlock(Properties properties) {
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

    /**
     * Placed against the face of a log, so the tapper ends up in the cell the player was
     * pointing into and turns back to face the wood. Returning null when that is not a
     * bored log is what stops it being put anywhere else.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction towardsLog = context.getClickedFace().getOpposite();
        if (towardsLog.getAxis().isVertical())
            return null;
        BlockState placed = defaultBlockState().setValue(FACING, towardsLog);
        return canSurvive(placed, context.getLevel(), context.getClickedPos()) ? placed : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING))).is(ModTags.HOLED_LOG_BLOCKS);
    }

    /** Take the log away and the tapper falls off with whatever was in it. */
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbour,
            LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        if (direction == state.getValue(FACING) && !canSurvive(state, level, pos))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    /** Bare handed: lift out whatever has set in the bucket. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        TapperBlockEntity tapper = getBlockEntity(level, pos);
        if (tapper == null || tapper.getOutput().isEmpty())
            return InteractionResult.PASS;

        if (!level.isClientSide) {
            ItemStack collected = tapper.takeOutput();
            if (!player.addItem(collected))
                player.drop(collected, false);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.7f, 1.2f);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Broken or replaced: what had set inside drops rather than going with the block. */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()))
            withBlockEntityDo(level, pos, tapper -> {
                if (!tapper.getOutput().isEmpty())
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            tapper.getOutput());
            });
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public Class<TapperBlockEntity> getBlockEntityClass() {
        return TapperBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TapperBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.TAPPER.get();
    }

    // --- shape ---------------------------------------------------------------

    /**
     * The blockstate turns the model clockwise by the facing yaw, and south is where a
     * yaw of zero points - so the shape drawn above is the one for FACING=SOUTH and the
     * other three are quarter turns of it.
     */
    private static Map<Direction, VoxelShape> shapes() {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int turns = (int) (facing.toYRot() / 90);
            map.put(facing, Shapes.or(turn(BUCKET, turns), turn(SPOUT, turns)));
        }
        return map;
    }

    /** One turn takes east to south, which is what a positive y rotation does. */
    private static VoxelShape turn(double[] box, int turns) {
        double x0 = box[0];
        double z0 = box[2];
        double x1 = box[3];
        double z1 = box[5];
        for (int i = 0; i < turns; i++) {
            double nx0 = 16 - z1;
            double nx1 = 16 - z0;
            z0 = x0;
            z1 = x1;
            x0 = nx0;
            x1 = nx1;
        }
        return Block.box(x0, box[1], z0, x1, box[4], z1);
    }
}
