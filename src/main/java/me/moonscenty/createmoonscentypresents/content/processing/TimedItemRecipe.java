package me.moonscenty.createmoonscentypresents.content.processing;

import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * One item left in a station for a while, becoming one other item.
 *
 * <p>Several of this mod's stations do nothing but wait - a rack, a kiln, a charcoal
 * pit. They are separate recipe types because each is a different thing to build and a
 * different page in JEI, but the shape of what they read is the same, and this is what
 * lets one block entity serve more than one of them.
 */
public interface TimedItemRecipe {

    Ingredient input();

    ItemStack result();

    /** How long one piece takes, in ticks. */
    int processingTime();

    /** The fire this needs. Stations that only wait out time need none. */
    default HeatLevel heat() {
        return HeatLevel.NONE;
    }
}
