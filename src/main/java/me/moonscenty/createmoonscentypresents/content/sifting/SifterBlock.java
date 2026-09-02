package me.moonscenty.createmoonscentypresents.content.sifting;

import me.moonscenty.createmoonscentypresents.content.kinetics.GrinderBlock;
import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * A frame with a mesh in it, turned from below.
 *
 * <p>It holds water. A sifter is mostly open, and standing in a stream is where one
 * belongs; recipes can ask for that state through {@link SiftingRecipe}.
 *
 * <p>The water it holds does not run out of it. A waterlogged block is a source, and a
 * source spreads as soon as its fluid is ticked - which is why a waterlogged fence next
 * to open air pours water. The two places that schedule that tick are overridden below
 * to leave it out, so the water inside still renders, still joins the water around it
 * and still comes out with a bucket, but never feeds a neighbouring block.
 */
public class SifterBlock extends GrinderBlock<SifterBlockEntity> implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SifterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placed = super.getStateForPlacement(context);
        if (placed == null)
            return null;
        boolean inWater = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return placed.setValue(WATERLOGGED, inWater);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    /** The default fills the block and then schedules the tick; this one only fills it. */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(WATERLOGGED) || fluidState.getType() != Fluids.WATER)
            return false;
        if (!level.isClientSide())
            level.setBlock(pos, state.setValue(WATERLOGGED, true), Block.UPDATE_ALL);
        return true;
    }

    @Override
    public Class<SifterBlockEntity> getBlockEntityClass() {
        return SifterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SifterBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SIFTER.get();
    }
}
