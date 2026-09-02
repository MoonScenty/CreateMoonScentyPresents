package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import com.tterrag.registrate.util.entry.FluidEntry;

import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class ModFluids {

    /**
     * Sap as it comes out of the tree, before it sets.
     *
     * <p>A standard fluid rather than one of Create's virtual ones: it gets a bucket and
     * a block, so a tapper can be emptied by hand long before there is any pipe to drain
     * it with. Thick and slow - it creeps rather than flows, which is most of what tells
     * it apart from water at a glance.
     */
    public static final FluidEntry<BaseFlowingFluid.Flowing> LIQUID_RESIN =
            CreateMoonScentyPresents.REGISTRATE.standardFluid("liquid_resin")
                    .lang("Liquid Resin")
                    .properties(p -> p.viscosity(2000).density(1400).temperature(300))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(30)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .register();

    // Called from the mod constructor purely to load this class, which declares
    // the entries above. Registrate handles the actual registry events.
    public static void register() {
    }
}
