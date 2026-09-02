package me.moonscenty.createmoonscentypresents.compat.jade;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;

/**
 * Jade asserts that every provider it loads has a name for its config screen, so the
 * entries have to be in our lang file whether or not Jade is installed. Kept apart from
 * the plugin itself: nothing here touches a Jade class, so it is safe to load always.
 */
public class JadeLang {

    public static final String TAPPER = "tapper";
    /** One provider for every station that packs a load and waits over a fire. */
    public static final String KILN = "kiln";

    private static final String PREFIX = "jade." + CreateMoonScentyPresents.MODID + ".";

    public static final String KILN_EMPTY_KEY = PREFIX + "kiln.empty";
    public static final String KILN_NOTHING_TO_FIRE_KEY = PREFIX + "kiln.nothing_to_fire";
    public static final String KILN_WORKING_KEY = PREFIX + "kiln.working";
    public static final String KILN_STOPPED_KEY = PREFIX + "kiln.stopped";
    public static final String KILN_WAITING_KEY = PREFIX + "kiln.waiting";
    public static final String KILN_DONE_KEY = PREFIX + "kiln.done";
    public static final String KILN_NEEDS_HEAT_KEY = PREFIX + "kiln.needs_heat";

    public static void register() {
        addProviderLang(TAPPER, "Tapper");
        addProviderLang(KILN, "Kiln");

        addLang(KILN_EMPTY_KEY, "Empty");
        addLang(KILN_NOTHING_TO_FIRE_KEY, "The fire does nothing to this");
        addLang(KILN_WORKING_KEY, "Firing: %s%%");
        addLang(KILN_STOPPED_KEY, "Stopped: %s%%");
        addLang(KILN_WAITING_KEY, "%s x%s waiting");
        addLang(KILN_DONE_KEY, "%s x%s done");
        addLang(KILN_NEEDS_HEAT_KEY, "Needs %s");

        addLang(HeatLevel.WARM.getTranslationKey(), "a fire");
        addLang(HeatLevel.HEATED.getTranslationKey(), "a blaze burner");
        addLang(HeatLevel.SUPERHEATED.getTranslationKey(), "a blaze burner on blaze cake");
    }

    private static void addProviderLang(String provider, String name) {
        addLang("config.jade.plugin_" + CreateMoonScentyPresents.MODID + "." + provider, name);
    }

    private static void addLang(String key, String value) {
        CreateMoonScentyPresents.REGISTRATE.addRawLang(key, value);
    }
}
