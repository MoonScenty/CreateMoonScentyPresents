package me.moonscenty.createmoonscentypresents.compat.jade;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

/**
 * Jade asserts that every provider it loads has a name for its config screen, so the
 * entries have to be in our lang file whether or not Jade is installed. Kept apart from
 * the plugin itself: nothing here touches a Jade class, so it is safe to load always.
 */
public class JadeLang {

    public static final String TAPPER = "tapper";
    public static final String PIT_KILN = "pit_kiln";

    private static final String PREFIX = "jade." + CreateMoonScentyPresents.MODID + ".";

    public static final String KILN_EMPTY_KEY = PREFIX + "kiln.empty";
    public static final String KILN_NOTHING_TO_FIRE_KEY = PREFIX + "kiln.nothing_to_fire";
    public static final String KILN_FIRING_KEY = PREFIX + "kiln.firing";
    public static final String KILN_COLD_KEY = PREFIX + "kiln.cold";
    public static final String KILN_WAITING_KEY = PREFIX + "kiln.waiting";
    public static final String KILN_DONE_KEY = PREFIX + "kiln.done";

    public static void register() {
        addProviderLang(TAPPER, "Tapper");
        addProviderLang(PIT_KILN, "Pit Kiln");

        addLang(KILN_EMPTY_KEY, "Empty");
        addLang(KILN_NOTHING_TO_FIRE_KEY, "The fire does nothing to this");
        addLang(KILN_FIRING_KEY, "Firing: %s%%");
        addLang(KILN_COLD_KEY, "No fire below: %s%%");
        addLang(KILN_WAITING_KEY, "%s x%s waiting");
        addLang(KILN_DONE_KEY, "%s x%s done");
    }

    private static void addProviderLang(String provider, String name) {
        addLang("config.jade.plugin_" + CreateMoonScentyPresents.MODID + "." + provider, name);
    }

    private static void addLang(String key, String value) {
        CreateMoonScentyPresents.REGISTRATE.addRawLang(key, value);
    }
}
