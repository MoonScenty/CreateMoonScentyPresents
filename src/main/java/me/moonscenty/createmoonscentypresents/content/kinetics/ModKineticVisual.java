package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.function.Consumer;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Create's own visuals pick their models by comparing against {@code AllBlocks}, so
 * another mod's cogwheel silently falls back to a bare shaft with no teeth and its
 * shafts render Create's model. This is the same logic, with the model looked up by
 * block in {@link ModPartialModels} instead.
 */
public class ModKineticVisual {

    public static BlockEntityVisual<BracketedKineticBlockEntity> create(VisualizationContext context,
            BracketedKineticBlockEntity blockEntity, float partialTick) {
        BlockState state = blockEntity.getBlockState();
        if (ICogWheel.isLargeCog(state))
            return new LargeCogVisual(context, blockEntity, partialTick);
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick,
                Models.partial(ModPartialModels.rotating(state.getBlock())));
    }

    public static BlockEntityVisual<PoweredShaftBlockEntity> poweredShaft(VisualizationContext context,
            PoweredShaftBlockEntity blockEntity, float partialTick) {
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick,
                Models.partial(ModPartialModels.rotating(blockEntity.getBlockState().getBlock())));
    }

    /** For machines where only the part inside turns; the housing is the block model. */
    public static <T extends KineticBlockEntity> BlockEntityVisual<T> rotatingCore(
            VisualizationContext context, T blockEntity, float partialTick) {
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick,
                Models.partial(ModPartialModels.rotating(blockEntity.getBlockState().getBlock())));
    }

    /**
     * The same, for a machine that can be pointed anywhere.
     *
     * <p>The millstone always turns about Y, so its cog can be drawn exactly as it was
     * modelled. A machine with a facing cannot: its part is modelled for one direction
     * and has to be turned to the one it was placed in before the spin is applied, or it
     * turns about an axis it is not lying on.
     */
    public static <T extends KineticBlockEntity> BlockEntityVisual<T> facingRotatingCore(
            VisualizationContext context, T blockEntity, float partialTick) {
        BlockState state = blockEntity.getBlockState();
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick,
                state.getValue(DirectionalKineticBlock.FACING),
                Models.partial(ModPartialModels.rotating(state.getBlock())));
    }

    /** Large cogs render their shaft separately so its teeth can be offset to mesh. */
    public static class LargeCogVisual extends SingleAxisRotatingVisual<BracketedKineticBlockEntity> {

        protected final RotatingInstance additionalShaft;

        private LargeCogVisual(VisualizationContext context, BracketedKineticBlockEntity blockEntity,
                float partialTick) {
            super(context, blockEntity, partialTick,
                    Models.partial(ModPartialModels.rotating(blockEntity.getBlockState().getBlock())));

            Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);

            additionalShaft = instancerProvider()
                    .instancer(AllInstanceTypes.ROTATING,
                            Models.partial(ModPartialModels.cogShaft(blockEntity.getBlockState().getBlock())))
                    .createInstance();

            additionalShaft.rotateToFace(axis)
                    .setup(blockEntity)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos))
                    .setPosition(getVisualPosition())
                    .setChanged();
        }

        @Override
        public void update(float pt) {
            super.update(pt);
            additionalShaft.setup(blockEntity)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos))
                    .setChanged();
        }

        @Override
        public void updateLight(float partialTick) {
            super.updateLight(partialTick);
            relight(additionalShaft);
        }

        @Override
        protected void _delete() {
            super._delete();
            additionalShaft.delete();
        }

        @Override
        public void collectCrumblingInstances(Consumer<Instance> consumer) {
            super.collectCrumblingInstances(consumer);
            consumer.accept(additionalShaft);
        }
    }
}
