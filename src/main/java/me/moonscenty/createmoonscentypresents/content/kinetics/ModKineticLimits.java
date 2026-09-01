package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.Map;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * How fast each age's rotation parts can be driven before they give out.
 *
 * <p>This is the gate between the ages: stone age parts take 32 RPM, bronze parts 64,
 * so reaching a faster network means building the next age's parts first.
 *
 * <p>Keyed by block id rather than by class, because one class serves every age - the
 * same {@link ModShaftBlock} is both the wooden and the bronze shaft.
 */
public class ModKineticLimits {

    /** Parts with no entry are not gated. */
    public static final int UNLIMITED = 0;

    private static final Map<String, Integer> LIMITS = Map.ofEntries(
            Map.entry("wooden_shaft", 32),
            Map.entry("wooden_powered_shaft", 32),
            Map.entry("stone_cogwheel", 32),
            Map.entry("large_stone_cogwheel", 32),
            // The vertical gearbox is the same block placed on a different axis, so it
            // is covered by this one entry.
            Map.entry("primitive_gearbox", 32),
            Map.entry("primitive_millstone", 32),
            Map.entry("primitive_sifter", 32),
            Map.entry("bronze_shaft", 64),
            Map.entry("bronze_powered_shaft", 64),
            Map.entry("bronze_cogwheel", 64));

    public static int of(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return LIMITS.getOrDefault(id.getPath(), UNLIMITED);
    }

    /**
     * Breaks the part if its network is driving it past what it can take.
     *
     * <p>Breaking rather than stalling is what makes the gate work. A kinetic network
     * carries one speed through every block on it, so a part cannot quietly refuse to
     * turn while staying connected - it would still be spun by its neighbours. Taking
     * it out of the world splits the network, and everything downstream of the break
     * loses its source and coasts to a stop.
     *
     * <p>The part is dropped, not lost.
     */
    public static void enforce(KineticBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null || level.isClientSide)
            return;

        int limit = of(blockEntity.getBlockState());
        if (limit == UNLIMITED || Math.abs(blockEntity.getSpeed()) <= limit)
            return;
        if (!isEntryPoint(blockEntity))
            return;

        level.destroyBlock(blockEntity.getBlockPos(), true);
    }

    /**
     * Whether the overspeed enters this mod's parts here, rather than further upstream.
     *
     * <p>Without this the whole powered run gives way at once: every part on the
     * network sees the same speed, so every one of them would break on the same tick.
     * Only the part being fed by something that is itself within its limits gives -
     * the one where the too-fast rotation first arrives. The parts behind it are
     * spared because they lose their source the moment it goes.
     */
    private static boolean isEntryPoint(KineticBlockEntity blockEntity) {
        if (!blockEntity.hasSource())
            return true;
        if (!(blockEntity.getLevel().getBlockEntity(blockEntity.source) instanceof KineticBlockEntity upstream))
            return true;

        // A neighbour over its own limit is nearer the source than we are, so it is the
        // one that should give. A bronze shaft feeding a wooden one at 48 RPM is not:
        // it is within its 64, and the wooden part is where the run has to stop.
        int upstreamLimit = of(upstream.getBlockState());
        return upstreamLimit == UNLIMITED || Math.abs(upstream.getSpeed()) <= upstreamLimit;
    }
}
