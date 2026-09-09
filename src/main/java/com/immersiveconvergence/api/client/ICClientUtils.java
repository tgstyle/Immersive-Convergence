package com.immersiveconvergence.api.client;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.client.IEClientBridge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class ICClientUtils {
    private static final Map<String, ResourceLocation> RESOURCES = new ConcurrentHashMap<>();
    private static final float[] NO_FADE = {1, 1, 1, 1};

    public static Minecraft mc() { return Minecraft.getMinecraft(); }

    public static Tessellator tes() { return Tessellator.getInstance(); }

    public static FontRenderer font() { return mc().fontRenderer; }

    public static ResourceLocation getResource(String path) { return RESOURCES.computeIfAbsent(path, ResourceLocation::new); }

    public static void bindTexture(String path) { mc().getTextureManager().bindTexture(getResource(path)); }

    public static void bindAtlas() { mc().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE); }

    public static TextureAtlasSprite getRegisterSprite(TextureMap map, ResourceLocation path) {
        TextureAtlasSprite sprite = map.getTextureExtry(path.toString());
        if (sprite == null) {
            map.registerSprite(path);
            sprite = map.getTextureExtry(path.toString());
        }
        return sprite;
    }

    public static void drawTexturedRect(float x, float y, float w, float h, double... uv) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + h, 0).tex(uv[0], uv[3]).endVertex();
        buffer.pos(x + w, y + h, 0).tex(uv[1], uv[3]).endVertex();
        buffer.pos(x + w, y, 0).tex(uv[1], uv[2]).endVertex();
        buffer.pos(x, y, 0).tex(uv[0], uv[2]).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedRect(int x, int y, int w, int h, float picSize, int... uv) {
        drawTexturedRect(x, y, w, h, uv[0] / picSize, uv[1] / picSize, uv[2] / picSize, uv[3] / picSize);
    }

    public static void drawRepeatedSprite(float x, float y, float w, float h, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax) {
        int iterMaxW = (int)(w / iconWidth);
        int iterMaxH = (int)(h / iconHeight);
        float leftoverW = w % iconWidth;
        float leftoverH = h % iconHeight;
        float leftoverWf = leftoverW / (float)iconWidth;
        float leftoverHf = leftoverH / (float)iconHeight;
        float iconUDif = uMax - uMin;
        float iconVDif = vMax - vMin;
        for (int ww = 0; ww < iterMaxW; ww++) {
            for (int hh = 0; hh < iterMaxH; hh++) { drawTexturedRect(x + ww * iconWidth, y + hh * iconHeight, iconWidth, iconHeight, uMin, uMax, vMin, vMax); }
            drawTexturedRect(x + ww * iconWidth, y + iterMaxH * iconHeight, iconWidth, leftoverH, uMin, uMax, vMin, vMin + iconVDif * leftoverHf);
        }
        if (leftoverW > 0) {
            for (int hh = 0; hh < iterMaxH; hh++) { drawTexturedRect(x + iterMaxW * iconWidth, y + hh * iconHeight, leftoverW, iconHeight, uMin, uMin + iconUDif * leftoverWf, vMin, vMax); }
            drawTexturedRect(x + iterMaxW * iconWidth, y + iterMaxH * iconHeight, leftoverW, leftoverH, uMin, uMin + iconUDif * leftoverWf, vMin, vMin + iconVDif * leftoverHf);
        }
    }

    public static void drawGradientRect(int x0, int y0, int x1, int y1, int colour0, int colour1) {
        float a0 = (colour0 >> 24 & 255) / 255.0F;
        float r0 = (colour0 >> 16 & 255) / 255.0F;
        float g0 = (colour0 >> 8 & 255) / 255.0F;
        float b0 = (colour0 & 255) / 255.0F;
        float a1 = (colour1 >> 24 & 255) / 255.0F;
        float r1 = (colour1 >> 16 & 255) / 255.0F;
        float g1 = (colour1 >> 8 & 255) / 255.0F;
        float b1 = (colour1 & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y0, 0).color(r0, g0, b0, a0).endVertex();
        buffer.pos(x0, y0, 0).color(r0, g0, b0, a0).endVertex();
        buffer.pos(x0, y1, 0).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1, y1, 0).color(r1, g1, b1, a1).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawRepeatedFluidSprite(FluidStack fluid, float x, float y, float w, float h) {
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE.toString());
        TextureAtlasSprite sprite = mc().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill(fluid).toString());
        int col = fluid.getFluid().getColor(fluid);
        GlStateManager.color((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f, 1);
        int iW = sprite.getIconWidth();
        int iH = sprite.getIconHeight();
        if (iW > 0 && iH > 0) { drawRepeatedSprite(x, y, w, h, iW, iH, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV()); }
    }

    public static void addFluidTooltip(FluidStack fluid, List<String> tooltip, int tankCapacity) {
        if (ICMods.immersiveEngineering()) { IEClientBridge.addFluidTooltip(fluid, tooltip, tankCapacity); return; }
        tooltip.add(fluid != null && fluid.getFluid() != null ? fluid.getFluid().getRarity(fluid).color + fluid.getLocalizedName() : TextFormatting.GRAY + "Empty");
        String amount = TextFormatting.GRAY.toString() + (fluid != null ? fluid.amount : 0);
        tooltip.add(tankCapacity > 0 ? amount + "/" + tankCapacity + "mB" : amount + "mB");
    }

    public static void handleGuiTank(IFluidTank tank, int x, int y, int w, int h, int oX, int oY, int oW, int oH, int mX, int mY, String originalTexture, ArrayList<String> tooltip) {
        handleGuiTank(tank.getFluid(), tank.getCapacity(), x, y, w, h, oX, oY, oW, oH, mX, mY, originalTexture, tooltip);
    }

    public static void handleGuiTank(FluidStack fluid, int capacity, int x, int y, int w, int h, int oX, int oY, int oW, int oH, int mX, int mY, String originalTexture, ArrayList<String> tooltip) {
        if (tooltip != null) {
            if (mX >= x && mX < x + w && mY >= y && mY < y + h) { addFluidTooltip(fluid, tooltip, capacity); }
            return;
        }
        if (fluid != null && fluid.getFluid() != null) {
            int fluidHeight = (int)(h * (fluid.amount / (float)capacity));
            drawRepeatedFluidSprite(fluid, x, y + h - fluidHeight, w, fluidHeight);
            bindTexture(originalTexture);
            GlStateManager.color(1, 1, 1, 1);
        }
        drawTexturedRect(x + (w - oW) / 2, y + (h - oH) / 2, oW, oH, 256f, oX, oX + oW, oY, oY + oH);
    }

    public static void drawHoveringText(List<String> list, int x, int y, FontRenderer font, int xSize, int ySize) {
        if (list.isEmpty()) { return; }
        boolean uni = font().getUnicodeFlag();
        font().setUnicodeFlag(false);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        int width = 0;
        for (String s : list) {
            int l = font.getStringWidth(s);
            if (l > width) {
                width = l;
            }
        }

        int textX = x + 12;
        int textY = y - 12;
        int height = 8;

        boolean shift = false;
        if (xSize > 0 && textX + width > xSize) { textX -= 28 + width; shift = true; }
        if (ySize > 0 && textY + height + 6 > ySize) { textY = ySize - height - 6; shift = true; }
        if (!shift && mc().currentScreen != null) {
            if (textX + width > mc().currentScreen.width) { textX -= 28 + width; }
            if (textY + height + 6 > mc().currentScreen.height) { textY = mc().currentScreen.height - height - 6; }
        }

        if (list.size() > 1) { height += 2 + (list.size() - 1) * 10; }

        GlStateManager.translate(0, 0, 300);
        int background = -267386864;
        drawGradientRect(textX - 3, textY - 4, textX + width + 3, textY - 3, background, background);
        drawGradientRect(textX - 3, textY + height + 3, textX + width + 3, textY + height + 4, background, background);
        drawGradientRect(textX - 3, textY - 3, textX + width + 3, textY + height + 3, background, background);
        drawGradientRect(textX - 4, textY - 3, textX - 3, textY + height + 3, background, background);
        drawGradientRect(textX + width + 3, textY - 3, textX + width + 4, textY + height + 3, background, background);
        int borderStart = 1347420415;
        int borderEnd = (borderStart & 16711422) >> 1 | borderStart & -16777216;
        drawGradientRect(textX - 3, textY - 3 + 1, textX - 3 + 1, textY + height + 3 - 1, borderStart, borderEnd);
        drawGradientRect(textX + width + 2, textY - 3 + 1, textX + width + 3, textY + height + 3 - 1, borderStart, borderEnd);
        drawGradientRect(textX - 3, textY - 3, textX + width + 3, textY - 3 + 1, borderStart, borderStart);
        drawGradientRect(textX - 3, textY + height + 2, textX + width + 3, textY + height + 3, borderEnd, borderEnd);
        GlStateManager.translate(0, 0, -300);

        for (int i = 0; i < list.size(); i++) {
            font.drawStringWithShadow(list.get(i), textX, textY, -1);
            if (i == 0) { textY += 2; }
            textY += 10;
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        font().setUnicodeFlag(uni);
    }

    public static void renderModelTESRFast(List<BakedQuad> quads, BufferBuilder renderer, World world, BlockPos pos) {
        int brightness = world.getCombinedLight(pos, 0);
        int light1 = (brightness >> 0x10) & 0xFFFF;
        int light2 = brightness & 0xFFFF;
        for (BakedQuad quad : quads) {
            int[] data = quad.getVertexData();
            VertexFormat format = quad.getFormat();
            int size = format.getIntegerSize();
            int uv = format.getUvOffsetById(0) / 4;
            for (int i = 0; i < 4; i++) {
                renderer.pos(Float.intBitsToFloat(data[size * i]), Float.intBitsToFloat(data[size * i + 1]), Float.intBitsToFloat(data[size * i + 2]))
                        .color(255, 255, 255, 255)
                        .tex(Float.intBitsToFloat(data[size * i + uv]), Float.intBitsToFloat(data[size * i + uv + 1]))
                        .lightmap(light1, light2)
                        .endVertex();
            }
        }
    }

    public static void putVertexData(VertexFormat format, UnpackedBakedQuad.Builder builder, Vector3f pos, OBJModel.Normal faceNormal, double u, double v, TextureAtlasSprite sprite, float[] colour, float alpha) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, pos.getX(), pos.getY(), pos.getZ(), 0);
                    break;
                case COLOR:
                    builder.put(e, colour[0], colour[1], colour[2], colour[3] * alpha);
                    break;
                case UV:
                    TextureAtlasSprite used = sprite == null ? mc().getTextureMapBlocks().getMissingSprite() : sprite;
                    builder.put(e, used.getInterpolatedU(u), used.getInterpolatedV(v), 0, 1);
                    break;
                case NORMAL:
                    builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                    break;
                default:
                    builder.put(e);
            }
        }
    }

    public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing, TextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert) {
        return createBakedQuad(format, vertices, facing, sprite, uvs, colour, invert, NO_FADE);
    }

    public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing, TextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert, float[] alpha) {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadOrientation(facing);
        builder.setTexture(sprite);
        OBJModel.Normal faceNormal = new OBJModel.Normal(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
        int vId = invert ? 3 : 0;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[vId > 1 ? 2 : 0], uvs[1], sprite, colour, alpha[vId]);
        vId = invert ? 2 : 1;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[vId > 1 ? 2 : 0], uvs[3], sprite, colour, alpha[vId]);
        vId = invert ? 1 : 2;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[vId > 1 ? 2 : 0], uvs[3], sprite, colour, alpha[vId]);
        vId = invert ? 0 : 3;
        putVertexData(format, builder, vertices[vId], faceNormal, uvs[vId > 1 ? 2 : 0], uvs[1], sprite, colour, alpha[vId]);
        return builder.build();
    }
}
