package me.moonscenty.createmoonscentypresents.content.heat;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * How hot a station is being kept, as a ladder a recipe can name a rung of.
 *
 * <p>Create has the same idea in {@code HeatCondition}, but its lowest real step is a
 * blaze burner, and this pack starts long before one. {@link #WARM} is that missing
 * step: a fire you can build with a flint and a log, hot enough to bake clay and char
 * wood and nothing else.
 *
 * <p>The rungs are ordered, so a station kept hotter than a recipe asks for still runs
 * it - a blaze burner will fire pottery, it is only wasted on it.
 */
public enum HeatLevel implements StringRepresentable {

    /** No fire. */
    NONE,
    /** A campfire, or a blaze burner with nothing in it. Bakes and chars. */
    WARM,
    /** A blaze burner on ordinary fuel. Melts metal. */
    HEATED,
    /** A blaze burner on a blaze cake. */
    SUPERHEATED;

    public static final Codec<HeatLevel> CODEC = StringRepresentable.fromEnum(HeatLevel::values);
    public static final StreamCodec<ByteBuf, HeatLevel> STREAM_CODEC =
            CatnipStreamCodecBuilders.ofEnum(HeatLevel.class);

    /** Whether this fire is enough for something asking for that one. */
    public boolean isAtLeast(HeatLevel required) {
        return ordinal() >= required.ordinal();
    }

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }

    public String getTranslationKey() {
        return "heat." + getSerializedName();
    }
}
