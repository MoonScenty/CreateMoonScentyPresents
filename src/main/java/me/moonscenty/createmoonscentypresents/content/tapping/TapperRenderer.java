package me.moonscenty.createmoonscentypresents.content.tapping;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/** Draws what is in the bucket: the sap it has collected, or the lump that has set. */
public class TapperRenderer extends SafeBlockEntityRenderer<TapperBlockEntity> {

    // Inside the staves. The walls sit at 2-3 and 13-14, and the floor at 1.
    private static final float INNER_MIN = 3 / 16f;
    private static final float INNER_MAX = 13 / 16f;
    private static final float FLOOR = 1 / 16f;
    private static final float RIM = 12 / 16f;

    /** Half scale, laid flat, sitting just clear of the floor. */
    private static final float ITEM_HEIGHT = 4 / 16f;
    private static final float ITEM_SCALE = 0.5f;

    public TapperRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(TapperBlockEntity tapper, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        renderFluid(tapper, ms, buffer, light);
        renderOutput(tapper, ms, buffer, light, overlay);
    }

    private void renderFluid(TapperBlockEntity tapper, PoseStack ms, MultiBufferSource buffer, int light) {
        FluidStack tank = tapper.getTank();
        if (tank.isEmpty())
            return;

        float surface = FLOOR + (RIM - FLOOR) * tapper.getFillLevel();
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(tank,
                INNER_MIN, FLOOR, INNER_MIN, INNER_MAX, surface, INNER_MAX,
                buffer, ms, light, false, false);
    }

    private void renderOutput(TapperBlockEntity tapper, PoseStack ms, MultiBufferSource buffer, int light,
            int overlay) {
        ItemStack output = tapper.getOutput();
        if (output.isEmpty())
            return;

        ms.pushPose();
        ms.translate(0.5, ITEM_HEIGHT, 0.5);
        ms.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemDisplayContext.FIXED,
                light, overlay, ms, buffer, tapper.getLevel(), tapper.getBlockPos().hashCode());
        ms.popPose();
    }
}
