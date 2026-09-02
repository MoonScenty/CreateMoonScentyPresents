package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

/** Text this mod shows in game that does not belong to one item or block. */
public class ModLang {

    /** The ceiling a rotation part will break above, shown under goggles. */
    public static final String SPEED_LIMIT_KEY =
            "gui." + CreateMoonScentyPresents.MODID + ".speed_limit";

    public static void register() {
        CreateMoonScentyPresents.REGISTRATE.addRawLang(SPEED_LIMIT_KEY, "Limit: %s RPM");
    }
}
