package com.immersiveconvergence.api.client;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ICClientUtils {
    public static Minecraft mc() { return ClientUtils.mc(); }

    public static Tessellator tes() { return ClientUtils.tes(); }

    public static void bindTexture(String path) { ClientUtils.bindTexture(path); }

    public static void bindAtlas() { ClientUtils.bindAtlas(); }

    public static TextureAtlasSprite getRegisterSprite(TextureMap map, ResourceLocation path) { return ApiUtils.getRegisterSprite(map, path); }

    public static void handleGuiTank(IFluidTank tank, int x, int y, int w, int h, int oX, int oY, int oW, int oH, int mX, int mY, String originalTexture, ArrayList<String> tooltip) { ClientUtils.handleGuiTank(tank, x, y, w, h, oX, oY, oW, oH, mX, mY, originalTexture, tooltip); }

    public static void drawHoveringText(List<String> list, int x, int y, FontRenderer font, int xSize, int ySize) { ClientUtils.drawHoveringText(list, x, y, font, xSize, ySize); }

    public static void drawGradientRect(int x0, int y0, int x1, int y1, int colour0, int colour1) { ClientUtils.drawGradientRect(x0, y0, x1, y1, colour0, colour1); }

    public static void drawRepeatedFluidSprite(FluidStack fluid, float x, float y, float w, float h) { ClientUtils.drawRepeatedFluidSprite(fluid, x, y, w, h); }

    public static void renderModelTESRFast(List<BakedQuad> quads, BufferBuilder renderer, World world, BlockPos pos) { ClientUtils.renderModelTESRFast(quads, renderer, world, pos); }

    public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing, TextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert) { return ClientUtils.createBakedQuad(format, vertices, facing, sprite, uvs, colour, invert); }
}
