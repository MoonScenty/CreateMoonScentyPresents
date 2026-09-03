package me.moonscenty.createmoonscentypresents.content.foundry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPartialModels;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the three parts of the mixer that move: the cog on top, the pole that drops into
 * the basin, and the head on the end of it.
 *
 * <p>The same drawing Create does for its own mixer, with this mod's models. Create's
 * renderer names its parts through {@code AllPartialModels}, so reusing it would put an
 * andesite whisk on a fire brick machine - and registering no renderer at all, which is
 * what was here before, drew nothing but the housing.
 *
 * <p>Create's version bows out when Flywheel is running because it has a visual to take
 * over. This has none, so it always draws.
 */
public class FoundryMixerRenderer extends KineticBlockEntityRenderer<FoundryMixerBlockEntity> {

    /** The pole reaches below the block, so it has to be drawn even when the block is not. */
    private static final boolean OFF_SCREEN = true;

    public FoundryMixerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(FoundryMixerBlockEntity mixer) {
        return OFF_SCREEN;
    }

    @Override
    protected void renderSafe(FoundryMixerBlockEntity mixer, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        BlockState state = mixer.getBlockState();
        VertexConsumer solid = buffer.getBuffer(RenderType.solid());

        SuperByteBuffer cog = CachedBuffers.partial(ModPartialModels.mixerCog(), state);
        standardKineticRotationTransform(cog, mixer, light).renderInto(ms, solid);

        // How far the head has dropped, and how far round it has turned by now. The six
        // tenths is Create's own figure, kept so the whisk spins at the pace the rest of
        // its machines do.
        float drop = mixer.getRenderedHeadOffset(partialTicks);
        float speed = mixer.getRenderedHeadRotationSpeed(partialTicks);
        float angle = AnimationTickHolder.getRenderTime(mixer.getLevel()) * speed * 6f / 10f % 360;

        SuperByteBuffer pole = CachedBuffers.partial(ModPartialModels.mixerPole(), state);
        pole.translate(0, -drop, 0).light(light).renderInto(ms, solid);

        // The head has holes in it, so it goes on the cutout layer rather than the solid
        // one - on solid its gaps would come out black.
        VertexConsumer cutout = buffer.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer head = CachedBuffers.partial(ModPartialModels.mixerHead(), state);
        head.rotateCenteredDegrees(angle, Axis.YP)
                .translate(0, -drop, 0)
                .light(light)
                .renderInto(ms, cutout);
    }
}
