package com.immersiveconvergence.api.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public record SmokeCustomProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
    @Override public Particle createParticle(@Nonnull SimpleParticleType type, @Nonnull ClientLevel level, double x, double y, double z, double velX, double velY, double velZ) {
        return new CustomSmoke(level, x, y, z, velX, velY, velZ, sprites, 7.0F);
    }
}
