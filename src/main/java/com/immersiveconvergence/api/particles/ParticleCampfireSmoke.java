package com.immersiveconvergence.api.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class ParticleCampfireSmoke extends Particle {

    private final Random random;

    public ParticleCampfireSmoke(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z);
        this.random = world.rand;
        this.particleScale *= 3.0F;
        this.particleMaxAge = this.random.nextInt(50) + 80;
        this.particleGravity = 3.0E-6F;
        this.canCollide = false;
        this.motionX = xSpeed;
        this.motionY = ySpeed + this.random.nextFloat() / 500.0F;
        this.motionZ = zSpeed;
        this.particleAlpha = 0.9F;
        this.setParticleTexture(SmokeSprites.random(this.random));
    }

    @Override public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge || this.particleAlpha <= 0.0F) {
            this.setExpired();
            return;
        }
        this.motionX += this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
        this.motionZ += this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
        this.motionY -= this.particleGravity;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        if (this.particleAge >= this.particleMaxAge - 60 && this.particleAlpha > 0.01F) { this.particleAlpha -= 0.015F; }
    }

    @Override public int getFXLayer() { return 1; }
}
