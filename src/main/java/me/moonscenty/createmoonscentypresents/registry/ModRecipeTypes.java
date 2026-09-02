package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import me.moonscenty.createmoonscentypresents.content.applying.ApplyingRecipe;
import me.moonscenty.createmoonscentypresents.content.hammering.HammeringRecipe;
import me.moonscenty.createmoonscentypresents.content.shaping.ShapingRecipe;
import me.moonscenty.createmoonscentypresents.content.casting.CastingRecipe;
import me.moonscenty.createmoonscentypresents.content.charring.CharringRecipe;
import me.moonscenty.createmoonscentypresents.content.foundry.FoundryRecipe;
import me.moonscenty.createmoonscentypresents.content.firing.FiringRecipe;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModRecipeTypeInfo;
import me.moonscenty.createmoonscentypresents.content.milling.MillingRecipe;
import me.moonscenty.createmoonscentypresents.content.sifting.SiftingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.CoagulatingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.TappingRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CreateMoonScentyPresents.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateMoonScentyPresents.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SawingRecipe>> SAWING =
            TYPES.register("sawing", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "sawing")));

    public static final DeferredHolder<RecipeSerializer<?>, SawingRecipe.Serializer> SAWING_SERIALIZER =
            SERIALIZERS.register("sawing", SawingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<HammeringRecipe>> HAMMERING =
            TYPES.register("hammering", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "hammering")));

    public static final DeferredHolder<RecipeSerializer<?>, HammeringRecipe.Serializer> HAMMERING_SERIALIZER =
            SERIALIZERS.register("hammering", HammeringRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ApplyingRecipe>> APPLYING =
            TYPES.register("applying", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "applying")));

    public static final DeferredHolder<RecipeSerializer<?>, ApplyingRecipe.Serializer> APPLYING_SERIALIZER =
            SERIALIZERS.register("applying", ApplyingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ShapingRecipe>> SHAPING =
            TYPES.register("shaping", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "shaping")));

    public static final DeferredHolder<RecipeSerializer<?>, ShapingRecipe.Serializer> SHAPING_SERIALIZER =
            SERIALIZERS.register("shaping", ShapingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DryingRecipe>> DRYING =
            TYPES.register("drying", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "drying")));

    public static final DeferredHolder<RecipeSerializer<?>, DryingRecipe.Serializer> DRYING_SERIALIZER =
            SERIALIZERS.register("drying", DryingRecipe.Serializer::new);

    // What a pit kiln turns a packed load into. Its own type rather than smelting: a
    // kiln takes one batch, is lit once and gives nothing back until it burns out.
    public static final DeferredHolder<RecipeType<?>, RecipeType<FiringRecipe>> FIRING =
            TYPES.register("firing", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "firing")));

    public static final DeferredHolder<RecipeSerializer<?>, FiringRecipe.Serializer> FIRING_SERIALIZER =
            SERIALIZERS.register("firing", FiringRecipe.Serializer::new);

    // What a charcoal pit turns wood into. Apart from firing so that logs cannot be
    // burnt in the open kiln instead, which would make the pit's cover pointless.
    public static final DeferredHolder<RecipeType<?>, RecipeType<CharringRecipe>> CHARRING =
            TYPES.register("charring", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "charring")));

    public static final DeferredHolder<RecipeSerializer<?>, CharringRecipe.Serializer> CHARRING_SERIALIZER =
            SERIALIZERS.register("charring", CharringRecipe.Serializer::new);

    // What a foundry basin melts under a closed lid. A basin recipe, so Create's own
    // heat check applies - and Create already reads a lit campfire as a smouldering
    // blaze burner, which is what a stone age fire amounts to.
    public static final ResourceLocation MELTING_ID =
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "melting");

    public static final DeferredHolder<RecipeType<?>, RecipeType<FoundryRecipe>> MELTING =
            TYPES.register("melting", () -> RecipeType.simple(MELTING_ID));

    /** Declared before the serializer that names it, so the reference resolves. */
    public static final ModRecipeTypeInfo MELTING_INFO =
            new ModRecipeTypeInfo(MELTING_ID, MELTING, () -> ModRecipeTypes.MELTING_SERIALIZER.get());

    public static final DeferredHolder<RecipeSerializer<?>, StandardProcessingRecipe.Serializer<FoundryRecipe>>
            MELTING_SERIALIZER = SERIALIZERS.register("melting",
                    () -> new StandardProcessingRecipe.Serializer<>(
                            params -> new FoundryRecipe(MELTING_INFO, params)));

    // What a tapper draws out of a bored log, and what the pool it collects sets into.
    // Two types rather than one: the first is keyed on a block and yields a fluid, the
    // second is keyed on a fluid and yields an item, and nothing is shared between them
    // but the block that runs both.
    public static final DeferredHolder<RecipeType<?>, RecipeType<TappingRecipe>> TAPPING =
            TYPES.register("tapping", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "tapping")));

    public static final DeferredHolder<RecipeSerializer<?>, TappingRecipe.Serializer> TAPPING_SERIALIZER =
            SERIALIZERS.register("tapping", TappingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CoagulatingRecipe>> COAGULATING =
            TYPES.register("coagulating", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "coagulating")));

    public static final DeferredHolder<RecipeSerializer<?>, CoagulatingRecipe.Serializer> COAGULATING_SERIALIZER =
            SERIALIZERS.register("coagulating", CoagulatingRecipe.Serializer::new);

    // Kept apart from create:milling so the primitive millstone and Create's own
    // grind different things; see PrimitiveMillingRecipe.
    public static final ResourceLocation MILLING_ID =
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "milling");

    public static final DeferredHolder<RecipeType<?>, RecipeType<MillingRecipe>> MILLING =
            TYPES.register("milling", () -> RecipeType.simple(MILLING_ID));

    public static final DeferredHolder<RecipeSerializer<?>, StandardProcessingRecipe.Serializer<MillingRecipe>>
            MILLING_SERIALIZER = SERIALIZERS.register("milling",
                    () -> new StandardProcessingRecipe.Serializer<>(MillingRecipe::new));

    /** What Create's processing recipe base needs to find this type again. */
    public static final ModRecipeTypeInfo MILLING_INFO =
            new ModRecipeTypeInfo(MILLING_ID, MILLING, MILLING_SERIALIZER);

    public static final ResourceLocation SIFTING_ID =
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "sifting");

    public static final DeferredHolder<RecipeType<?>, RecipeType<SiftingRecipe>> SIFTING =
            TYPES.register("sifting", () -> RecipeType.simple(SIFTING_ID));

    // Its own serializer rather than Create's: the sifting recipe carries a field
    // Create's processing format does not have.
    public static final DeferredHolder<RecipeSerializer<?>, SiftingRecipe.Serializer> SIFTING_SERIALIZER =
            SERIALIZERS.register("sifting", SiftingRecipe.Serializer::new);

    /** What Create's processing recipe base needs to find this type again. */
    public static final ModRecipeTypeInfo SIFTING_INFO =
            new ModRecipeTypeInfo(SIFTING_ID, SIFTING, SIFTING_SERIALIZER);

    // Molten metal poured into a mould on a casting table and left to set. Not a
    // processing recipe: it is keyed on a fluid and a mould rather than on inputs, and
    // the table matches it itself.
    public static final DeferredHolder<RecipeType<?>, RecipeType<CastingRecipe>> CASTING =
            TYPES.register("casting", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "casting")));

    public static final DeferredHolder<RecipeSerializer<?>, CastingRecipe.Serializer> CASTING_SERIALIZER =
            SERIALIZERS.register("casting", CastingRecipe.Serializer::new);

    // Two molten metals stirred into a third. Nothing uses it in the stone age - a
    // foundry mixer needs a kinetic network that does not exist yet - but the type has
    // to be here for the mixer to have a list to read.
    public static final ResourceLocation ALLOYING_ID =
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "alloying");

    public static final DeferredHolder<RecipeType<?>, RecipeType<FoundryRecipe>> ALLOYING =
            TYPES.register("alloying", () -> RecipeType.simple(ALLOYING_ID));

    /** Declared before the serializer that names it, so the reference resolves. */
    public static final ModRecipeTypeInfo ALLOYING_INFO =
            new ModRecipeTypeInfo(ALLOYING_ID, ALLOYING, () -> ModRecipeTypes.ALLOYING_SERIALIZER.get());

    public static final DeferredHolder<RecipeSerializer<?>, StandardProcessingRecipe.Serializer<FoundryRecipe>>
            ALLOYING_SERIALIZER = SERIALIZERS.register("alloying",
                    () -> new StandardProcessingRecipe.Serializer<>(
                            params -> new FoundryRecipe(ALLOYING_INFO, params)));

    public static void register(IEventBus modEventBus) {
        TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
    }
}
