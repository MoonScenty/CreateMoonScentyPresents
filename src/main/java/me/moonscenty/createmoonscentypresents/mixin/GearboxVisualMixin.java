package me.moonscenty.createmoonscentypresents.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.gearbox.GearboxVisual;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPartialModels;

/**
 * The Flywheel counterpart of {@link GearboxRendererMixin}, and the one that normally
 * runs - Flywheel's backend is on by default.
 *
 * <p>The visual builds one instancer for the shaft stubs in its constructor and reuses
 * it for all four faces, so there is a single lookup to replace.
 */
@Mixin(GearboxVisual.class)
public class GearboxVisualMixin {

    @Redirect(
            method = "<init>(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;"
                    + "Lcom/simibubi/create/content/kinetics/gearbox/GearboxBlockEntity;F)V",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
                    target = "Lcom/simibubi/create/AllPartialModels;SHAFT_HALF:"
                            + "Ldev/engine_room/flywheel/lib/model/baked/PartialModel;"))
    private PartialModel createmoonscentypresents$shaftHalf(VisualizationContext context,
            GearboxBlockEntity blockEntity, float partialTick) {
        return ModPartialModels.gearboxShaft(blockEntity.getBlockState());
    }
}
