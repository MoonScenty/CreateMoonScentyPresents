package me.moonscenty.createmoonscentypresents.compat.jei;

import com.simibubi.create.content.processing.recipe.HeatCondition;

import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;

import net.minecraft.network.chat.Component;

/** Shared bits of presentation for the categories in this package. */
class JeiFormat {

    /** Ticks as seconds, without a trailing ".0" on the whole ones. */
    static String seconds(int ticks) {
        float value = ticks / 20f;
        return value == Math.round(value) ? String.valueOf(Math.round(value)) : String.format("%.1f", value);
    }

    /**
     * How to name the fire a foundry recipe asks for.
     *
     * <p>Create's own key comes back without its namespace on the front, so handing it
     * straight to a translatable component prints the key itself. Its wording is also
     * adjectival - "Heated" - which does not sit in the sentence these pages build.
     *
     * <p>So it is mapped onto this pack's own rungs instead, which is what the kiln page
     * and the Jade tooltip already use. A fire gets described the same way everywhere.
     *
     * <p>None is not silence here: a foundry needs a fire even when the recipe names no
     * particular one, so it reads as any fire rather than as no requirement.
     */
    static Component heatName(HeatCondition heat) {
        return Component.translatable(switch (heat) {
            case NONE -> ModRecipes.ANY_FIRE_KEY;
            case HEATED -> HeatLevel.HEATED.getTranslationKey();
            case SUPERHEATED -> HeatLevel.SUPERHEATED.getTranslationKey();
        });
    }
}
