package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import me.moonscenty.createmoonscentypresents.content.kinetics.PrimitiveMillingRecipe;
import me.moonscenty.createmoonscentypresents.content.kinetics.PrimitiveSiftingRecipe;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;

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

    public static final DeferredHolder<RecipeType<?>, RecipeType<DryingRecipe>> DRYING =
            TYPES.register("drying", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            CreateMoonScentyPresents.MODID, "drying")));

    public static final DeferredHolder<RecipeSerializer<?>, DryingRecipe.Serializer> DRYING_SERIALIZER =
            SERIALIZERS.register("drying", DryingRecipe.Serializer::new);

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
