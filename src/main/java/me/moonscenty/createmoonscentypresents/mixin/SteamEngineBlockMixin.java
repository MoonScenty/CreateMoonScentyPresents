package me.moonscenty.createmoonscentypresents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPoweredShaftBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModShaftBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Opens the two engine-side checks that compare blocks by identity.
 *
 * <p>The original asks {@code AllBlocks.SHAFT.has(state) || AllBlocks.POWERED_SHAFT.has(state)}.
 * {@code BlockEntry.has()} is an equality check, so subclassing {@code ShaftBlock} is
 * not enough to get through it.
 *
 * <p>Any other mod's engine that extends {@code SteamEngineBlock} calls the same
 * static, so one check here covers those too.
 */
@Mixin(SteamEngineBlock.class)
public class SteamEngineBlockMixin {

    @Inject(method = "isShaftValid", at = @At("HEAD"), cancellable = true)
    private static void createmoonscentypresents$acceptModShafts(BlockState engineState, BlockState shaftState,
            CallbackInfoReturnable<Boolean> cir) {
        Block block = shaftState.getBlock();
        if (!(block instanceof ModShaftBlock) && !(block instanceof ModPoweredShaftBlock))
            return;

        // Same remaining condition as the original: the shaft may not lie along the
        // axis the engine faces.
        cir.setReturnValue(shaftState.getValue(RotatedPillarKineticBlock.AXIS)
                != SteamEngineBlock.getFacing(engineState).getAxis());
    }

    /**
     * Schedules the tick that turns the powered shaft back into a plain one.
     *
     * <p>The original is gated on {@code AllBlocks.POWERED_SHAFT.has(...)}, so our
     * powered shaft would simply stay behind after the engine is broken. The shaft sits
     * two blocks away, out of reach of neighbour updates, so this is the only path back.
     */
    @Inject(method = "onRemove", at = @At("RETURN"))
    private void createmoonscentypresents$scheduleRevert(BlockState state, Level level, BlockPos pos,
            BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (state.is(newState.getBlock()))
            return;

        BlockPos shaftPos = SteamEngineBlock.getShaftPos(state, pos);
        if (level.getBlockState(shaftPos).getBlock() instanceof ModPoweredShaftBlock shaft)
            level.scheduleTick(shaftPos, shaft, 1);
    }
}
