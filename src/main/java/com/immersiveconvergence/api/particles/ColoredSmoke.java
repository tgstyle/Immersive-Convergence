package com.immersiveconvergence.api.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

@SuppressWarnings({"deprecation", "unused", "RedundantSuppression"}) public record ColoredSmoke(Vector3f color, boolean collideHorizontal,
                           boolean collideVertical) implements ParticleOptions {
    public static final Codec<ColoredSmoke> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.x()),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.y()),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.z()),
            Codec.BOOL.optionalFieldOf("collide_horizontal", false).forGetter(d -> d.collideHorizontal),
            Codec.BOOL.optionalFieldOf("collide_vertical", false).forGetter(d -> d.collideVertical)
    ).apply(inst, ColoredSmoke::new));

    public static final Deserializer<ColoredSmoke> DESERIALIZER = new Deserializer<>() {
        @Nonnull
        public ColoredSmoke fromCommand(@Nonnull ParticleType<ColoredSmoke> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            boolean collideHorizontal = false;
            boolean collideVertical = false;
            if (reader.canRead()) {
                reader.expect(' ');
                collideHorizontal = reader.readBoolean();
                if (reader.canRead()) {
                    reader.expect(' ');
                    collideVertical = reader.readBoolean();
                }
            }
            return new ColoredSmoke(r, g, b, collideHorizontal, collideVertical);
        }

        @Nonnull
        public ColoredSmoke fromNetwork(@Nonnull ParticleType<ColoredSmoke> type, FriendlyByteBuf buf) {
            return new ColoredSmoke(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean());
        }
    };

    public static Supplier<ParticleType<?>> typeSupplier;

    public ColoredSmoke(float r, float g, float b) {
        this(r, g, b, false, false);
    }

    public ColoredSmoke(float r, float g, float b, boolean collideHorizontal, boolean collideVertical) {
        this(new Vector3f(r, g, b), collideHorizontal, collideVertical);
    }

    @Override
    @Nonnull
    public ParticleType<?> getType() {
        return typeSupplier.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(color.x());
        buf.writeFloat(color.y());
        buf.writeFloat(color.z());
        buf.writeBoolean(collideHorizontal);
        buf.writeBoolean(collideVertical);
    }

    @Override
    @Nonnull
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %b %b", Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this.getType())), this.color.x(), this.color.y(), this.color.z(), this.collideHorizontal, this.collideVertical);
    }
}
