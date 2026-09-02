package me.moonscenty.createmoonscentypresents.compat.curios;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.registry.ModItems;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import top.theillusivec4.curios.api.CuriosCapability;

/** Hands each reward its behaviour. The slot it goes in comes from its item tag. */
@EventBusSubscriber(modid = CreateMoonScentyPresents.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCurios {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(CuriosCapability.ITEM,
                (stack, context) -> new GatherersSatchelCurio(stack),
                ModItems.GATHERERS_SATCHEL.get());
    }
}
