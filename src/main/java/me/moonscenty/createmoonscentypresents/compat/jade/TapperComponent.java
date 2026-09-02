package me.moonscenty.createmoonscentypresents.compat.jade;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.content.tapping.TapperBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.view.FluidView;

/**
 * Shows what a tapper is holding: the sap in its tank and the lump that has set.
 * <p>
 * Reads the block entity directly - both fields are written on the client packet, so
 * no server data provider is needed. The tank is drawn the way Jade draws any other
 * fluid handler, so it reads like Create's own tanks.
 */
public enum TapperComponent implements IBlockComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, JadeLang.TAPPER);

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof TapperBlockEntity tapper))
            return;

        IElementHelper helper = IElementHelper.get();
        appendTank(tooltip, helper, tapper.getTank());

        ItemStack output = tapper.getOutput();
        if (!output.isEmpty()) {
            tooltip.add(helper.item(output));
            tooltip.append(output.getHoverName());
        }
    }

    /** Mirrors Jade's own fluid storage line - a filled bar with the fluid as its overlay. */
    private static void appendTank(ITooltip tooltip, IElementHelper helper, FluidStack tank) {
        JadeFluidObject fluid = tank.isEmpty() ? JadeFluidObject.empty()
                : JadeFluidObject.of(tank.getFluid(), tank.getAmount(), tank.getComponentsPatch());
        FluidView view = FluidView.readDefault(
                FluidView.writeDefault(fluid, TapperBlockEntity.TANK_CAPACITY));

        Component text = view.overrideText != null ? view.overrideText
                : Component.translatable("jade.fluid", view.fluidName,
                        Component.translatable("jade.fluid.with_capacity", view.current, view.max));
        tooltip.add(helper.progress(view.ratio, text, helper.progressStyle().overlay(view.overlay),
                BoxStyle.getNestedBox(), true));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
