package me.moonscenty.createmoonscentypresents.content.processing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Draws the hung item under the rack's pole, flat against the side it faces. */
public class DryingRackRenderer extends SafeBlockEntityRenderer<DryingRackBlockEntity> {

    // The pole sits at y 13-16ths. Half scale makes the item 8 pixels tall, so its
    // centre goes 4 below the pole for it to hang from rather than through it.
    private static final float HEIGHT = 9 / 16f;
    private static final float SCALE = 0.5f;

    public DryingRackRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(DryingRackBlockEntity rack, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        ItemStack stack = rack.getHeldItem();
        if (stack.isEmpty())
            return;

        BlockState state = rack.getBlockState();
        Direction facing = state.getValue(DryingRackBlock.FACING);

        ms.pushPose();
        ms.translate(0.5, HEIGHT, 0.5);
        // A flat item model lies in the XY plane facing south, which is where FACING
        // points at a yaw of zero - so turning back by the yaw squares the two up.
        ms.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        ms.scale(SCALE, SCALE, SCALE);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                light, overlay, ms, buffer, rack.getLevel(), rack.getBlockPos().hashCode());
        ms.popPose();
    }
}
