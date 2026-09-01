package me.moonscenty.createmoonscentypresents.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.gearbox.GearboxRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPartialModels;

import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Draws this mod's gearboxes with their own shaft stubs.
 *
 * <p>The shafts on a gearbox's faces are not part of its block model - the renderer
 * spins them itself, from a partial model named directly in the method. Only that
 * lookup is replaced here, so all of Create's rotation and direction maths is left
 * to do its own work.
 *
 * <p>This is the fallback path, used when Flywheel's backend is off;
 * {@link GearboxVisualMixin} covers the other one.
 */
@Mixin(GearboxRenderer.class)
public class GearboxRendererMixin {

    @Redirect(
            method = "renderSafe(Lcom/simibubi/create/content/kinetics/gearbox/GearboxBlockEntity;F"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
                    target = "Lcom/simibubi/create/AllPartialModels;SHAFT_HALF:"
                            + "Ldev/engine_room/flywheel/lib/model/baked/PartialModel;"))
    private PartialModel createmoonscentypresents$shaftHalf(GearboxBlockEntity blockEntity, float partialTicks,
            PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        return ModPartialModels.gearboxShaft(blockEntity.getBlockState());
    }
}
