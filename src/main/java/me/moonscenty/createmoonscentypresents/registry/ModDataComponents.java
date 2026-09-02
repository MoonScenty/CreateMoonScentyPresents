package me.moonscenty.createmoonscentypresents.registry;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import me.moonscenty.createmoonscentypresents.content.applying.BrushContents;
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

    /**
     * What an applicator brush is loaded with. Unlike the three above this is not
     * progress through one action - it is the brush's contents, and it persists between
     * uses until the last of it has been applied.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BrushContents>> BRUSH_CONTENTS =
            TYPES.register("brush_contents", () -> DataComponentType.<BrushContents>builder()
                    .persistent(BrushContents.CODEC)
                    .networkSynchronized(BrushContents.STREAM_CODEC)
                    .build());

    /**
     * How long the stroke in progress takes, in ticks, read from the recipe when the
     * brush is put to a block. Synced but not persistent: it means nothing once the
     * button is let go, and a half finished stroke should not survive a save.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> APPLYING_TIME =
            TYPES.register("applying_time", () -> DataComponentType.<Integer>builder()
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    /** The same, for the chisel. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ShapingItemComponent>> SHAPING =
            TYPES.register("shaping", () -> DataComponentType.<ShapingItemComponent>builder()
                    .persistent(ShapingItemComponent.CODEC)
                    .networkSynchronized(ShapingItemComponent.STREAM_CODEC)
                    .build());

    /**
     * Whether the satchel is drawing items in. Kept on the stack rather than on the
     * player so each satchel remembers its own setting, and so the tooltip can read it
     * without asking the server.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SATCHEL_ACTIVE =
            TYPES.register("satchel_active", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
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
