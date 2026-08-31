package me.moonscenty.createmoonscentypresents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Common tags introduced by this mod.
 * <p>
 * The standard metal forms already have keys in Create's {@code CommonMetal}, so only
 * the steps this mod invents live here. Ore concentrates are one of those - they sit
 * between crushed ore and dust, a stage no other mod names.
 */
public class ModTags {
    public static final TagKey<Item> CONCENTRATES = item("concentrates");
    public static final TagKey<Item> COPPER_CONCENTRATES = item("concentrates/copper");
    public static final TagKey<Item> TIN_CONCENTRATES = item("concentrates/tin");

    // c:dusts itself is a NeoForge tag (Tags.Items.DUSTS); only the per-metal
    // subtags need declaring, since CommonMetal does not cover dusts.
    public static final TagKey<Item> COPPER_DUSTS = item("dusts/copper");
    public static final TagKey<Item> TIN_DUSTS = item("dusts/tin");
    public static final TagKey<Item> IRON_DUSTS = item("dusts/iron");
    public static final TagKey<Item> LIMESTONE_DUSTS = item("dusts/limestone");

    // Low grade metal specks recovered from washing; another step no other mod names.
    public static final TagKey<Item> FRAGMENTS = item("fragments");
    public static final TagKey<Item> COPPER_FRAGMENTS = item("fragments/copper");
    public static final TagKey<Item> TIN_FRAGMENTS = item("fragments/tin");

    // Bronze is not one of Create's CommonMetal entries, so its tags live here.
    public static final TagKey<Item> BRONZE_NUGGETS = item("nuggets/bronze");

    private static TagKey<Item> item(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
