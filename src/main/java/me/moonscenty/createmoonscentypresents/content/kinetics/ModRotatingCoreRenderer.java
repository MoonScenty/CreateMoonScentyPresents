package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * For machines whose housing is a static block model with one turning part inside.
 *
 * <p>Create's equivalents name their own partial model in the renderer, which is why an
 * addon cannot just reuse them - the primitive millstone would spin an andesite cog.
 * This looks the model up by block in {@link ModPartialModels} instead, so one renderer
 * serves every machine of that shape.
 */
public class ModRotatingCoreRenderer<T extends KineticBlockEntity> extends KineticBlockEntityRenderer<T> {

    public ModRotatingCoreRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(T blockEntity, BlockState state) {
        return CachedBuffers.partial(ModPartialModels.rotating(state.getBlock()), state);
    }
}
