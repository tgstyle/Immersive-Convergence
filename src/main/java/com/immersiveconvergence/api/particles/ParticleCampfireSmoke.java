package com.immersiveconvergence.api.particles;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
public class ParticleCampfireSmoke extends Particle {

    private static final int SPRITE_COUNT = 12;
    private static final ResourceLocation[] SPRITE_NAMES = new ResourceLocation[SPRITE_COUNT];
    private final Random random;

    static {
        for (int i = 0; i < SPRITE_COUNT; i++) { SPRITE_NAMES[i] = new ResourceLocation(ImmersiveConvergence.MODID, "particle/big_smoke_" + i); }
    }

    @SubscribeEvent public static void onTextureStitch(TextureStitchEvent.Pre event) {
        for (ResourceLocation name : SPRITE_NAMES) { event.getMap().registerSprite(name); }
    }

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
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite sprite = map.getAtlasSprite(SPRITE_NAMES[this.random.nextInt(SPRITE_COUNT)].toString());
        this.setParticleTexture(sprite);
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
