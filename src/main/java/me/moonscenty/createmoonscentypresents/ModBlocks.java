package me.moonscenty.createmoonscentypresents;

import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import java.util.Map;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModCogwheelBlock;
import me.moonscenty.createmoonscentypresents.content.processing.BasinShapedBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModPoweredShaftBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModShaftBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {
    // Stone Age - low temperature firing. The model is Create's basin with the lid
    // merged in on top, so the item shows the same shape as the placed block;
    // see assets/.../models/block/pit_kiln.json.
    public static final BlockEntry<Block> PIT_KILN = CreateMoonScentyPresents.REGISTRATE
            .block("pit_kiln", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.noOcclusion())
            .transform(TagGen.pickaxeOnly())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/pit_kiln"))))
            .simpleItem()
            .register();

    // Stone Age - 32 RPM power transmission. Copied from Create's cogwheel: same
    // block class, block entity, item placement helper and bracketed model.
    public static final BlockEntry<ModCogwheelBlock> STONE_COGWHEEL =
            cogwheel("stone_cogwheel", false, () -> Blocks.STONE, SoundType.STONE, MapColor.STONE, true);
    public static final BlockEntry<ModCogwheelBlock> LARGE_STONE_COGWHEEL =
            cogwheel("large_stone_cogwheel", true, () -> Blocks.STONE, SoundType.STONE, MapColor.STONE, true);

    // Bronze Age - 64 RPM gearing. Same block class as the stone cogwheel.
    public static final BlockEntry<ModCogwheelBlock> BRONZE_COGWHEEL =
            cogwheel("bronze_cogwheel", false, () -> Blocks.COPPER_BLOCK, SoundType.COPPER,
                    MapColor.TERRACOTTA_ORANGE, false);

    // Stone Age - the vessel metal is melted in. Create's basin shape and render
    // layer, but none of its behaviour: no block entity, no item or fluid handling,
    // no processing recipes. Just the hollow it needs to look right.
    public static final BlockEntry<BasinShapedBlock> FIRED_CRUCIBLE = CreateMoonScentyPresents.REGISTRATE
            .block("fired_crucible", BasinShapedBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.mapColor(MapColor.COLOR_RED))
            .transform(TagGen.pickaxeOnly())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/fired_crucible"))))
            .addLayer(() -> RenderType::cutoutMipped)
            .simpleItem()
            .register();

    // Stone Age - 32 RPM power transmission. Copied from Create's shaft.
    public static final BlockEntry<ModShaftBlock> WOODEN_SHAFT = CreateMoonScentyPresents.REGISTRATE
            .block("wooden_shaft", p -> new ModShaftBlock(p, ModBlocks.WOODEN_POWERED_SHAFT))
            .initialProperties(() -> Blocks.OAK_PLANKS)
            .properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.WOOD).forceSolidOff())
            .transform(TagGen.axeOrPickaxe())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .simpleItem()
            .register();

    // No item of its own: an engine placed beside a wooden shaft swaps it in, and it
    // reverts when the engine goes. Broken by hand it gives the plain shaft back.
    public static final BlockEntry<ModPoweredShaftBlock> WOODEN_POWERED_SHAFT = CreateMoonScentyPresents.REGISTRATE
            .block("wooden_powered_shaft", p -> new ModPoweredShaftBlock(p, ModBlocks.WOODEN_SHAFT))
            .initialProperties(() -> Blocks.OAK_PLANKS)
            .properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.WOOD).forceSolidOn())
            .transform(TagGen.axeOrPickaxe())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .loot((lt, block) -> lt.dropOther(block, WOODEN_SHAFT.get()))
            .register();

    /** @param wooden whether an axe should work on it too - the stone cogwheels have a wooden axle. */
    private static BlockEntry<ModCogwheelBlock> cogwheel(String name, boolean large,
            NonNullSupplier<? extends Block> base, SoundType sound, MapColor color, boolean wooden) {
        return CreateMoonScentyPresents.REGISTRATE
                .block(name, large ? ModCogwheelBlock::large : ModCogwheelBlock::small)
                .initialProperties(base)
                .properties(p -> p.sound(sound).mapColor(color))
                .transform(wooden ? TagGen.axeOrPickaxe() : TagGen.pickaxeOnly())
                .blockstate(BlockStateGen.axisBlockProvider(false))
                // Draws the bracket that a wrench can add to the cogwheel.
                .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
                // Handles the "place a cogwheel against another one" behaviour.
                .item(CogwheelBlockItem::new)
                .build()
                .register();
    }

    // Tin ore and its storage block. Both sit at copper's tool tier, since tin has to
    // be reachable before bronze exists.
    public static final BlockEntry<Block> TIN_ORE = CreateMoonScentyPresents.REGISTRATE
            .block("tin_ore", Block::new)
            .initialProperties(() -> Blocks.COPPER_ORE)
            .properties(p -> p.mapColor(MapColor.STONE).requiresCorrectToolForDrops().sound(SoundType.STONE))
            .transform(TagGen.pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantments =
                        lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b, lt.createSilkTouchDispatchTable(b,
                        lt.applyExplosionDecay(b, LootItem.lootTableItem(ModItems.RAW_TIN.get())
                                .apply(ApplyBonusCount.addOreBonusCount(
                                        enchantments.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(Tags.Blocks.ORES)
            .transform(TagGen.tagBlockAndItem(Map.of(
                    CommonMetal.TIN.ores.blocks(), CommonMetal.TIN.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE)))
            .tag(Tags.Items.ORES)
            .build()
            .register();

    public static final BlockEntry<Block> DEEPSLATE_TIN_ORE = CreateMoonScentyPresents.REGISTRATE
            .block("deepslate_tin_ore", Block::new)
            .initialProperties(() -> Blocks.DEEPSLATE_COPPER_ORE)
            .properties(p -> p.mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE))
            .transform(TagGen.pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantments =
                        lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b, lt.createSilkTouchDispatchTable(b,
                        lt.applyExplosionDecay(b, LootItem.lootTableItem(ModItems.RAW_TIN.get())
                                .apply(ApplyBonusCount.addOreBonusCount(
                                        enchantments.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(Tags.Blocks.ORES)
            .transform(TagGen.tagBlockAndItem(Map.of(
                    CommonMetal.TIN.ores.blocks(), CommonMetal.TIN.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE)))
            .tag(Tags.Items.ORES)
            .build()
            .register();

    public static final BlockEntry<Block> RAW_TIN_BLOCK = CreateMoonScentyPresents.REGISTRATE
            .block("raw_tin_block", Block::new)
            .initialProperties(() -> Blocks.RAW_COPPER_BLOCK)
            .properties(p -> p.mapColor(MapColor.METAL).requiresCorrectToolForDrops())
            .transform(TagGen.pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .lang("Block of Raw Tin")
            .transform(TagGen.tagBlockAndItem(CommonMetal.TIN.rawStorageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> TIN_BLOCK = CreateMoonScentyPresents.REGISTRATE
            .block("tin_block", Block::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(p -> p.mapColor(MapColor.METAL).requiresCorrectToolForDrops())
            .transform(TagGen.pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(TagGen.tagBlockAndItem(CommonMetal.TIN.storageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    // Bronze Age - 64 RPM power transmission. Same classes as the wooden shaft; only
    // the textures and the powered-variant pairing differ.
    public static final BlockEntry<ModShaftBlock> BRONZE_SHAFT = CreateMoonScentyPresents.REGISTRATE
            .block("bronze_shaft", p -> new ModShaftBlock(p, ModBlocks.BRONZE_POWERED_SHAFT))
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_ORANGE).forceSolidOff())
            .transform(TagGen.pickaxeOnly())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .simpleItem()
            .register();

    public static final BlockEntry<ModPoweredShaftBlock> BRONZE_POWERED_SHAFT = CreateMoonScentyPresents.REGISTRATE
            .block("bronze_powered_shaft", p -> new ModPoweredShaftBlock(p, ModBlocks.BRONZE_SHAFT))
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_ORANGE).forceSolidOn())
            .transform(TagGen.pickaxeOnly())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .loot((lt, block) -> lt.dropOther(block, BRONZE_SHAFT.get()))
            .register();

    // Called from the mod constructor purely to load this class, which declares
    // the entries above. Registrate handles the actual registry events.
    public static void register() {
    }
}
