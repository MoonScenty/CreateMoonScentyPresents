package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Item handlers for the machines that hold things.
 *
 * <p>Create registers the millstone's against its own block entity type, so a machine
 * built on that block entity gets nothing - which is why the primitive millstone had no
 * way for a hopper, a belt or a dropped item to reach it.
 */
@EventBusSubscriber(modid = CreateMoonScentyPresents.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.MILLSTONE.get(),
                (blockEntity, context) -> blockEntity.capability);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.SIFTER.get(),
                (blockEntity, context) -> blockEntity.capability);
        // The foundry basin holds both, and Create binds neither for us.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.FOUNDRY_BASIN.get(),
                (blockEntity, context) -> blockEntity.getItemCapability());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntityTypes.FOUNDRY_BASIN.get(),
                (blockEntity, context) -> blockEntity.getFluidCapability());
        // What a faucet pours into. Items are handled by hand, so no item handler here.
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntityTypes.CASTING_TABLE.get(),
                (blockEntity, context) -> blockEntity.getFluidTank());
    }
}
