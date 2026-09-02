package me.moonscenty.createmoonscentypresents.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeRemovals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * Drops the recipes this mod takes over, before they are parsed. The map handed to
 * apply is a plain HashMap built in prepare, so removing from it is enough - nothing
 * downstream ever sees the entry, so no stale ids are left in JEI or in advancements.
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "apply", at = @At("HEAD"))
    private void createmoonscentypresents$removeTakenOverRecipes(Map<ResourceLocation, JsonElement> recipes,
            ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        recipes.keySet().removeAll(ModRecipeRemovals.REMOVED);
    }
}
