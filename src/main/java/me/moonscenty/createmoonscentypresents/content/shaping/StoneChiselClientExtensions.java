package me.moonscenty.createmoonscentypresents.content.shaping;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * A back and forth stroke for the chisel, drawn in place of the vanilla use animation.
 * <p>
 * {@code ItemInHandRenderer} offers this hook just before its own {@code UseAnim}
 * switch; returning true replaces that block entirely, so the resting placement has to
 * be reproduced here too. Only the in-use case is taken over - everything else falls
 * back to vanilla by returning false.
 * <p>
 * Client only: this class is referenced from {@code initializeClient}, which the
 * dedicated server never calls.
 */
public class StoneChiselClientExtensions implements IClientItemExtensions {

    /** Ticks for one full push-pull. Shorter than vanilla's brush for a busier cut. */
    private static final float STROKE_TICKS = 12.0F;
    /** How far the chisel travels along the cut, in blocks. */
    private static final float STROKE_REACH = 0.09F;
    /** Degrees the edge rocks through the stroke. */
    private static final float STROKE_ROCK = 3.0F;

    @Override
    public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack,
            float partialTick, float equipProcess, float swingProcess) {
        if (!player.isUsingItem() || player.getUseItem() != stack)
            return false;

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;

        // Vanilla's applyItemArmTransform - the resting spot in the corner of the screen.
        // The item's own display transform already points the toothed edge forwards, so
        // nothing here re-orients the chisel; it only moves it.
        pose.translate(side * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);

        // -1..1 over one stroke. getTicksUsingItem counts up, so the phase is continuous
        // across the whole cut instead of resetting like the brush does every 10 ticks.
        float ticks = player.getTicksUsingItem() + partialTick;
        float stroke = Mth.sin(ticks / STROKE_TICKS * Mth.TWO_PI);

        // Push away from the camera and pull back: -Z is forwards in this space.
        pose.translate(0.0F, 0.0F, -stroke * STROKE_REACH);
        // A slight rock so the edge looks like it is biting rather than sliding.
        pose.mulPose(Axis.XP.rotationDegrees(stroke * STROKE_ROCK));

        return true;
    }
}
