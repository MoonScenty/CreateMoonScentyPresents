package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import me.moonscenty.createmoonscentypresents.content.applying.ApplyingRecipe;
import me.moonscenty.createmoonscentypresents.content.hammering.HammeringRecipe;
import me.moonscenty.createmoonscentypresents.content.shaping.ShapingRecipe;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import me.moonscenty.createmoonscentypresents.content.kinetics.PrimitiveMillingRecipe;
import me.moonscenty.createmoonscentypresents.content.kinetics.PrimitiveSiftingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.CoagulatingRecipe;
import me.moonscenty.createmoonscentypresents.content.tapping.TappingRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
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
    public static final DeferredHolder<RecipeType<?>, RecipeType<PrimitiveMillingRecipe>> PRIMITIVE_MILLING =
            TYPES.register("primitive_milling", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "primitive_milling")));

    public static final DeferredHolder<RecipeSerializer<?>, StandardProcessingRecipe.Serializer<PrimitiveMillingRecipe>>
            PRIMITIVE_MILLING_SERIALIZER = SERIALIZERS.register("primitive_milling",
                    () -> new StandardProcessingRecipe.Serializer<>(PrimitiveMillingRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<PrimitiveSiftingRecipe>> PRIMITIVE_SIFTING =
            TYPES.register("primitive_sifting", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "primitive_sifting")));

    public static final DeferredHolder<RecipeSerializer<?>, StandardProcessingRecipe.Serializer<PrimitiveSiftingRecipe>>
            PRIMITIVE_SIFTING_SERIALIZER = SERIALIZERS.register("primitive_sifting",
                    () -> new StandardProcessingRecipe.Serializer<>(PrimitiveSiftingRecipe::new));

    public static void register(IEventBus modEventBus) {
        TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
    }
}
