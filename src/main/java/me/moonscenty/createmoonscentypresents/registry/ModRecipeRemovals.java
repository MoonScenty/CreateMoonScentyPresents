package me.moonscenty.createmoonscentypresents.registry;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

/**
 * Recipes from other mods that this mod takes over.
 * <p>
 * Shipping a file at the same path does not reliably win - it depends on which mod's
 * pack ends up on top. Removing by id does not depend on anything: the entries are
 * dropped from the recipe manager's input before it parses them, and the replacement
 * is then a plain recipe of our own that happens to produce their item.
 */
public class ModRecipeRemovals {

    public static final Set<ResourceLocation> REMOVED = Set.of(
            create("crafting/materials/andesite_alloy"),
            create("crafting/materials/andesite_alloy_from_zinc"),
            create("mixing/andesite_alloy"),
            create("mixing/andesite_alloy_from_zinc"),
            create("crafting/kinetics/shaft"),
            create("item_application/andesite_casing_from_log"),
            create("item_application/andesite_casing_from_wood"),
            create("crafting/kinetics/mechanical_press"));

    private static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath("create", path);
    }
}
