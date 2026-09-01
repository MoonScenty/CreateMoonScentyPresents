package me.moonscenty.createmoonscentypresents.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;

import me.moonscenty.createmoonscentypresents.content.kinetics.PrimitiveGrinder;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

/**
 * Sends the primitive millstone to this mod's recipe list instead of Create's.
 *
 * <p>{@code AllRecipeTypes.MILLING} is named directly in three places, and the field
 * holding the result is typed as Create's {@code MillingRecipe} - so the answer has to
 * be one of those, which is why {@code PrimitiveMillingRecipe} extends it. Only the
 * lookup is replaced; the grinding itself is Create's.
 */
@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin {

    @Redirect(method = {"tick", "process", "canProcess"},
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/AllRecipeTypes;find("
                            + "Lnet/minecraft/world/item/crafting/RecipeInput;"
                            + "Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>>
            createmoonscentypresents$findPrimitiveRecipe(AllRecipeTypes milling, I input, Level level) {
        if (!((Object) this instanceof PrimitiveGrinder grinder))
            return milling.find(input, level);
        return level.getRecipeManager().getRecipeFor(grinder.primitiveRecipeType(), input, level)
                .map(holder -> (RecipeHolder<R>) (RecipeHolder<?>) holder);
    }
}
