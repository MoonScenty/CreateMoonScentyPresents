package me.moonscenty.createmoonscentypresents.content.processing;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * A full cube with a front, turned to face whoever placed it.
 *
 * <p>{@link HorizontalDirectionalBlock} is abstract and adds nothing to the state
 * definition on its own, so even a block that only needs a facing needs a class.
 * This is that class, and nothing more - no behaviour of any kind.
 */
public class HorizontalCubeBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<HorizontalCubeBlock> CODEC = simpleCodec(HorizontalCubeBlock::new);

    public HorizontalCubeBlock(Properties properties) {
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
}
