package com.immersiveconvergence.client.render.ip;

import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Function;

public class PumpjackSprite extends TextureAtlasSprite {
    public static final String NAME = "immersivepetroleum:models/pumpjack_atlas";
    private static final ResourceLocation SOURCE = new ResourceLocation("immersivepetroleum", "textures/models/pumpjack.png");

    public PumpjackSprite() { super(NAME); }

    @Override public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) { return true; }

    @Override public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        try (IResource resource = manager.getResource(SOURCE)) {
            BufferedImage source = TextureUtil.readBufferedImage(resource.getInputStream());
            int size = Math.max(source.getWidth(), source.getHeight());
            BufferedImage square = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            square.setRGB(0, 0, source.getWidth(), source.getHeight(), source.getRGB(0, 0, source.getWidth(), source.getHeight(), null, 0, source.getWidth()), 0, source.getWidth());
            setIconWidth(size);
            setIconHeight(size);
            int[][] frame = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            frame[0] = square.getRGB(0, 0, size, size, null, 0, size);
            clearFramesTextureData();
            framesTextureData.add(frame);
            return false;
        }
        catch (IOException e) {
            ICLogger.error("Could not read " + SOURCE + " for the pumpjack sprite: " + e);
            return true;
        }
    }
}
