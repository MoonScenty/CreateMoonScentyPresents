package me.moonscenty.createmoonscentypresents.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPoweredShaftBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModShaftBlock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Routes the shaft -> powered shaft swap to this mod's block.
 *
 * <p>The original returns {@code AllBlocks.POWERED_SHAFT} whatever it is given. Both
 * {@code SteamEngineBlock.onPlace} and {@code ShaftBlock.pickCorrectShaftType} call it,
 * so patching here covers placing the engine first and placing the shaft first alike.
 *
 * <p>The reverse direction is not handled here - see
 * {@link ModPoweredShaftBlock#tick}.
 */
@Mixin(PoweredShaftBlock.class)
public class PoweredShaftBlockMixin {

    @Inject(method = "getEquivalent", at = @At("HEAD"), cancellable = true)
    private static void createmoonscentypresents$modEquivalent(BlockState shaftState,
            CallbackInfoReturnable<BlockState> cir) {
        // Already ours - the engine is being reattached. Without this the original
        // would hand back Create's powered shaft and the wooden one would be lost.
        if (shaftState.getBlock() instanceof ModPoweredShaftBlock) {
            cir.setReturnValue(shaftState);
            return;
        }

        if (!(shaftState.getBlock() instanceof ModShaftBlock shaft))
            return;

        cir.setReturnValue(shaft.getPoweredVariant().defaultBlockState()
                .setValue(RotatedPillarKineticBlock.AXIS, shaftState.getValue(RotatedPillarKineticBlock.AXIS))
                .setValue(BlockStateProperties.WATERLOGGED,
                        shaftState.getValue(BlockStateProperties.WATERLOGGED)));
    }
}
