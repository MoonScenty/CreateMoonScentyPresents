package me.moonscenty.createmoonscentypresents;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModPartialModels;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateMoonScentyPresents.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CreateMoonScentyPresents.MODID, value = Dist.CLIENT)
public class CreateMoonScentyPresentsClient {
    public CreateMoonScentyPresentsClient() {
        // Partial models have to exist before the model bake, which happens well
        // before client setup - so this runs during mod construction.
        ModPartialModels.init();
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Client-only registration (renderers, screens, Ponder scenes) goes here.
    }
}
