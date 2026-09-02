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

    /**
     * Zinc as it comes out of a foundry basin.
     *
     * <p>Hot enough to glow and thin enough to pour, which is the whole of what a molten
     * metal has to be here - it exists to be moved from the basin to a mould and set
     * again. There is no bucket for it on purpose: metal is carried by pouring, not by
     * being picked up.
     */
    public static final FluidEntry<BaseFlowingFluid.Flowing> MOLTEN_ZINC =
            CreateMoonScentyPresents.REGISTRATE.standardFluid("molten_zinc")
                    .lang("Molten Zinc")
                    .properties(p -> p.viscosity(1200).density(2000).temperature(693)
                            .lightLevel(10))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .noBucket()
                    .register();

    // Called from the mod constructor purely to load this class, which declares
    // the entries above. Registrate handles the actual registry events.
    public static void register() {
    }
}
