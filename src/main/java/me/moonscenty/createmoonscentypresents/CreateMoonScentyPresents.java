package me.moonscenty.createmoonscentypresents;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;

import me.moonscenty.createmoonscentypresents.compat.jade.JadeLang;
import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;
import me.moonscenty.createmoonscentypresents.registry.ModCreativeTabs;
import me.moonscenty.createmoonscentypresents.registry.ModDataComponents;
import me.moonscenty.createmoonscentypresents.registry.ModFluids;
import me.moonscenty.createmoonscentypresents.registry.ModItems;
import me.moonscenty.createmoonscentypresents.registry.ModLang;
import me.moonscenty.createmoonscentypresents.registry.ModRecipeTypes;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateMoonScentyPresents.MODID)
public class CreateMoonScentyPresents {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "createmoonscentypresents";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Create's Registrate subclass. Every item and block goes through this, which
    // also generates their models, lang entries and tags during data generation.
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CreateMoonScentyPresents(IEventBus modEventBus) {
        REGISTRATE.registerEventListeners(modEventBus);

        ModCreativeTabs.register(modEventBus);
        // Registrate defaults to the vanilla search tab, so point it at ours before
        // any entry is declared - the tab is baked into each builder as it is created.
        REGISTRATE.defaultCreativeTab(ModCreativeTabs.MAIN_KEY);

        ModItems.register();
        ModFluids.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModRecipeTypes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModRecipes.register();
        ModLang.register();
        JadeLang.register();
    }
}
