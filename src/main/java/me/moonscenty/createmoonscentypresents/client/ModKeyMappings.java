package me.moonscenty.createmoonscentypresents.client;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.network.ToggleSatchelPayload;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.network.PacketDistributor;

import org.lwjgl.glfw.GLFW;

/** The mod's keys. Client only, and registered on the mod bus. */
@EventBusSubscriber(modid = CreateMoonScentyPresents.MODID, bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public class ModKeyMappings {

    /** Alt + N. In game only, so it does not fire while a screen is open. */
    public static final KeyMapping TOGGLE_SATCHEL = new KeyMapping(ToggleSatchelPayload.KEY_NAME,
            KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N,
            ToggleSatchelPayload.KEY_CATEGORY);

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_SATCHEL);
    }

    /** Consume every press: holding the key down should not toggle repeatedly. */
    @EventBusSubscriber(modid = CreateMoonScentyPresents.MODID, value = Dist.CLIENT)
    public static class Input {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            while (TOGGLE_SATCHEL.consumeClick())
                PacketDistributor.sendToServer(new ToggleSatchelPayload());
        }
    }
}
