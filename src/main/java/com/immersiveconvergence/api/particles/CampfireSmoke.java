package com.immersiveconvergence.api.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public final class CampfireSmoke extends TextureSheetParticle {
    public CampfireSmoke(ClientLevel level, double x, double y, double z, double velX, double velY, double velZ) {
        super(level, x, y, z);
        this.scale(3.0F);
        this.setSize(0.25F, 0.25F);
        this.lifetime = this.random.nextInt(50) + 80;
        this.gravity = 3.0E-6F;
        this.xd = velX;
        this.yd = velY + this.random.nextFloat() / 500.0F;
        this.zd = velZ;
        this.alpha = 0.9F;
        this.hasPhysics = ParticleSettings.particleCollide.getAsBoolean();
    }

    @Override public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || this.alpha <= 0.0F) {
            this.remove();
            return;
        }
        this.xd += this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
        this.zd += this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) { this.alpha -= 0.015F; }
    }

    @Override @Nonnull public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
}
