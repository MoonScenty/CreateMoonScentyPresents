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
                    .tag(ModTags.MOLTEN)
                    .register();

    /**
     * Tin, which melts at a lower heat than zinc does.
     *
     * <p>232 degrees, the lowest melting point of any metal the pack uses, so a campfire
     * takes it easily. That is what makes bronze a stone age metal at all: the tin comes
     * out of washing iron, and melting it needs nothing the age does not already have.
     */
    public static final FluidEntry<BaseFlowingFluid.Flowing> MOLTEN_TIN =
            CreateMoonScentyPresents.REGISTRATE.standardFluid("molten_tin")
                    .lang("Molten Tin")
                    .properties(p -> p.viscosity(1100).density(1900).temperature(505)
                            .lightLevel(8))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .noBucket()
                    .tag(ModTags.MOLTEN)
                    .register();

    /**
     * Iron, which the stone age cannot reach.
     *
     * <p>1538 degrees. Its melting recipes ask for a blaze burner, so this exists for
     * the age after this one - the ore line is built now because washing iron is how tin
     * is found, not because iron can be poured yet. Drawn glowing rather than dull for
     * that reason: it is the one metal here that is genuinely hot.
     */
    public static final FluidEntry<BaseFlowingFluid.Flowing> MOLTEN_IRON =
            CreateMoonScentyPresents.REGISTRATE.standardFluid("molten_iron")
                    .lang("Molten Iron")
                    .properties(p -> p.viscosity(1400).density(2400).temperature(1811)
                            .lightLevel(14))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(20)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .noBucket()
                    .tag(ModTags.MOLTEN)
                    .register();

    /**
     * Copper, the other half of bronze.
     *
     * <p>Melting it is the one place the heat ladder is bent. Copper really wants 1085
     * degrees, which is a blaze burner - but a blaze burner is a trip to the Nether, and
     * putting the Nether in front of bronze would close the age the same way requiring
     * one for zinc would have closed it. The bend is deliberate and it is the only one;
     * see the melting recipes.
     */
    public static final FluidEntry<BaseFlowingFluid.Flowing> MOLTEN_COPPER =
            CreateMoonScentyPresents.REGISTRATE.standardFluid("molten_copper")
                    .lang("Molten Copper")
                    .properties(p -> p.viscosity(1300).density(2200).temperature(1358)
                            .lightLevel(12))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(22)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .noBucket()
                    .tag(ModTags.MOLTEN)
                    .register();

    /**
     * What the stone age is working towards: one part tin to three of copper.
     *
     * <p>The only fluid here that is made rather than melted, and the only one the mixer
     * produces. Everything the age built gets used once to reach it.
     */
    public static final FluidEntry<BaseFlowingFluid.Flowing> MOLTEN_BRONZE =
            CreateMoonScentyPresents.REGISTRATE.standardFluid("molten_bronze")
                    .lang("Molten Bronze")
                    .properties(p -> p.viscosity(1300).density(2100).temperature(1223)
                            .lightLevel(11))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(22)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .noBucket()
                    .tag(ModTags.MOLTEN)
                    .register();

    // Called from the mod constructor purely to load this class, which declares
    // the entries above. Registrate handles the actual registry events.
    public static void register() {
    }
}
