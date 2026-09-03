package me.moonscenty.createmoonscentypresents.content.bellows;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPartialModels;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the wheel inside the pump.
 *
 * <p>{@code partialFacing} rather than {@code partial}: the wheel is modelled lying one
 * way and the block can be pointed any of six, so it has to be turned to the facing
 * before the kinetic spin is applied. Drawn with the plain call it turns about whatever
 * axis it happened to be modelled on, which is only right for one facing out of six.
 *
 * <p>This is why the millstone's renderer cannot be reused here even though the shape of
 * the problem looks the same - a millstone has no facing to be turned to.
 */
public class AirPumpRenderer extends KineticBlockEntityRenderer<AirPumpBlockEntity> {

    public AirPumpRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(AirPumpBlockEntity blockEntity, BlockState state) {
        return CachedBuffers.partialFacing(ModPartialModels.rotating(state.getBlock()), state);
    }
}
