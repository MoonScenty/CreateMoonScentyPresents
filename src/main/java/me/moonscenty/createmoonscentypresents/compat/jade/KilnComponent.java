package me.moonscenty.createmoonscentypresents.compat.jade;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.content.firing.KilnBlockEntity;
import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

/**
 * What is in a kiln or a charcoal pit and how far along it is.
 *
 * <p>Both are closed, so there is nothing to look at - without this the only way to
 * know whether a load is done is to reach in, which throws away the point of leaving it
 * alone. Progress is asked for from the server rather than synced to every client,
 * since it only matters to whoever is standing in front of it.
 */
public enum KilnComponent implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, JadeLang.KILN);

    /** Jade's bar defaults to black text, which is unreadable over a filled bar. */
    private static final int TEXT_COLOUR = 0xFFFFFFFF;
    /** Embers, so the bar reads as a fire rather than as a generic timer. */
    private static final int BAR_COLOUR = 0xFFB4501E;

    private static final String TICKS = "FiringTicks";
    private static final String DURATION = "FiringDuration";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof KilnBlockEntity<?> kiln))
            return;
        data.putInt(TICKS, kiln.getFiringTicks());
        data.putInt(DURATION, kiln.getFiringDuration());
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof KilnBlockEntity<?> kiln))
            return;

        if (kiln.isEmpty()) {
            tooltip.add(Component.translatable(JadeLang.KILN_EMPTY_KEY).withStyle(ChatFormatting.GRAY));
            return;
        }

        IElementHelper helper = IElementHelper.get();
        ItemStack load = kiln.getLoad();
        if (!load.isEmpty())
            line(tooltip, helper, load, JadeLang.KILN_WAITING_KEY);
        ItemStack fired = kiln.getFired();
        if (!fired.isEmpty())
            line(tooltip, helper, fired, JadeLang.KILN_DONE_KEY);
        if (load.isEmpty())
            return;

        CompoundTag data = accessor.getServerData();
        int duration = data.getInt(DURATION);
        if (duration <= 0) {
            // Nothing the fire can do with what is in there.
            tooltip.add(Component.translatable(JadeLang.KILN_NOTHING_TO_FIRE_KEY)
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        // Say what fire it wants before saying it is stopped: a cold kiln and one that
        // wants a blaze burner look the same on the bar otherwise.
        HeatLevel required = kiln.requiredHeat();
        if (!kiln.availableHeat().isAtLeast(required))
            tooltip.add(Component.translatable(JadeLang.KILN_NEEDS_HEAT_KEY,
                    Component.translatable(required.getTranslationKey())).withStyle(ChatFormatting.GRAY));

        int ticks = Math.min(data.getInt(TICKS), duration);
        float ratio = ticks / (float) duration;
        // Stopped covers both halves of what a station needs: not hot enough, and for a
        // charcoal pit no cover over it either.
        Component text = Component.translatable(
                kiln.isRunning() ? JadeLang.KILN_WORKING_KEY : JadeLang.KILN_STOPPED_KEY,
                Math.round(ratio * 100));
        tooltip.add(helper.progress(ratio, text,
                helper.progressStyle().color(BAR_COLOUR).textColor(TEXT_COLOUR),
                BoxStyle.getNestedBox(), true));
    }

    private static void line(ITooltip tooltip, IElementHelper helper, ItemStack stack, String label) {
        tooltip.add(helper.item(stack));
        tooltip.append(Component.translatable(label, stack.getHoverName(), stack.getCount())
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
