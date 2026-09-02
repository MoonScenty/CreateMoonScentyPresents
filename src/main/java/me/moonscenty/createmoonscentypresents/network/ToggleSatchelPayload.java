package me.moonscenty.createmoonscentypresents.network;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.compat.curios.EquippedCurios;
import me.moonscenty.createmoonscentypresents.registry.ModDataComponents;
import me.moonscenty.createmoonscentypresents.registry.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent when the toggle key is pressed. Carries nothing: the server already knows who
 * asked, and the satchel it applies to is whichever one that player is wearing. The
 * stack lives on the server, so the client cannot flip the flag itself.
 */
public record ToggleSatchelPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleSatchelPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    CreateMoonScentyPresents.MODID, "toggle_satchel"));

    public static final StreamCodec<Object, ToggleSatchelPayload> STREAM_CODEC =
            StreamCodec.unit(new ToggleSatchelPayload());

    /**
     * The names this feature shows the player. They live here rather than on the key
     * mapping because that class is client only, and the lang file is generated on the
     * common side - naming them there would load a client class on a server.
     */
    public static final String KEY_CATEGORY = "key.categories." + CreateMoonScentyPresents.MODID;
    public static final String KEY_NAME = "key." + CreateMoonScentyPresents.MODID + ".toggle_satchel";

    /** Shown on the action bar so the state is visible without opening anything. */
    public static final String ON_KEY = "message." + CreateMoonScentyPresents.MODID + ".satchel.on";
    public static final String OFF_KEY = "message." + CreateMoonScentyPresents.MODID + ".satchel.off";

    @Override
    public CustomPacketPayload.Type<ToggleSatchelPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleSatchelPayload payload, IPayloadContext context) {
        Player player = context.player();
        ItemStack satchel = EquippedCurios.find(player, ModItems.GATHERERS_SATCHEL.get());
        if (satchel.isEmpty())
            return;

        boolean active = !satchel.getOrDefault(ModDataComponents.SATCHEL_ACTIVE.get(), true);
        satchel.set(ModDataComponents.SATCHEL_ACTIVE.get(), active);
        player.displayClientMessage(Component.translatable(active ? ON_KEY : OFF_KEY), true);
    }
}
