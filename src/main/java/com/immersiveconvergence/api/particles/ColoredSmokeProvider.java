package com.immersiveconvergence.api.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused") public record ColoredSmokeProvider(SpriteSet sprites) implements ParticleProvider<ColoredSmoke> {
    @Override public Particle createParticle(ColoredSmoke data, @NotNull ClientLevel level, double x, double y, double z, double velX, double velY, double velZ) {
        return new ColoredSmokeParticle(level, x, y, z, velX, velY, velZ, data.color(), data.collideHorizontal(), data.collideVertical(), this.sprites);
    }
}
