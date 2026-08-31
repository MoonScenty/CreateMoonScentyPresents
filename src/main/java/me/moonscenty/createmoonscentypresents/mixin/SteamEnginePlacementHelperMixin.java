package me.moonscenty.createmoonscentypresents.mixin;

import java.util.function.Function;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModShaftBlock;

import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Opens the ghost guide shown when a shaft is held against an engine.
 *
 * <p>Whether rotation connects and whether the ghost appears are two separate checks:
 * the first is {@code isShaftValid}, the second is this placement helper. Without this
 * the wooden shaft would work but show no preview.
 */
@Mixin(targets = "com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock$PlacementHelper")
public class SteamEnginePlacementHelperMixin {

    /** The original accepts Create's shaft item only. */
    @Inject(method = "getItemPredicate", at = @At("RETURN"), cancellable = true)
    private void createmoonscentypresents$acceptModShaft(CallbackInfoReturnable<Predicate<ItemStack>> cir) {
        Predicate<ItemStack> original = cir.getReturnValue();
        cir.setReturnValue(original.or(stack -> stack.getItem() instanceof BlockItem item
                && item.getBlock() instanceof ModShaftBlock));
    }

    /**
     * Shows our powered shaft in the preview instead of Create's.
     *
     * <p>The original transform always yields Create's powered shaft on the client, to
     * preview the swap the engine performs. Holding a wooden shaft that previews the
     * wrong block. The server path places the held block and is left alone.
     */
    @Inject(method = "getOffset", at = @At("RETURN"), cancellable = true)
    private void createmoonscentypresents$modGhost(Player player, Level level, BlockState state, BlockPos pos,
            BlockHitResult ray, CallbackInfoReturnable<PlacementOffset> cir) {
        PlacementOffset offset = cir.getReturnValue();
        if (!offset.isSuccessful())
            return;

        Function<BlockState, BlockState> original = offset.getTransform();
        cir.setReturnValue(offset.withTransform(held -> {
            BlockState result = original.apply(held);
            if (!(held.getBlock() instanceof ModShaftBlock shaft))
                return result;
            if (!(result.getBlock() instanceof PoweredShaftBlock))
                return result; // not a swap path; the held shaft is placed as-is

            return shaft.getPoweredVariant().defaultBlockState()
                    .setValue(RotatedPillarKineticBlock.AXIS, result.getValue(RotatedPillarKineticBlock.AXIS));
        }));
    }
}
