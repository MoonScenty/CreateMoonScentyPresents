package me.moonscenty.createmoonscentypresents.content.milling;

import me.moonscenty.createmoonscentypresents.content.kinetics.GrinderBlockEntity;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** The primitive millstone: the shared working loop over this mod's milling list. */
public class MillstoneBlockEntity extends GrinderBlockEntity<MillingRecipe> {

    public MillstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected RecipeType<MillingRecipe> recipeType() {
        return ModRecipeTypes.MILLING.get();
    }
}
