package me.moonscenty.createmoonscentypresents.content.foundry;

import java.util.Optional;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Anything that sits on a foundry basin and drives it - a lid to melt, later a mixer to
 * alloy.
 *
 * <p>Create's own base assumes the basin under it is Create's; this narrows it to ours,
 * which is what lets the extra tanks and the wider input count be reached.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public abstract class FoundryBasinOperatingBlockEntity extends BasinOperatingBlockEntity {

    protected FoundryBasinOperatingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void applyBasinRecipe() {
        if (currentRecipe == null)
            return;

        Optional<BasinBlockEntity> optional = getBasin();
        if (optional.isEmpty() || !(optional.get() instanceof FoundryBasinBlockEntity basin))
            return;

        boolean wasEmpty = basin.canContinueProcessing();
        if (!FoundryRecipe.apply(basin, currentRecipe))
            return;
        getProcessedRecipeTrigger().ifPresent(this::award);
        basin.inputTank.sendDataImmediately();

        // Straight into the next one if there is still enough in the basin for it.
        if (wasEmpty && matchBasinRecipe(currentRecipe)) {
            continueWithPreviousRecipe();
            sendData();
        }

        basin.notifyChangeOfContents();
    }

    /**
     * Create's own lowest heat condition, {@code none}, passes with no fire at all -
     * which is right for a basin being stirred but not for a foundry. A recipe that
     * names no particular fire still needs one, so the floor is put back here; naming
     * {@code heated} on top of that is what asks for a blaze burner.
     */
    @Override
    protected <I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe) {
        if (recipe == null)
            return false;
        return getBasin()
                .filter(basin -> !(basin instanceof FoundryBasinBlockEntity foundry)
                        || foundry.heatLevel() != BlazeBurnerBlock.HeatLevel.NONE)
                .filter(basin -> BasinRecipe.match(basin, recipe))
                .isPresent();
    }
}
