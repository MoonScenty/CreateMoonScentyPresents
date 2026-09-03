package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Common tags introduced by this mod.
 * <p>
 * The standard metal forms already have keys in Create's {@code CommonMetal}, so only
 * the steps this mod invents live here. Ore concentrates are one of those - they sit
 * between crushed ore and dust, a stage no other mod names.
 */
public class ModTags {
    public static final TagKey<Item> CONCENTRATES = common("concentrates");
    public static final TagKey<Item> COPPER_CONCENTRATES = common("concentrates/copper");
    public static final TagKey<Item> TIN_CONCENTRATES = common("concentrates/tin");
    public static final TagKey<Item> ZINC_CONCENTRATES = common("concentrates/zinc");
    public static final TagKey<Item> IRON_CONCENTRATES = common("concentrates/iron");

    // c:dusts itself is a NeoForge tag (Tags.Items.DUSTS); only the per-metal
    // subtags need declaring, since CommonMetal does not cover dusts.
    public static final TagKey<Item> COPPER_DUSTS = common("dusts/copper");
    public static final TagKey<Item> TIN_DUSTS = common("dusts/tin");
    public static final TagKey<Item> IRON_DUSTS = common("dusts/iron");
    public static final TagKey<Item> LIMESTONE_DUSTS = common("dusts/limestone");

    // Low grade metal specks recovered from washing; another step no other mod names.
    public static final TagKey<Item> FRAGMENTS = common("fragments");
    public static final TagKey<Item> COPPER_FRAGMENTS = common("fragments/copper");
    public static final TagKey<Item> TIN_FRAGMENTS = common("fragments/tin");

    // Bronze is not one of Create's CommonMetal entries, so its tags live here.
    public static final TagKey<Item> BRONZE_NUGGETS = common("nuggets/bronze");
    public static final TagKey<Item> BRONZE_INGOTS = common("ingots/bronze");

    // Create names the item a sheet but tags it under plates, which is the cross-mod
    // convention. Both are followed: the id reads like Create's, the tag matches
    // everyone else's.
    public static final TagKey<Item> PLATES = common("plates");
    public static final TagKey<Item> BRONZE_PLATES = common("plates/bronze");

    // Logs a hand drill has been through. Not a common tag - nothing else has the idea -
    // so these two live in this mod's own namespace. The block tag is what a tapper asks
    // when it wants to know whether it is leaning on something worth tapping; the item
    // tag lets one tapping recipe cover all eight woods.
    public static final TagKey<Block> HOLED_LOG_BLOCKS = block("holed_logs");
    public static final TagKey<Item> HOLED_LOGS = ownItem("holed_logs");

    /**
     * A tag in the cross-mod {@code c} namespace.
     *
     * <p>Public because it is also how recipes name the c tags this mod does not declare
     * itself - zinc's forms, stripped logs. Create used to hand those out through
     * {@code AllTags.commonItemTag}, which it has marked for removal; the body of it was
     * this line.
     */
    public static TagKey<Item> common(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Item> ownItem(String path) {
        return TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, path));
    }

    private static TagKey<Block> block(String path) {
        return TagKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, path));
    }

    /**
     * Fluids hot enough to set light to whatever is standing in them. Molten metal is
     * what this is for; a faucet reads it to decide whether a pour hurts.
     */
    public static final TagKey<Fluid> MOLTEN =
            TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(
                    CreateMoonScentyPresents.MODID, "molten"));
}
