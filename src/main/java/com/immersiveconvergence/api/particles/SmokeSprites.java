package com.immersiveconvergence.api.particles;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
public final class SmokeSprites {
    private static final int SPRITE_COUNT = 12;
    private static final ResourceLocation[] SPRITE_NAMES = new ResourceLocation[SPRITE_COUNT];

    static {
        for (int i = 0; i < SPRITE_COUNT; i++) { SPRITE_NAMES[i] = new ResourceLocation(ImmersiveConvergence.MODID, "particle/big_smoke_" + i); }
    }

    private SmokeSprites() {}

    @SubscribeEvent public static void onTextureStitch(TextureStitchEvent.Pre event) {
        for (ResourceLocation name : SPRITE_NAMES) { event.getMap().registerSprite(name); }
    }

    public static TextureAtlasSprite random(Random random) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(SPRITE_NAMES[random.nextInt(SPRITE_COUNT)].toString());
    }
}
