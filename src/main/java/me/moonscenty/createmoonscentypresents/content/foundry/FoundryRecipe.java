package me.moonscenty.createmoonscentypresents.content.foundry;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

/**
 * What a foundry basin can be asked to do.
 *
 * <p>A basin recipe, so Create's own matching, heat check and consumption all apply
 * unchanged - including reading a lit campfire as a smouldering blaze burner, which is
 * how a stone age fire drives one of these at all. Only the room is widened: three
 * things in and four fluids, where Create allows two.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public class FoundryRecipe extends BasinRecipe {

    public FoundryRecipe(IRecipeTypeInfo type, ProcessingRecipeParams params) {
        super(type, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 3;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 4;
    }
}
