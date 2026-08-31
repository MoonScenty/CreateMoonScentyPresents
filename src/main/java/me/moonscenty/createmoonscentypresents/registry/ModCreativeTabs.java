package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    private static final String MAIN_NAME = "main";

    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateMoonScentyPresents.MODID);

    // Registrate needs the key, not the holder, to route entries into this tab.
    public static final ResourceKey<CreativeModeTab> MAIN_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, MAIN_NAME));

    public static final String MAIN_LANG_KEY = "itemGroup." + CreateMoonScentyPresents.MODID + "." + MAIN_NAME;

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(MAIN_NAME,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(MAIN_LANG_KEY))
                    .icon(() -> ModItems.PLANT_FIBER.asStack())
                    .build());

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
        CreateMoonScentyPresents.REGISTRATE.addRawLang(MAIN_LANG_KEY, "Create: MoonScenty Presents");
    }
}
