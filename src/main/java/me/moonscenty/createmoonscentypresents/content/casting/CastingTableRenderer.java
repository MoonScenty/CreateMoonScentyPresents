package me.moonscenty.createmoonscentypresents.content.casting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Draws what is on the table: the mould, the metal poured over it, and the casting once
 * it has set.
 *
 * <p>All three lie flat on the top face, one above the other, because that is what the
 * table is - a surface you put something on and pour onto. Only one of the mould and
 * the casting is ever there at a time, so they share the same height.
 */
public class CastingTableRenderer extends SafeBlockEntityRenderer<CastingTableBlockEntity> {

    /** The table is 14 pixels tall; everything sits just clear of that. */
    private static final float SURFACE = 14 / 16f;
    private static final float ITEM_HEIGHT = SURFACE + 0.5f / 16f;
    private static final float ITEM_SCALE = 0.75f;

    /** The pool of metal is a thin sheet across the middle of the top. */
    private static final float POOL_MIN = 3 / 16f;
    private static final float POOL_MAX = 13 / 16f;
    private static final float POOL_DEPTH = 1.5f / 16f;

    public CastingTableRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(CastingTableBlockEntity table, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        renderFlatItem(table, table.getMold(), ms, buffer, light, overlay);
        renderFluid(table, ms, buffer, light);
        // The casting sits on top of everything: once it is there, it is what the table
        // is showing.
        renderFlatItem(table, table.getResult(), ms, buffer, light, overlay);
    }

    private void renderFluid(CastingTableBlockEntity table, PoseStack ms, MultiBufferSource buffer,
            int light) {
        FluidStack fluid = table.getFluidTank().getFluid();
        if (fluid.isEmpty())
            return;

        float fill = fluid.getAmount() / (float) table.getFluidTank().getCapacity();
        float surface = SURFACE + POOL_DEPTH * Math.min(1f, fill);
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluid,
                POOL_MIN, SURFACE, POOL_MIN, POOL_MAX, surface, POOL_MAX,
                buffer, ms, light, false, false);
    }

    private void renderFlatItem(CastingTableBlockEntity table, ItemStack stack, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        if (stack.isEmpty())
            return;

        Direction facing = table.getBlockState().getValue(CastingTableBlock.FACING);

        ms.pushPose();
        ms.translate(0.5, ITEM_HEIGHT, 0.5);
        // A flat item model stands in the XY plane; tipping it back a quarter turn lays
        // it on the table, and the yaw then lines it up with the way the table faces.
        ms.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        ms.mulPose(Axis.XP.rotationDegrees(90));
        ms.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                light, overlay, ms, buffer, table.getLevel(), table.getBlockPos().hashCode());
        ms.popPose();
    }
}
