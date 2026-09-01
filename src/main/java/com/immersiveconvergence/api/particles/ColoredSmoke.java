package com.immersiveconvergence.api.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record ColoredSmoke(Vector3f color, boolean collideHorizontal,
                           boolean collideVertical) implements ParticleOptions {
    public static final MapCodec<ColoredSmoke> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.x()),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.y()),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.z()),
            Codec.BOOL.optionalFieldOf("collide_horizontal", false).forGetter(d -> d.collideHorizontal),
            Codec.BOOL.optionalFieldOf("collide_vertical", false).forGetter(d -> d.collideVertical)
    ).apply(inst, ColoredSmoke::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ColoredSmoke> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, d -> d.color.x(),
            ByteBufCodecs.FLOAT, d -> d.color.y(),
            ByteBufCodecs.FLOAT, d -> d.color.z(),
            ByteBufCodecs.BOOL, d -> d.collideHorizontal,
            ByteBufCodecs.BOOL, d -> d.collideVertical,
            ColoredSmoke::new
    );

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
}
