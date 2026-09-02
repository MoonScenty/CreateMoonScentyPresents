package me.moonscenty.createmoonscentypresents.compat.curios;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.registry.ModItems;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import com.simibubi.create.content.equipment.goggles.GogglesItem;

import top.theillusivec4.curios.api.CuriosCapability;

/** Hands each reward its behaviour. The slot it goes in comes from its item tag. */
@EventBusSubscriber(modid = CreateMoonScentyPresents.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCurios {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(CuriosCapability.ITEM,
                (stack, context) -> new GatherersSatchelCurio(stack),
                ModItems.GATHERERS_SATCHEL.get());
        event.registerItem(CuriosCapability.ITEM,
                (stack, context) -> new ApprenticeGogglesCurio(stack),
                ModItems.APPRENTICE_GOGGLES.get());
    }

    /**
     * Teaches Create that our goggles count as goggles.
     *
     * <p>{@code GogglesItem} keeps a list of ways a player might be wearing a pair and
     * asks all of them, which is the hook an addon is meant to use - so the whole
     * overlay comes for free rather than being drawn again.
     */
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> GogglesItem.addIsWearingPredicate(ApprenticeGogglesCurio::isWorn));
    }
}
