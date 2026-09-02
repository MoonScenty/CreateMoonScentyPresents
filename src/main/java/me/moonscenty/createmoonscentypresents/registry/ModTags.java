package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
    public static final TagKey<Item> BRONZE_INGOTS = item("ingots/bronze");

    // Create names the item a sheet but tags it under plates, which is the cross-mod
    // convention. Both are followed: the id reads like Create's, the tag matches
    // everyone else's.
    public static final TagKey<Item> PLATES = item("plates");
    public static final TagKey<Item> BRONZE_PLATES = item("plates/bronze");

    // Logs a hand drill has been through. Not a common tag - nothing else has the idea -
    // so these two live in this mod's own namespace. The block tag is what a tapper asks
    // when it wants to know whether it is leaning on something worth tapping; the item
    // tag lets one tapping recipe cover all eight woods.
    public static final TagKey<Block> HOLED_LOG_BLOCKS = block("holed_logs");
    public static final TagKey<Item> HOLED_LOGS = ownItem("holed_logs");

    private static TagKey<Item> item(String path) {
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
}
