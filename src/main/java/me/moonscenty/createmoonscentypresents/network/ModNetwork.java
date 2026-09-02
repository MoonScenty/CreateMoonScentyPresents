package me.moonscenty.createmoonscentypresents.network;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Every packet this mod sends. There is one. */
@EventBusSubscriber(modid = CreateMoonScentyPresents.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {

    private static final String VERSION = "1";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToServer(ToggleSatchelPayload.TYPE, ToggleSatchelPayload.STREAM_CODEC,
                ToggleSatchelPayload::handle);
    }
}
