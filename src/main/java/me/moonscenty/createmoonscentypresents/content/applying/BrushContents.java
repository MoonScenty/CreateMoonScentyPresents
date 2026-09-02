package me.moonscenty.createmoonscentypresents.content.applying;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * What an applicator brush is loaded with.
 *
 * <p>One kind of substance at a time, up to that substance's own stack size, so a
 * brush of resin holds sixty four and a brush of something rarer holds as many as the
 * item itself allows. Each application spends one.
 *
 * <p>{@link ItemStack} cannot be a data component value on its own: it inherits
 * identity equality, and NeoForge rejects components that do not implement equals and
 * hashCode. This record supplies both in terms of the stack's contents.
 */
public record BrushContents(ItemStack substance) {

    public static final Codec<BrushContents> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ItemStack.OPTIONAL_CODEC.fieldOf("substance").forGetter(BrushContents::substance))
            .apply(instance, BrushContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BrushContents> STREAM_CODEC =
            StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, BrushContents::substance,
                    BrushContents::new);

    @Override
    public boolean equals(Object other) {
        // isSameItemSameComponents ignores count, so that is compared separately.
        return other instanceof BrushContents that
                && ItemStack.isSameItemSameComponents(that.substance, substance)
                && that.substance.getCount() == substance.getCount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(substance.getItem(), substance.getCount(), substance.getComponents());
    }
}
