package me.moonscenty.createmoonscentypresents.compat.jade;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

/**
 * Jade asserts that every provider it loads has a name for its config screen, so the
 * entries have to be in our lang file whether or not Jade is installed. Kept apart from
 * the plugin itself: nothing here touches a Jade class, so it is safe to load always.
 */
public class JadeLang {

    public static final String TAPPER = "tapper";

    public static void register() {
        addProviderLang(TAPPER, "Tapper");
    }

    private static void addProviderLang(String provider, String name) {
        CreateMoonScentyPresents.REGISTRATE.addRawLang(
                "config.jade.plugin_" + CreateMoonScentyPresents.MODID + "." + provider, name);
    }
}
