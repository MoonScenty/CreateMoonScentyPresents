package me.moonscenty.createmoonscentypresents.content.bellows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPartialModels;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the parts of the bellows that move.
 *
 * <p>The rib and the nozzle are in the block model and stay where they are: the middle
 * of the bellows is what is pinned, and it is also where the air leaves, so nothing
 * about the nozzle should shift. Both boards then draw in towards that middle and the
 * leather closes from both ends at once.
 *
 * <p>Each board travels the same distance, so the bag has to give up twice that - which
 * is what keeps the boards sitting exactly on the ends of the leather at every point in
 * the stroke.
 */
public class BellowsRenderer extends SafeBlockEntityRenderer<BellowsBlockEntity> {

    /** How far each board travels, in blocks. Both move, so the bag loses twice this. */
    private static final float TRAVEL = 3 / 16f;
    /** The middle of the bag, level with the rib, which is what it closes towards. */
    private static final float BAG_MIDDLE = 8 / 16f;
    private static final float BAG_HEIGHT = 12 / 16f;

    public BellowsRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(BellowsBlockEntity bellows, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        BlockState state = bellows.getBlockState();
        float travel = bellows.squeeze(partialTicks) * TRAVEL;
        VertexConsumer solid = buffer.getBuffer(RenderType.solid());

        draw(ModPartialModels.bellowsBag(), state, light, ms, solid, part -> {
            // Scaled about the rib, so the leather draws in from above and below at once.
            float remaining = (BAG_HEIGHT - 2 * travel) / BAG_HEIGHT;
            part.translate(0, BAG_MIDDLE, 0).scale(1, remaining, 1).translate(0, -BAG_MIDDLE, 0);
        });
        draw(ModPartialModels.bellowsTop(), state, light, ms, solid,
                part -> part.translate(0, -travel, 0));
        draw(ModPartialModels.bellowsBottom(), state, light, ms, solid,
                part -> part.translate(0, travel, 0));
    }

    /** Turns the part to face the way the block does, then applies the stroke to it. */
    private static void draw(PartialModel model, BlockState state, int light, PoseStack ms,
            VertexConsumer buffer, java.util.function.Consumer<SuperByteBuffer> squeeze) {
        SuperByteBuffer part = CachedBuffers.partial(model, state);
        // The blockstate is the plain horizontal one, and this is the turn Create pairs
        // with it everywhere - the crafter and the deployer both draw their parts this
        // way. Working the angle out by hand lands 180 degrees off.
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        part.center().rotateYDegrees(AngleHelper.horizontalAngle(facing)).uncenter();
        squeeze.accept(part);
        part.light(light).renderInto(ms, buffer);
    }
}
