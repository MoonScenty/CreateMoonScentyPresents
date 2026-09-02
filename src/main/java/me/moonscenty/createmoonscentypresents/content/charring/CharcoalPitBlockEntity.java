package me.moonscenty.createmoonscentypresents.content.charring;

import me.moonscenty.createmoonscentypresents.content.firing.KilnBlockEntity;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The charcoal pit.
 *
 * <p>Charcoal is wood that burned without enough air to burn away, so this asks for one
 * thing the pit kiln does not: something solid sitting on top of it. Uncovered it is
 * just a fire, and a fire leaves ash.
 */
public class CharcoalPitBlockEntity extends KilnBlockEntity<CharringRecipe> {

    public CharcoalPitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected RecipeType<CharringRecipe> recipeType() {
        return ModRecipeTypes.CHARRING.get();
    }

    @Override
    protected boolean isAssembled() {
        return CharcoalPitBlock.isCovered(level, worldPosition);
    }

    /** Smothered wood, not an open fire. */
    @Override
    protected ParticleOptions smoke() {
        return ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
    }
}
