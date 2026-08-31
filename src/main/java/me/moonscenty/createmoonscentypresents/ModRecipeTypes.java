package me.moonscenty.createmoonscentypresents;

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

    public static void register(IEventBus modEventBus) {
        TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
    }
}
