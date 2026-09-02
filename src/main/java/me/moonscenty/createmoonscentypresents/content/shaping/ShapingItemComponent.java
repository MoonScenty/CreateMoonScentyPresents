package me.moonscenty.createmoonscentypresents.content.shaping;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * Wrapper for the stack a chisel is working on.
 * <p>
 * {@link ItemStack} cannot be a data component value on its own: it inherits identity
 * equality, and NeoForge rejects components that do not implement equals and hashCode.
 * This record supplies both in terms of the stack's contents.
 */
public record ShapingItemComponent(ItemStack item) {

    public static final Codec<ShapingItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(ShapingItemComponent::item))
            .apply(instance, ShapingItemComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapingItemComponent> STREAM_CODEC =
            StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, ShapingItemComponent::item,
                    ShapingItemComponent::new);

    @Override
    public boolean equals(Object other) {
        // isSameItemSameComponents ignores count, so that is compared separately.
        return other instanceof ShapingItemComponent that
                && ItemStack.isSameItemSameComponents(that.item, item)
                && that.item.getCount() == item.getCount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(item.getItem(), item.getCount(), item.getComponents());
    }
}
