package me.moonscenty.createmoonscentypresents.content.charring;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import me.moonscenty.createmoonscentypresents.content.firing.KilnBlockEntity;
import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;
import me.moonscenty.createmoonscentypresents.content.heat.HeatSources;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The charcoal pit.
 *
 * <p>Charcoal is wood that burned without enough air to burn away, and the pit is what
 * keeps the air off - it is a sealed brick box with one grate in the front, so nothing
 * has to be stacked on top of it to smother the load.
 *
 * <p>Its heat comes from inside rather than from below. The kiln reads whatever fire is
 * under it every tick; this reads whether it has been struck, which is why nothing needs
 * building underneath one either. A pit is a station that stands on its own, and
 * whatever is set on top of it is being cooked rather than holding it shut.
 */
public class CharcoalPitBlockEntity extends KilnBlockEntity<CharringRecipe> {

    public CharcoalPitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected RecipeType<CharringRecipe> recipeType() {
        return ModRecipeTypes.CHARRING.get();
    }

    /** Set by a bellows beside it; the game time the air stops making a difference. */
    private long blownUntil;

    /**
     * Its own fire, not one underneath.
     *
     * <p>Read straight off the block, because the block is where the fire is written -
     * which is the same place a foundry basin standing on this pit reads it from.
     */
    @Override
    public HeatLevel availableHeat() {
        return HeatSources.of(getBlockState());
    }

    public boolean isBlown() {
        return level != null && level.getGameTime() < blownUntil;
    }

    /**
     * Air on the fire: a rung up for this many ticks, and pumping again pushes the end
     * back out.
     *
     * <p>Written into the blockstate rather than kept here, because the thing that has
     * to see it is whatever is standing on top - and a basin only ever asks the block.
     *
     * <p>A cold pit stays cold. Air is not a light: the rung this writes is one <em>up</em>
     * from smouldering, so blowing on a pit that was never struck would otherwise hand
     * out a fire for free and make the flint and steel optional.
     */
    public void blow(int ticks) {
        if (level == null || !CharcoalPitBlock.isLit(getBlockState()))
            return;
        blownUntil = level.getGameTime() + ticks;
        setHeat(BlazeBurnerBlock.HeatLevel.KINDLED);
    }

    /**
     * Out when there is nothing left to burn, and back down a rung when the pumping
     * stops.
     *
     * <p>Going out is checked here rather than at the end of a piece so that emptying a
     * burning pit by hand puts it out too - there is only one rule, and it is that the
     * fire is the load. Every batch is therefore struck fresh.
     */
    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide)
            return;

        if (!CharcoalPitBlock.isLit(getBlockState()))
            return;
        if (getLoad().isEmpty())
            setHeat(BlazeBurnerBlock.HeatLevel.NONE);
        else if (!isBlown())
            setHeat(BlazeBurnerBlock.HeatLevel.SMOULDERING);
    }

    /** Writes the rung onto the block, and only when it is actually changing. */
    private void setHeat(BlazeBurnerBlock.HeatLevel heat) {
        BlockState state = getBlockState();
        if (state.getValue(CharcoalPitBlock.HEAT_LEVEL) == heat)
            return;
        level.setBlock(worldPosition, state.setValue(CharcoalPitBlock.HEAT_LEVEL, heat), Block.UPDATE_ALL);
    }

    /** Smothered wood, not an open fire. */
    @Override
    protected ParticleOptions smoke() {
        return ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
    }
}
