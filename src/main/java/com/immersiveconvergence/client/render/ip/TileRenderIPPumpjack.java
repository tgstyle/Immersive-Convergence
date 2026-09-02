package com.immersiveconvergence.client.render.ip;

import blusunrize.immersiveengineering.client.ClientUtils;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

public class TileRenderIPPumpjack extends TileEntitySpecialRenderer<TileEntityPumpjack.TileEntityPumpjackParent> {
    private static final double CRANK_RADIUS = 8.5 / 16.0;

    @Override public void render(TileEntityPumpjack.TileEntityPumpjackParent te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        World world = te == null ? null : te.getWorld();
        BlockPos pos = te == null ? BlockPos.ORIGIN : te.getPos();
        EnumFacing facing = te == null ? EnumFacing.NORTH : te.facing;
        if (world != null && !world.isBlockLoaded(pos, false)) { return; }
        if (IPPumpjackSupport.arm == null) { return; }
        float ticks = te == null ? 0 : 1.5F * (te.activeTicks + (te.wasActive ? partialTicks : 0));
        double armAngle = Math.toRadians(15.0 * Math.sin(ticks / 25.0));
        double swingAngle = Math.PI / 2 + ticks / 25.0;
        double sin = Math.sin(swingAngle), cos = Math.cos(swingAngle);
        double sin2 = Math.sin(armAngle), cos2 = Math.cos(armAngle);
        double crankX = 24.0 / 16.0 - CRANK_RADIUS * sin, crankY = 30.0 / 16.0 + CRANK_RADIUS * cos;
        double beamX = 56.0 / 16.0 - 33.0 / 16.0 * cos2 - 4.0 / 16.0 * sin2, beamY = 48.0 / 16.0 - 33.0 / 16.0 * sin2 + 4.0 / 16.0 * cos2;
        double connectorAngle = Math.PI * 1.5 + Math.atan2(beamY - crankY, beamX - crankX);
        double headX = 34.0 / 16.0 * cos2 + 13.0 / 16.0 * sin2, headY = 34.0 / 16.0 * sin2 - 13.0 / 16.0 * cos2;
        double wellDx = 32.0 / 16.0 - headX, wellDy = -32.0 / 16.0 - headY;
        double wellAngle = Math.PI * 1.5 + Math.atan2(wellDy, wellDx);
        IBakedModel well = Math.sqrt(wellDx * wellDx + wellDy * wellDy) <= 1.0 ? IPPumpjackSupport.wellShort : IPPumpjackSupport.wellLong;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.rotate(yaw(facing), 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        if (world != null) { RenderHelper.disableStandardItemLighting(); }
        GlStateManager.disableCull();
        if (Minecraft.isAmbientOcclusionEnabled()) { GlStateManager.shadeModel(GL11.GL_SMOOTH); }
        else { GlStateManager.shadeModel(GL11.GL_FLAT); }
        ClientUtils.bindAtlas();
        if (world == null) { draw(IPPumpjackSupport.body, 0, 0, 0, 0, null, pos); }
        part(IPPumpjackSupport.arm, 56.0, 48.0, 24.0, armAngle, world, pos);
        part(IPPumpjackSupport.swing, 24.0, 30.0, 30.0, swingAngle, world, pos);
        part(IPPumpjackSupport.connector, crankX * 16.0, crankY * 16.0, 26.0, connectorAngle, world, pos);
        part(well, 56.0 + headX * 16.0, 48.0 + headY * 16.0, 24.0, wellAngle, world, pos);
        GlStateManager.enableCull();
        if (world != null) { RenderHelper.enableStandardItemLighting(); }
        GlStateManager.popMatrix();
    }

    private static void part(IBakedModel model, double pivotX, double pivotY, double pivotZ, double angle, World world, BlockPos pos) { draw(model, pivotZ / 16.0 - 1.0, pivotY / 16.0 - 1.0, 2.0 - pivotX / 16.0, angle, world, pos); }

    private static void draw(IBakedModel model, double x, double y, double z, double angle, World world, BlockPos pos) {
        if (model == null) { return; }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate((float)Math.toDegrees(angle), 1, 0, 0);
        if (world == null) {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            for (BakedQuad quad : model.getQuads(null, null, 0L)) { LightUtil.renderQuadColor(buffer, quad, -1); }
        }
        else {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            ClientUtils.renderModelTESRFancy(model.getQuads(null, null, 0L), buffer, world, pos, false);
        }
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    private static float yaw(EnumFacing facing) {
        switch (facing) {
            case SOUTH: return 180;
            case WEST: return 90;
            case EAST: return -90;
            default: return 0;
        }
    }
}
