package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.simibubi.create.content.kinetics.millstone.MillstoneRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws this mod's millstone with its own turning cog.
 *
 * <p>The cog inside is not part of the block model - the renderer spins it from a
 * partial model. Create names its own there, so without this the primitive millstone
 * would turn an andesite cog. Unlike the gearbox, the lookup sits in a method of its
 * own, so overriding is enough and no mixin is needed.
 */
public class ModMillstoneRenderer extends MillstoneRenderer {

    public ModMillstoneRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(MillstoneBlockEntity blockEntity, BlockState state) {
        return CachedBuffers.partial(ModPartialModels.rotating(state.getBlock()), state);
    }
}
