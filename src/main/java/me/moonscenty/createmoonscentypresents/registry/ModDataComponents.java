package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import me.moonscenty.createmoonscentypresents.content.applying.ApplyingItemComponent;
import me.moonscenty.createmoonscentypresents.content.hammering.HammeringItemComponent;
import me.moonscenty.createmoonscentypresents.content.shaping.ShapingItemComponent;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingItemComponent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    private static final DeferredRegister<DataComponentType<?>> TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CreateMoonScentyPresents.MODID);

    /** The stack a saw is part way through cutting; held on the saw while it is in use. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SawingItemComponent>> SAWING =
            TYPES.register("sawing", () -> DataComponentType.<SawingItemComponent>builder()
                    .persistent(SawingItemComponent.CODEC)
                    .networkSynchronized(SawingItemComponent.STREAM_CODEC)
                    .build());

    /** The same, for a substance being rubbed in. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ApplyingItemComponent>> APPLYING =
            TYPES.register("applying", () -> DataComponentType.<ApplyingItemComponent>builder()
                    .persistent(ApplyingItemComponent.CODEC)
                    .networkSynchronized(ApplyingItemComponent.STREAM_CODEC)
                    .build());

    /** The same, for the chisel. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ShapingItemComponent>> SHAPING =
            TYPES.register("shaping", () -> DataComponentType.<ShapingItemComponent>builder()
                    .persistent(ShapingItemComponent.CODEC)
                    .networkSynchronized(ShapingItemComponent.STREAM_CODEC)
                    .build());

    /** The same, for the hammer. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HammeringItemComponent>> HAMMERING =
            TYPES.register("hammering", () -> DataComponentType.<HammeringItemComponent>builder()
                    .persistent(HammeringItemComponent.CODEC)
                    .networkSynchronized(HammeringItemComponent.STREAM_CODEC)
                    .build());

    public static void register(IEventBus modEventBus) {
        TYPES.register(modEventBus);
    }
}
