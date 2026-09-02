package me.moonscenty.createmoonscentypresents.compat.jei;

/** Shared bits of presentation for the categories in this package. */
class JeiFormat {

    /** Ticks as seconds, without a trailing ".0" on the whole ones. */
    static String seconds(int ticks) {
        float value = ticks / 20f;
        return value == Math.round(value) ? String.valueOf(Math.round(value)) : String.format("%.1f", value);
    }
}
