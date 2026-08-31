package me.moonscenty.createmoonscentypresents;

import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.util.entry.ItemEntry;

import me.moonscenty.createmoonscentypresents.content.sawing.WoodenSawItem;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

public class ModItems {
    // Stone Age - fibre chain
    public static final ItemEntry<Item> PLANT_FIBER = simple("plant_fiber");
    public static final ItemEntry<Item> TWINE = simple("twine");
    public static final ItemEntry<Item> ROPE = simple("rope");

    // Stone Age - binding and sealing materials
    public static final ItemEntry<Item> RESIN = simple("resin");
    public static final ItemEntry<Item> LEATHER_STRIP = simple("leather_strip");

    // Stone Age - charcoal pit products
    public static final ItemEntry<Item> CHARCOAL_DUST = simple("charcoal_dust");
    public static final ItemEntry<Item> ASH = simple("ash");

    // Stone Age - clay ware, fired in the pit kiln
    public static final ItemEntry<Item> UNFIRED_CRUCIBLE = simple("unfired_crucible");
    public static final ItemEntry<Item> UNFIRED_INGOT_MOLD = simple("unfired_ingot_mold");
    public static final ItemEntry<Item> INGOT_MOLD = simple("ingot_mold");
    public static final ItemEntry<Item> UNFIRED_FIRE_BRICK = simple("unfired_fire_brick");
    public static final ItemEntry<Item> FIRE_BRICK = simple("fire_brick");

    // Stone Age - machine parts
    public static final ItemEntry<Item> WOODEN_BEARING = simple("wooden_bearing");
    public static final ItemEntry<Item> WOODEN_GEARBOX_COMPONENT = simple("wooden_gearbox_component");

    // Stone Age - tools
    public static final ItemEntry<Item> FLINT_KNIFE = simple("flint_knife");
    public static final ItemEntry<Item> STONE_HAMMER = simple("stone_hammer");
    public static final ItemEntry<Item> STONE_HAMMER_HEAD = simple("stone_hammer_head");
    public static final ItemEntry<Item> STONE_CHISEL = simple("stone_chisel");
    // item/generated only defines right-hand transforms, so the off hand falls back to
    // them plus vanilla's mirror - and for this sprite the mirrored side is the one
    // that points the teeth forward. Both hands are pinned here so they match: the main
    // hand gets the mirrored rotation, the off hand keeps the original values and lets
    // vanilla mirror them as before. handheld is also the correct parent for a tool.
    public static final ItemEntry<WoodenSawItem> WOODEN_SAW = CreateMoonScentyPresents.REGISTRATE
            .item("wooden_saw", WoodenSawItem::new)
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("item/handheld"))
                    .texture("layer0", prov.modLoc("item/" + ctx.getName()))
                    .transforms()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .rotation(0, 90, -25).translation(1.13f, 3.2f, 1.13f).scale(0.68f)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .rotation(0, -90, 25).translation(1.13f, 3.2f, 1.13f).scale(0.68f)
                    .end()
                    .end())
            .register();
    public static final ItemEntry<Item> ORE_PAN = simple("ore_pan");
    public static final ItemEntry<Item> WOODEN_TONGS = simple("wooden_tongs");
    public static final ItemEntry<Item> PRIMITIVE_SIEVE = simple("primitive_sieve");

    // Tin is not a vanilla metal and nothing in this pack provides the ore line, so
    // it lives here. The nugget stays Petrochem's; these follow its colour and share
    // the common tags, which is what lets both mods' tin be used interchangeably.
    public static final ItemEntry<Item> RAW_TIN = CreateMoonScentyPresents.REGISTRATE
            .item("raw_tin", Item::new)
            .tag(CommonMetal.TIN.rawOres, Tags.Items.RAW_MATERIALS)
            .register();

    public static final ItemEntry<Item> TIN_INGOT = CreateMoonScentyPresents.REGISTRATE
            .item("tin_ingot", Item::new)
            .tag(CommonMetal.TIN.ingots, Tags.Items.INGOTS)
            .register();

    // Ore dressing output: the panned concentrate that goes on to the millstone.
    public static final ItemEntry<Item> COPPER_CONCENTRATE =
            concentrate("copper_concentrate", ModTags.COPPER_CONCENTRATES);
    public static final ItemEntry<Item> TIN_CONCENTRATE =
            concentrate("tin_concentrate", ModTags.TIN_CONCENTRATES);

    // Millstone output: the meltable powder fed to the crucible furnace.
    public static final ItemEntry<Item> COPPER_DUST = dust("copper_dust", ModTags.COPPER_DUSTS);
    public static final ItemEntry<Item> TIN_DUST = dust("tin_dust", ModTags.TIN_DUSTS);
    // Obtainable in the stone age but not smeltable until later; a flux, not a metal.
    public static final ItemEntry<Item> IRON_DUST = dust("iron_dust", ModTags.IRON_DUSTS);
    public static final ItemEntry<Item> LIMESTONE_DUST = dust("limestone_dust", ModTags.LIMESTONE_DUSTS);

    private static ItemEntry<Item> dust(String name, TagKey<Item> metalTag) {
        return CreateMoonScentyPresents.REGISTRATE.item(name, Item::new)
                .tag(Tags.Items.DUSTS, metalTag)
                .register();
    }

    // Ore washing byproduct: small amounts recovered from gravel and tailings.
    public static final ItemEntry<Item> COPPER_FRAGMENT = fragment("copper_fragment", ModTags.COPPER_FRAGMENTS);
    public static final ItemEntry<Item> TIN_FRAGMENT = fragment("tin_fragment", ModTags.TIN_FRAGMENTS);

    // The ingot and plate come from Petrochem and Vintage Improvements; only the
    // nugget is missing from the pack.
    public static final ItemEntry<Item> BRONZE_NUGGET = CreateMoonScentyPresents.REGISTRATE
            .item("bronze_nugget", Item::new)
            .tag(Tags.Items.NUGGETS, ModTags.BRONZE_NUGGETS)
            .register();

    public static final ItemEntry<Item> BRONZE_BEARING = simple("bronze_bearing");

    private static ItemEntry<Item> fragment(String name, TagKey<Item> metalTag) {
        return CreateMoonScentyPresents.REGISTRATE.item(name, Item::new)
                .tag(ModTags.FRAGMENTS, metalTag)
                .register();
    }

    private static ItemEntry<Item> concentrate(String name, TagKey<Item> metalTag) {
        return CreateMoonScentyPresents.REGISTRATE.item(name, Item::new)
                .tag(ModTags.CONCENTRATES, metalTag)
                .register();
    }

    // Plain crafting material with no behaviour of its own.
    private static ItemEntry<Item> simple(String name) {
        return CreateMoonScentyPresents.REGISTRATE.item(name, Item::new).register();
    }

    // Called from the mod constructor purely to load this class, which declares
    // the entries above. Registrate handles the actual registry events.
    public static void register() {
    }
}
