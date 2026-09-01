package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * The primitive hand crank.
 * <p>
 * The handle is not part of the block model - it swings on its own angle, drawn from a
 * partial model. Create names its own there, so this points at ours instead. This is
 * the no-Flywheel path; {@code HandCrankVisualMixin} covers the other one.
 */
public class ModHandCrankBlockEntity extends HandCrankBlockEntity {

    public ModHandCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SuperByteBuffer getRenderedHandle() {
        BlockState state = getBlockState();
        Direction facing = state.getOptionalValue(HandCrankBlock.FACING).orElse(Direction.UP);
        return CachedBuffers.partialFacing(ModPartialModels.crankHandle(state.getBlock()), state,
                facing.getOpposite());
    }
}
