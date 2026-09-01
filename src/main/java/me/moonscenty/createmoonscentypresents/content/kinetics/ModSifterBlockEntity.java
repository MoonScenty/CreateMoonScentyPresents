package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;

import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** The primitive sifter, with the age's speed limit applied. */
public class ModSifterBlockEntity extends MillstoneBlockEntity implements PrimitiveGrinder {

    public ModSifterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public RecipeType<? extends MillingRecipe> primitiveRecipeType() {
        return ModRecipeTypes.PRIMITIVE_SIFTING.get();
    }

    @Override
    public void tick() {
        super.tick();
        ModKineticLimits.enforce(this);
    }
}
