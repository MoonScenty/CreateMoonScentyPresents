package me.moonscenty.createmoonscentypresents.content.sifting;

import me.moonscenty.createmoonscentypresents.content.kinetics.GrinderBlockEntity;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The primitive sifter.
 *
 * <p>The one thing it adds to the shared working loop is that it cares where it is
 * standing: a recipe may ask for water, and only the block itself knows whether it has
 * any.
 */
public class SifterBlockEntity extends GrinderBlockEntity<SiftingRecipe> {

    public SifterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected RecipeType<SiftingRecipe> recipeType() {
        return ModRecipeTypes.SIFTING.get();
    }

    @Override
    protected boolean accepts(SiftingRecipe recipe) {
        return recipe.matchesState(getBlockState());
    }
}
