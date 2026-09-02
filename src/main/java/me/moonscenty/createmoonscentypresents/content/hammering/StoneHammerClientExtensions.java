package me.moonscenty.createmoonscentypresents.content.hammering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * A raise-and-strike swing for the hammer, drawn in place of the vanilla use animation.
 *
 * <p>{@code ItemInHandRenderer} offers this hook just before its own {@code UseAnim}
 * switch; returning true replaces that block entirely, so the resting placement has to
 * be reproduced here too.
 *
 * <p>Where the saw rocks back and forth evenly, a hammer spends most of the cycle
 * lifting and then falls quickly - so the phase is shaped rather than a plain sine.
 *
 * <p>Client only: this class is referenced from {@code initializeClient}, which the
 * dedicated server never calls.
 */
public class StoneHammerClientExtensions implements IClientItemExtensions {

    /** Ticks per blow. Matches TICKS_PER_BLOW in the item so sound lands with the hit. */
    private static final float BLOW_TICKS = 10.0F;
    /** How far the head travels between raised and struck, in blocks. */
    private static final float SWING_REACH = 0.18F;
    /** Degrees the head tips over through the swing. */
    private static final float SWING_ROCK = 34.0F;
    /** Fraction of the cycle spent lifting; the rest is the fall. */
    private static final float LIFT_FRACTION = 0.7F;

    @Override
    public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack,
            float partialTick, float equipProcess, float swingProcess) {
        if (!player.isUsingItem() || player.getUseItem() != stack)
            return false;

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;

        // Vanilla's applyItemArmTransform - the resting spot in the corner of the screen.
        pose.translate(side * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);

        float ticks = player.getTicksUsingItem() + partialTick;
        float phase = (ticks % BLOW_TICKS) / BLOW_TICKS;

        // 0 at the moment of impact, 1 at the top of the lift. Slow up, fast down.
        float raised = phase < LIFT_FRACTION
                ? Mth.sin(phase / LIFT_FRACTION * Mth.HALF_PI)
                : 1.0F - (phase - LIFT_FRACTION) / (1.0F - LIFT_FRACTION);

        pose.translate(0.0F, raised * SWING_REACH * 0.5F, raised * SWING_REACH);
        pose.mulPose(Axis.XP.rotationDegrees(-raised * SWING_ROCK));

        return true;
    }
}
