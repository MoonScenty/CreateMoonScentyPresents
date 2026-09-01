package me.moonscenty.createmoonscentypresents.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.content.kinetics.crank.HandCrankVisual;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPartialModels;

/**
 * Draws this mod's hand crank with its own handle and base.
 *
 * <p>Neither part is in the block model: the base turns and the handle swings on an
 * angle of its own, so the visual builds both from partial models named directly in
 * its constructor. Only those two lookups are replaced.
 *
 * <p>This is the Flywheel path, and the one that normally runs;
 * {@code ModHandCrankBlockEntity#getRenderedHandle} covers the other.
 */
@Mixin(HandCrankVisual.class)
public class HandCrankVisualMixin {

    @Redirect(method = "<init>(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;"
            + "Lcom/simibubi/create/content/kinetics/crank/HandCrankBlockEntity;F)V",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
                    target = "Lcom/simibubi/create/AllPartialModels;HAND_CRANK_HANDLE:"
                            + "Ldev/engine_room/flywheel/lib/model/baked/PartialModel;"))
    private PartialModel createmoonscentypresents$handle(VisualizationContext context,
            HandCrankBlockEntity blockEntity, float partialTick) {
        return ModPartialModels.crankHandle(blockEntity.getBlockState().getBlock());
    }

    @Redirect(method = "<init>(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;"
            + "Lcom/simibubi/create/content/kinetics/crank/HandCrankBlockEntity;F)V",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
                    target = "Lcom/simibubi/create/AllPartialModels;HAND_CRANK_BASE:"
                            + "Ldev/engine_room/flywheel/lib/model/baked/PartialModel;"))
    private PartialModel createmoonscentypresents$base(VisualizationContext context,
            HandCrankBlockEntity blockEntity, float partialTick) {
        return ModPartialModels.crankBase(blockEntity.getBlockState().getBlock());
    }
}
