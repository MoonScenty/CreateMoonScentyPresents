package me.moonscenty.createmoonscentypresents.content.bellows;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * How far through a stroke the bellows is.
 *
 * <p>Nothing but the animation lives here. The heat it gives is held by the pit, which
 * is the thing that has to answer for it every tick; this only remembers how squeezed
 * the leather should be drawn.
 */
public class BellowsBlockEntity extends SmartBlockEntity {

    /** One squeeze, and the second the pit is held a rung higher. */
    public static final int STROKE_TICKS = 20;

    /** Down hard, back up slow - the push is the work and the rest is the leather. */
    private static final float PUSH_END = 0.35f;

    private int stroke;

    public BellowsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // None; a bellows is one countdown.
    }

    /**
     * Starts a stroke if the last one has finished.
     *
     * <p>Held down, the click repeats several times a second - far faster than the
     * leather can travel. Restarting on each one snapped the boards back to open every
     * few ticks and read as a stutter rather than a pump, so a stroke in progress is left
     * alone to finish. The air it puts on the fire is not gated by this; that keeps being
     * pushed out on every click, which is what holding the button is for.
     *
     * @return whether this click began a new stroke
     */
    public boolean pump() {
        if (stroke > 0)
            return false;
        stroke = STROKE_TICKS;
        notifyUpdate();
        return true;
    }

    public boolean isPumping() {
        return stroke > 0;
    }

    /**
     * How far the leather is squeezed right now, 0 open and 1 shut.
     *
     * @param partialTicks where between two ticks the frame is being drawn
     */
    public float squeeze(float partialTicks) {
        if (stroke <= 0)
            return 0;
        // stroke counts down, so this counts up from 0 at the press to 1 at the end.
        float elapsed = (STROKE_TICKS - stroke + partialTicks) / STROKE_TICKS;
        elapsed = Mth.clamp(elapsed, 0, 1);
        float phase = elapsed < PUSH_END
                ? elapsed / PUSH_END
                : 1 - (elapsed - PUSH_END) / (1 - PUSH_END);
        // Eased at both ends so the boards settle rather than stopping dead.
        return phase * phase * (3 - 2 * phase);
    }

    @Override
    public void tick() {
        super.tick();
        if (stroke > 0)
            stroke--;
    }

    // The countdown is the animation, so the client is the side that needs it. It is
    // not written to disk: a stroke is a second long and nobody saves mid-squeeze.
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (clientPacket && stroke > 0)
            tag.putInt("Stroke", stroke);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (clientPacket)
            stroke = tag.getInt("Stroke");
    }
}
