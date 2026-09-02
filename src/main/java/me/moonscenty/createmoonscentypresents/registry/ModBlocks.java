package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import java.util.List;
import java.util.Map;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModCogwheelBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModGearboxBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModHandCrankBlock;
import me.moonscenty.createmoonscentypresents.content.milling.MillstoneBlock;
import me.moonscenty.createmoonscentypresents.content.charring.CharcoalPitBlock;
import me.moonscenty.createmoonscentypresents.content.firing.PitKilnBlock;
import me.moonscenty.createmoonscentypresents.content.foundry.FoundryBasinBlock;
import me.moonscenty.createmoonscentypresents.content.foundry.FoundryLidBlock;
import me.moonscenty.createmoonscentypresents.content.processing.BasinShapedBlock;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRackBlock;
import me.moonscenty.createmoonscentypresents.content.processing.HorizontalCubeBlock;
import me.moonscenty.createmoonscentypresents.content.tapping.TapperBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModPoweredShaftBlock;
import me.moonscenty.createmoonscentypresents.content.kinetics.ModShaftBlock;
import me.moonscenty.createmoonscentypresents.content.sifting.SifterBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
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
    public static final BlockEntry<PitKilnBlock> PIT_KILN = CreateMoonScentyPresents.REGISTRATE
            .block("pit_kiln", PitKilnBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.noOcclusion())
            .transform(TagGen.pickaxeOnly())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/pit_kiln"))))
            .simpleItem()
            .register();

    // Stone Age - wood into charcoal. vanilla's blast furnace shape, with its plain stone body replaced by the bricks
    // texture pulled towards grey. The furnace's own face and base are left alone.
    public static final BlockEntry<CharcoalPitBlock> CHARCOAL_PIT = CreateMoonScentyPresents.REGISTRATE
            .block("charcoal_pit", CharcoalPitBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.mapColor(MapColor.STONE))
            .transform(TagGen.pickaxeOnly())
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(),
                    prov.models().orientable(ctx.getName(),
                            prov.modLoc("block/charcoal_pit_side"),
                            prov.modLoc("block/charcoal_pit_front"),
                            prov.modLoc("block/charcoal_pit_top"))))
            .simpleItem()
            .register();

    // Stone Age - time based drying. Hand made model; see
    // assets/.../models/block/drying_rack.json.
    //
    // 270 rather than the default 180: the rack is meant to be seen from its broad
    // side, with the pole running left to right, but the model is drawn with that
    // side lying along east-west. The extra quarter turn puts it square to whoever placed
    // it, instead of showing them one of the end frames.
    public static final BlockEntry<DryingRackBlock> DRYING_RACK = CreateMoonScentyPresents.REGISTRATE
            .block("drying_rack", DryingRackBlock::new)
            .initialProperties(() -> Blocks.OAK_FENCE)
            .properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.WOOD).noOcclusion())
            .transform(TagGen.axeOrPickaxe())
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/drying_rack")), 270))
            .simpleItem()
            .register();

    // Stone Age - grinds concentrates, charcoal and grain. Create's millstone taken
    // whole: its block class, block entity, renderer and milling recipes are reused,
    // and its model and textures are copied into this mod so they can be redrawn.
    //
    // No noOcclusion, matching Create: the millstone's own shape is not a full cube, so
    // light already reaches the cell the turning cog is drawn in.
    public static final BlockEntry<MillstoneBlock> PRIMITIVE_MILLSTONE = CreateMoonScentyPresents.REGISTRATE
            .block("primitive_millstone", MillstoneBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.mapColor(MapColor.METAL))
            .transform(TagGen.pickaxeOnly())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/primitive_millstone/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    prov.modLoc("block/primitive_millstone/item")))
            .build()
            .register();

    // Stone Age - splits and reverses rotation across four sides. Create's gearbox
    // taken whole: its block class, block entity, renderer and visual are reused, and
    // its model and textures are copied into this mod so they can be redrawn.
    // Create's crank already turns at 32 RPM, the stone age cap, so only its looks
    // and block entity needed redirecting.
    public static final BlockEntry<ModHandCrankBlock> PRIMITIVE_HAND_CRANK = CreateMoonScentyPresents.REGISTRATE
            .block("primitive_hand_crank", ModHandCrankBlock::new)
            .initialProperties(() -> Blocks.OAK_PLANKS)
            .properties(p -> p.mapColor(MapColor.WOOD).sound(SoundType.WOOD))
            .transform(TagGen.axeOrPickaxe())
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .onRegister(BlockStressValues.setGeneratorSpeed(32))
            .onRegister(block -> BlockStressValues.CAPACITIES.register(block, () -> 8.0))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    prov.modLoc("block/primitive_hand_crank/item")))
            .build()
            .register();

    // Its own machine, not a millstone with another recipe list: it holds water and
    // its recipes can require that. A wooden frame rather than a stone one, which is
    // also what tells it apart in the world.
    public static final BlockEntry<SifterBlock> PRIMITIVE_SIFTER = CreateMoonScentyPresents.REGISTRATE
            .block("primitive_sifter", SifterBlock::new)
            .initialProperties(() -> Blocks.OAK_PLANKS)
            .properties(p -> p.mapColor(MapColor.WOOD).sound(SoundType.WOOD))
            .transform(TagGen.axeOrPickaxe())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/primitive_sifter/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    prov.modLoc("block/primitive_sifter/item")))
            .build()
            .register();

    public static final BlockEntry<ModGearboxBlock> PRIMITIVE_GEARBOX = CreateMoonScentyPresents.REGISTRATE
            .block("primitive_gearbox", ModGearboxBlock::new)
            .initialProperties(() -> Blocks.STONE)
            // noOcclusion is not cosmetic here. The shafts on the faces are drawn by the
            // renderer at the block's own position, so they take that cell's light; left
            // occluding, the cell is dark and the shafts come out black.
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .transform(TagGen.axeOrPickaxe())
            .blockstate((ctx, prov) -> BlockStateGen.axisBlock(ctx, prov,
                    state -> prov.models().getExistingFile(prov.modLoc("block/primitive_gearbox/block")), true))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    prov.modLoc("block/primitive_gearbox/item")))
            .build()
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

    // Stone Age - the vessel metal is melted in. The foundry basin from Create:
    // Metallurgy, which is Create's basin with more room and a spout; the melting
    // itself is driven by the lid on top of it.
    public static final BlockEntry<FoundryBasinBlock> FOUNDRY_BASIN = CreateMoonScentyPresents.REGISTRATE
            .block("foundry_basin", FoundryBasinBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion())
            .transform(TagGen.pickaxeOnly())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/foundry_basin/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    prov.modLoc("block/foundry_basin/block")))
            .build()
            .register();

    // Stone Age - shuts a foundry basin in so what is in it can melt.
    public static final BlockEntry<FoundryLidBlock> FOUNDRY_LID = CreateMoonScentyPresents.REGISTRATE
            .block("foundry_lid", FoundryLidBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion())
            .transform(TagGen.pickaxeOnly())
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), state -> prov.models()
                    .getExistingFile(prov.modLoc("block/foundry_lid/" + lidModel(state)))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    prov.modLoc("block/foundry_lid/block")))
            .build()
            .register();

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

    // Stone Age - the only thing in the age that works while nobody is watching. Stands
    // in the cell beside a bored log with its spout in the wood; see TapperBlock for why
    // FACING points at the log rather than away from it.
    public static final BlockEntry<TapperBlock> TAPPER = CreateMoonScentyPresents.REGISTRATE
            .block("tapper", TapperBlock::new)
            .initialProperties(() -> Blocks.OAK_PLANKS)
            .properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.WOOD).noOcclusion())
            .transform(TagGen.axeOrPickaxe())
            // 0, not the default 180: that default assumes a model drawn with its front
            // on the north face, and the spout is drawn on the south one. Left at 180 the
            // tapper would point its spout away from the log it is standing on.
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/tapper")), 0))
            .simpleItem()
            .register();

    /** One bored log, the wood it came from, and the planks a saw gets back out of it. */
    public record HoledLog(String wood, Block log, ItemLike planks, BlockEntry<RotatedPillarBlock> block) {
    }

    /**
     * The eight overworld logs. Nether stems are fungus and have no sap in them, and a
     * stripped log has had the bark taken off - which is where the sap runs.
     */
    public static final List<HoledLog> HOLED_LOGS = List.of(
            holedLog("oak", Blocks.OAK_LOG, Items.OAK_PLANKS),
            holedLog("spruce", Blocks.SPRUCE_LOG, Items.SPRUCE_PLANKS),
            holedLog("birch", Blocks.BIRCH_LOG, Items.BIRCH_PLANKS),
            holedLog("jungle", Blocks.JUNGLE_LOG, Items.JUNGLE_PLANKS),
            holedLog("acacia", Blocks.ACACIA_LOG, Items.ACACIA_PLANKS),
            holedLog("dark_oak", Blocks.DARK_OAK_LOG, Items.DARK_OAK_PLANKS),
            holedLog("mangrove", Blocks.MANGROVE_LOG, Items.MANGROVE_PLANKS),
            holedLog("cherry", Blocks.CHERRY_LOG, Items.CHERRY_PLANKS));

    /**
     * What a drill turns this log into, or null if it is not a log the drill knows.
     *
     * <p>Walked rather than kept in a map: the entries cannot be resolved while this
     * class is still loading, and eight comparisons on a right click is nothing.
     */
    public static Block holedVariantOf(Block log) {
        for (HoledLog holed : HOLED_LOGS)
            if (holed.log() == log)
                return holed.block().get();
        return null;
    }

    private static HoledLog holedLog(String wood, Block log, ItemLike planks) {
        BlockEntry<RotatedPillarBlock> entry = CreateMoonScentyPresents.REGISTRATE
                .block("holed_" + wood + "_log", RotatedPillarBlock::new)
                .initialProperties(() -> log)
                .transform(TagGen.axeOnly())
                // The ends are the vanilla log ends; only the bark was redrawn.
                .blockstate((ctx, prov) -> prov.axisBlock(ctx.getEntry(),
                        prov.modLoc("block/" + ctx.getName()),
                        ResourceLocation.withDefaultNamespace("block/" + wood + "_log_top")))
                .tag(ModTags.HOLED_LOG_BLOCKS)
                .item()
                .tag(ModTags.HOLED_LOGS)
                .build()
                .register();
        return new HoledLog(wood, log, planks, entry);
    }

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

    /** Open or shut, plain or windowed - four models off two boolean states. */
    private static String lidModel(net.minecraft.world.level.block.state.BlockState state) {
        boolean window = state.getValue(FoundryLidBlock.WINDOW);
        if (state.getValue(FoundryLidBlock.OPEN))
            return window ? "block_open_window" : "block_open";
        return window ? "block_window" : "block";
    }
}
