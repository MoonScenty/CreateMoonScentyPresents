package me.moonscenty.createmoonscentypresents.content.foundry;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * A tap hung off a tank, opened by hand or by redstone, that pours straight down.
 *
 * <p>The stone age's only way to move a fluid: no pipe, no pump, just a hole let out of
 * the bottom of something and gravity underneath. It has to be stuck to a block that
 * holds fluid, and it breaks off if that block goes away.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed. Trimmed: no ladle, no
 * belt filling, and the burn is vanilla fire rather than a damage type of its own.
 */
public class FaucetBlock extends WrenchableDirectionalBlock implements IBE<FaucetBlockEntity> {

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    // A faucet on a wall sits in the half of its block nearest what it is tapping, and
    // one under a tank sits in the top half reaching up into it. The four side shapes are
    // the north one turned about Y; the down one is drawn where it stands.
    private static final VoxelShape SHAPE_DOWN = Block.box(4, 8, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_NORTH = Block.box(4, 6, 8, 12, 11, 15);
    private static final VoxelShape SHAPE_SOUTH = Block.box(4, 6, 1, 12, 11, 8);
    private static final VoxelShape SHAPE_WEST = Block.box(8, 6, 4, 15, 11, 12);
    private static final VoxelShape SHAPE_EAST = Block.box(1, 6, 4, 8, 11, 12);

    public FaucetBlock(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState().setValue(OPEN, false).setValue(POWERED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        return defaultBlockState()
                .setValue(FACING, face == Direction.UP ? Direction.NORTH : face)
                .setValue(OPEN, false)
                .setValue(POWERED, false);
    }

    /** Only stays up on something that holds fluid, which is what it is tapping. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        if (!(reader instanceof Level level))
            return false;
        Direction facing = state.getValue(FACING);
        BlockPos attached = pos.relative(facing.getOpposite());
        if (level.getBlockEntity(attached) == null)
            return false;

        IFluidHandler tank = level.getCapability(Capabilities.FluidHandler.BLOCK, attached, facing);
        if (tank != null && tank.getTanks() > 0)
            return true;
        tank = level.getCapability(Capabilities.FluidHandler.BLOCK, attached, null);
        return tank != null && tank.getTanks() > 0;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!canSurvive(state, level, pos))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_DOWN;
        };
    }

    /** Refuses to open onto nothing, so a tap left running does not drain into the air. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        withBlockEntityDo(level, pos, be -> {
            if (be.canOpenFaucet())
                toggle(state, level, pos);
        });
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean moving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) {
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
            return;
        }
        if (powered != state.getValue(OPEN))
            playSound(level, pos, powered);
        level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, POWERED);
        super.createBlockStateDefinition(builder);
    }

    public static void toggle(BlockState state, Level level, BlockPos pos) {
        boolean open = !state.getValue(OPEN);
        level.setBlockAndUpdate(pos, state.setValue(OPEN, open));
        playSound(level, pos, open);
    }

    private static void playSound(Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos,
                open ? BlockSetType.IRON.trapdoorOpen() : BlockSetType.IRON.trapdoorClose(),
                SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(null, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    public Class<FaucetBlockEntity> getBlockEntityClass() {
        return FaucetBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FaucetBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.FAUCET.get();
    }
}
