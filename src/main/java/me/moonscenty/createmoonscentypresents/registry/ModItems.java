package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModVerticalGearboxItem;
import me.moonscenty.createmoonscentypresents.content.applying.ApplicatorBrushItem;
import me.moonscenty.createmoonscentypresents.content.tapping.HandDrillItem;
import me.moonscenty.createmoonscentypresents.content.hammering.StoneHammerItem;
import me.moonscenty.createmoonscentypresents.content.shaping.StoneChiselItem;
import me.moonscenty.createmoonscentypresents.content.sawing.WoodenSawItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import top.theillusivec4.curios.api.CuriosTags;

public class ModItems {
    // Stone Age - fibre chain
    public static final ItemEntry<Item> PLANT_FIBER = simple("plant_fiber");
    public static final ItemEntry<Item> TWINE = simple("twine");
    public static final ItemEntry<Item> ROPE = simple("rope");

    // Stone Age - binding and sealing materials
    public static final ItemEntry<Item> RESIN = simple("resin");

    // Gate 1. Andesite alloy is the first thing every Create line needs, and this is
    // what stands in front of it: andesite has to be beaten into grit by hand before
    // it can be alloyed at all.
    public static final ItemEntry<Item> ANDESITE_GRIT = simple("andesite_grit");

    // Gate 2. Create's shaft is two alloy in a column; the lower one becomes a stave,
    // so every shaft costs a length of wood cut with the saw.
    public static final ItemEntry<Item> WOODEN_STAVE = simple("wooden_stave");

    // Gate 3. Not a product of Applying but the load for it: this goes in the brush and
    // is worked into a standing stripped log until the casing takes.
    public static final ItemEntry<Item> ANDESITE_CEMENT = simple("andesite_cement");

    // Gate 4, and the end of the age. The press cannot be reached with casings alone,
    // and it is also what brings create:shaft back.
    public static final ItemEntry<Item> STONE_DIE = simple("stone_die");

    // Age reward. Worn on the head; Create draws everything it shows, so the item
    // itself does nothing but tell Create it is being worn.
    public static final ItemEntry<Item> APPRENTICE_GOGGLES = CreateMoonScentyPresents.REGISTRATE
            .item("apprentice_goggles", Item::new)
            .properties(p -> p.stacksTo(1))
            .tag(CuriosTags.HEAD)
            .register();

    // Age reward. Worn on the back; the effect lives in GatherersSatchelCurio and is
    // only reachable through the Curios capability, so one carried in the inventory
    // does nothing.
    public static final ItemEntry<Item> GATHERERS_SATCHEL = CreateMoonScentyPresents.REGISTRATE
            .item("gatherers_satchel", Item::new)
            .properties(p -> p.stacksTo(1))
            .tag(CuriosTags.BACK)
            .register();

    // Holds one substance and works it into a placed block. The brush is the reusable
    // part and the load is the consumable, so another substance later needs a recipe
    // and nothing else - not another item, and not another mechanic.
    public static final ItemEntry<ApplicatorBrushItem> APPLICATOR_BRUSH = CreateMoonScentyPresents.REGISTRATE
            .item("applicator_brush", ApplicatorBrushItem::new)
            .model(ModItems::brushModels)
            .register();
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
    // Held against a material to break it down by hand - the hammering counterpart of
    // the wooden saw. handheld is the right parent for a tool.
    public static final ItemEntry<StoneHammerItem> STONE_HAMMER = CreateMoonScentyPresents.REGISTRATE
            .item("stone_hammer", StoneHammerItem::new)
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("item/handheld"))
                    .texture("layer0", prov.modLoc("item/" + ctx.getName())))
            .register();
    public static final ItemEntry<Item> STONE_HAMMER_HEAD = simple("stone_hammer_head");
    // Shapes stone and wood into machine parts by hand.
    public static final ItemEntry<StoneChiselItem> STONE_CHISEL = CreateMoonScentyPresents.REGISTRATE
            .item("stone_chisel", StoneChiselItem::new)
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("item/handheld"))
                    .texture("layer0", prov.modLoc("item/" + ctx.getName())))
            .register();
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
    // Bores a log so a tapper has somewhere to sit. Iron rather than stone: this is the
    // one place in the age where vanilla smelting, which was never gated, pays off.
    public static final ItemEntry<HandDrillItem> HAND_DRILL = CreateMoonScentyPresents.REGISTRATE
            .item("hand_drill", HandDrillItem::new)
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("item/handheld"))
                    .texture("layer0", prov.modLoc("item/" + ctx.getName())))
            .register();
    public static final ItemEntry<Item> WOODEN_TONGS = simple("wooden_tongs");
    public static final ItemEntry<Item> PRIMITIVE_SIEVE = simple("primitive_sieve");

    // Tin is not a vanilla metal and nothing else provides it, so the whole line is
    // ours. The common tags are still used, so any other mod's tin stays
    // interchangeable with it. Bronze age content; not used in the stone age.
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
    // The one the stone age actually uses: washed zinc ore, worth more in the foundry
    // than the ore it came from.
    public static final ItemEntry<Item> ZINC_CONCENTRATE =
            concentrate("zinc_concentrate", ModTags.ZINC_CONCENTRATES);

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

    // Bronze age content. Nothing else in the pack provides bronze, so the whole line
    // is ours. The textures follow the bronze already used by the cogwheel and bearing.
    public static final ItemEntry<Item> BRONZE_NUGGET = CreateMoonScentyPresents.REGISTRATE
            .item("bronze_nugget", Item::new)
            .tag(Tags.Items.NUGGETS, ModTags.BRONZE_NUGGETS)
            .register();

    public static final ItemEntry<Item> BRONZE_INGOT = CreateMoonScentyPresents.REGISTRATE
            .item("bronze_ingot", Item::new)
            .tag(Tags.Items.INGOTS, ModTags.BRONZE_INGOTS)
            .register();

    // Named like Create's sheets, tagged like everyone else's plates.
    public static final ItemEntry<Item> BRONZE_SHEET = CreateMoonScentyPresents.REGISTRATE
            .item("bronze_sheet", Item::new)
            .tag(ModTags.PLATES, ModTags.BRONZE_PLATES)
            .register();

    public static final ItemEntry<Item> BRONZE_BEARING = simple("bronze_bearing");

    // Not a block of its own: a second item for ModBlocks.PRIMITIVE_GEARBOX that stands
    // it on end. Lives here because it is registered as an item, not as a block.
    public static final ItemEntry<ModVerticalGearboxItem> PRIMITIVE_VERTICAL_GEARBOX =
            CreateMoonScentyPresents.REGISTRATE
                    .item("primitive_vertical_gearbox", ModVerticalGearboxItem::new)
                    .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/primitive_gearbox/item_vertical")))
                    .register();

    // The 64 RPM counterpart of the wooden component; both feed their era's gearbox.
    public static final ItemEntry<Item> BRONZE_GEARBOX_COMPONENT = simple("bronze_gearbox_component");

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

    /**
     * The brush is drawn by four models rather than one, the way vanilla draws its own:
     * a base, and three that swing it about in third person while it is in use. The
     * client class registers the {@code brushing} property that picks between them; the
     * first person motion is separate and comes from {@link net.minecraft.world.item.UseAnim#BRUSH}.
     */
    private static void brushModels(DataGenContext<Item, ApplicatorBrushItem> ctx,
            RegistrateItemModelProvider prov) {
        ResourceLocation texture = prov.modLoc("item/" + ctx.getName());
        ResourceLocation brushing = prov.modLoc("brushing");
        var mid = brushModel(prov, ctx.getName() + "_brushing_0", texture, 0, 4, 2);
        var raised = brushModel(prov, ctx.getName() + "_brushing_1", texture, 45, 0, 4);
        var over = brushModel(prov, ctx.getName() + "_brushing_2", texture, 90, -4, 2);
        brushModel(prov, ctx.getName(), texture, 45, 0, 4)
                .override().predicate(brushing, 0.25f).model(mid).end()
                .override().predicate(brushing, 0.5f).model(raised).end()
                .override().predicate(brushing, 0.75f).model(over).end();
    }

    /**
     * @param roll third person tilt; the off hand mirrors it, as does the sideways offset.
     */
    private static net.neoforged.neoforge.client.model.generators.ItemModelBuilder brushModel(
            RegistrateItemModelProvider prov, String name, ResourceLocation texture,
            float roll, float x, float y) {
        return prov.withExistingParent(name, prov.mcLoc("item/generated"))
                .texture("layer0", texture)
                .transforms()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                .rotation(55, -85, 0).translation(8.0f, 0.5f, -5.5f).scale(1)
                .end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(0, 0, roll).translation(x, y, 0).scale(0.9f)
                .end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .rotation(0, 0, -roll).translation(-x, y, 0).scale(0.9f)
                .end()
                .end();
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
