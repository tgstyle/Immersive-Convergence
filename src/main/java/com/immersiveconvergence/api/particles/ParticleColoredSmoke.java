package com.immersiveconvergence.api.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ParticleColoredSmoke extends Particle {
    private static final double FRICTION = 0.98;
    private static final double BUOYANCY = 0.002;
    private static final double SPAWN_JITTER = 0.015;
    private static final double DRIFT = 0.001;
    private static final int FADE_TICKS = 60;
    private static final float ALPHA = 0.75F;
    private final Random random;
    private final double heightScale;
    private final float baseScale;

    public ParticleColoredSmoke(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double heightScale) {
        super(world, x, y, z);
        this.random = world.rand;
        this.heightScale = heightScale;
        this.particleScale *= 3.0F;
        this.baseScale = this.particleScale;
        this.particleScale = 0.0F;
        this.particleMaxAge = (int)(80.0 / (this.random.nextFloat() * 0.5 + 0.5));
        this.canCollide = ParticleSettings.particleCollide.getAsBoolean();
        this.motionX = xSpeed + jitter(SPAWN_JITTER);
        this.motionY = ySpeed + jitter(SPAWN_JITTER);
        this.motionZ = zSpeed + jitter(SPAWN_JITTER);
        this.particleAlpha = ALPHA;
        this.setParticleTexture(SmokeSprites.random(this.random));
    }

    private double jitter(double magnitude) { return (this.random.nextDouble() * 2.0 - 1.0) * magnitude; }

    @Override public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.motionX += jitter(DRIFT);
        this.motionY += jitter(DRIFT) + BUOYANCY * (this.heightScale / 3.0);
        this.motionZ += jitter(DRIFT);
        this.motionX *= FRICTION;
        this.motionY *= FRICTION;
        this.motionZ *= FRICTION;
        this.move(this.motionX, this.motionY, this.motionZ);
        if (this.particleAge >= this.particleMaxAge - FADE_TICKS) { this.particleAlpha = Math.max(0.0F, this.particleAlpha - ALPHA / FADE_TICKS); }
        this.particleScale = this.baseScale * MathHelper.clamp((float)this.particleAge / this.particleMaxAge * 32.0F, 0.0F, 1.0F);
    }

    @Override public int getFXLayer() { return 1; }
}
