package me.moonscenty.createmoonscentypresents.content.bellows;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A bellows that something else works.
 *
 * <p>Create's mechanical pump with the pumping taken out: the housing, the cog and the
 * way it takes rotation are all Create's, but it moves air rather than fluid and the only
 * thing it is connected to is the charcoal pit it faces. While it turns, that pit is held
 * a rung up the heat ladder - exactly what the hand bellows does, and for the same reason.
 *
 * <p>Which makes it the answer to the bellows rather than a second one. Melting copper by
 * hand means standing at a bellows for two hundred ticks; this does the standing, and it
 * is the first thing rotation is good for that is not just a faster millstone.
 */
public class AirPumpBlock extends DirectionalKineticBlock implements ICogWheel, IBE<AirPumpBlockEntity> {

    public static final MapCodec<AirPumpBlock> CODEC = simpleCodec(AirPumpBlock::new);

    public AirPumpBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    /** It turns about the way it points, the way Create's pump does. */
    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    /**
     * Points itself at a charcoal pit if it is being placed beside one.
     *
     * <p>There is only one thing it can blow into, so guessing that is nearly always
     * right - and getting it wrong means a machine that turns and does nothing, which
     * gives no hint about which way it should have gone.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction side : Direction.values()) {
            if (context.getLevel().getBlockState(context.getClickedPos().relative(side))
                    .is(ModBlocks.CHARCOAL_PIT.get()))
                return defaultBlockState().setValue(FACING, side);
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public Class<AirPumpBlockEntity> getBlockEntityClass() {
        return AirPumpBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AirPumpBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.AIR_PUMP.get();
    }
}
