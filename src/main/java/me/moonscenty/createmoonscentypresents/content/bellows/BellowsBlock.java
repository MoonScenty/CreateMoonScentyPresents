package me.moonscenty.createmoonscentypresents.content.bellows;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.content.charring.CharcoalPitBlockEntity;
import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A leather bag between two boards, with its nozzle in the side of a charcoal pit.
 *
 * <p>Worked by hand and only by hand: a squeeze lifts the pit one rung up the heat
 * ladder for a second, and then it settles back. There is no way to hold it there, which
 * is the point - this is the stone age answer to a fire that is not hot enough, and the
 * answer is somebody standing there pumping.
 *
 * <p>FACING points at the pit rather than away from it, the same way the tapper faces
 * the log it is driven into: what matters is where the air goes.
 */
public class BellowsBlock extends HorizontalDirectionalBlock implements IBE<BellowsBlockEntity> {

    public static final MapCodec<BellowsBlock> CODEC = simpleCodec(BellowsBlock::new);

    /** The boards are 14 wide and the nozzle reaches the wall it is pointed at. */
    private static final VoxelShape SHAPE = Block.box(1, 0, 0, 15, 16, 15);

    public BellowsBlock(Properties properties) {
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

    /** Only ever stands against a charcoal pit, so placing it anywhere else is refused. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction towardsPit = context.getClickedFace().getOpposite();
        if (towardsPit.getAxis().isVertical())
            return null;
        BlockState placed = defaultBlockState().setValue(FACING, towardsPit);
        return canSurvive(placed, context.getLevel(), context.getClickedPos()) ? placed : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING))).is(ModBlocks.CHARCOAL_PIT.get());
    }

    /** Take the pit away and the bellows has nothing to blow into. */
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbour,
            LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        if (direction == state.getValue(FACING) && !canSurvive(state, level, pos))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** One squeeze. Working it again part way through starts the stroke over. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        BellowsBlockEntity bellows = getBlockEntity(level, pos);
        if (bellows == null)
            return InteractionResult.PASS;

        if (!level.isClientSide) {
            bellows.pump();
            boost(state, level, pos);
            level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.6F,
                    level.random.nextFloat() * 0.2F + 0.5F);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Pushes the heat onto the pit rather than having the pit go looking for bellows.
     *
     * <p>A pit checks its own fire every tick, and asking it to sweep its neighbours as
     * well would put that cost on every pit forever for something that happens when
     * somebody presses a button.
     */
    private static void boost(BlockState state, Level level, BlockPos pos) {
        BlockPos pitPos = pos.relative(state.getValue(FACING));
        if (level.getBlockEntity(pitPos) instanceof CharcoalPitBlockEntity pit)
            pit.blow(BellowsBlockEntity.STROKE_TICKS);
    }

    @Override
    public Class<BellowsBlockEntity> getBlockEntityClass() {
        return BellowsBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BellowsBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.BELLOWS.get();
    }
}
