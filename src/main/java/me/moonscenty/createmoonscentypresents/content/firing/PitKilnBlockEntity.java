package me.moonscenty.createmoonscentypresents.content.firing;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** The pit kiln: the shared waiting loop over this mod's firing list. */
public class PitKilnBlockEntity extends KilnBlockEntity<FiringRecipe> {

    public PitKilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected RecipeType<FiringRecipe> recipeType() {
        return ModRecipeTypes.FIRING.get();
    }
}
