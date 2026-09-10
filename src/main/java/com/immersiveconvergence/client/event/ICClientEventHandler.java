package com.immersiveconvergence.client.event;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IBlockOverlayText;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.ISelectionBounds;
import com.immersiveconvergence.api.energy.IICFluxAcceptor;
import com.immersiveconvergence.api.energy.IICFluxProvider;
import com.immersiveconvergence.common.client.IEClientBridge;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.api.shapes.IBooleanOp;
import com.immersiveconvergence.api.shapes.Shapes;
import com.immersiveconvergence.api.shapes.VoxelShape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ICClientEventHandler {
    private static BlockPos cachedPos;
    private static long cachedMask;
    private static int cachedBoundsHash;
    private static VoxelShape cachedUnion;

    @SubscribeEvent public void onRenderOverlayPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null) { return; }
        TileEntity tile = player.world.getTileEntity(mc.objectMouseOver.getBlockPos());
        if (drawVoltmeter(event, player, tile)) { return; }
        if (!(tile instanceof IBlockOverlayText)) { return; }
        IBlockOverlayText overlay = (IBlockOverlayText)tile;
        String[] text = overlay.getOverlayText(player, mc.objectMouseOver, ICUtils.isHammer(player.getHeldItem(EnumHand.MAIN_HAND)));
        if (text == null || text.length == 0) { return; }
        boolean nixie = overlay.useNixieFont(player, mc.objectMouseOver) && ICMods.immersiveEngineering();
        FontRenderer font = nixie ? IEClientBridge.nixieFont() : mc.fontRenderer;
        int colour = nixie ? IEClientBridge.nixieColour() : 0xffffff;
        int line = 0;
        for (String s : text) {
            if (s == null) { continue; }
            font.drawString(s, event.getResolution().getScaledWidth() / 2 + 8, event.getResolution().getScaledHeight() / 2 + 8 + (line++) * font.FONT_HEIGHT, colour, true);
        }
    }

    private boolean drawVoltmeter(RenderGameOverlayEvent.Post event, EntityPlayer player, TileEntity tile) {
        if (!ICMods.immersiveEngineering() || tile == null) { return false; }
        if (!IEClientBridge.isVoltmeter(player.getHeldItem(EnumHand.MAIN_HAND))) { return false; }
        EnumFacing side = mcSide();
        int stored;
        int max;
        if (tile instanceof IICFluxAcceptor) {
            stored = ((IICFluxAcceptor)tile).getEnergyStored(side);
            max = ((IICFluxAcceptor)tile).getMaxEnergyStored(side);
        }
        else if (tile instanceof IICFluxProvider) {
            stored = ((IICFluxProvider)tile).getEnergyStored(side);
            max = ((IICFluxProvider)tile).getMaxEnergyStored(side);
        }
        else { return false; }
        if (max <= 0) { return false; }
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int line = 0;
        for (String s : IEClientBridge.energyStoredText(stored, max)) {
            if (s == null) { continue; }
            font.drawString(s, event.getResolution().getScaledWidth() / 2 + 8, event.getResolution().getScaledHeight() / 2 + 8 + (line++) * font.FONT_HEIGHT, IEClientBridge.nixieColour(), true);
        }
        return true;
    }

    private EnumFacing mcSide() {
        RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
        return mop == null ? null : mop.sideHit;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        RayTraceResult target = event.getTarget();
        if (target.typeOfHit != RayTraceResult.Type.BLOCK) { return; }
        BlockPos pos = target.getBlockPos();
        TileEntity tile = event.getPlayer().world.getTileEntity(pos);
        if (tile == null) { return; }

        ISelectionBounds icBounds = tile instanceof ISelectionBounds ? (ISelectionBounds)tile : null;
        List<AxisAlignedBB> bounds;
        if (icBounds != null) { bounds = icBounds.getAdvancedSelectionBounds(); }
        else if (ICMods.immersiveEngineering()) {
            List<AxisAlignedBB> worldBoxes = IEClientBridge.advancedSelectionBounds(tile);
            if (worldBoxes == null || worldBoxes.isEmpty()) { return; }
            bounds = new ArrayList<>(worldBoxes.size());
            for (AxisAlignedBB box : worldBoxes) { bounds.add(box.offset(-pos.getX(), -pos.getY(), -pos.getZ())); }
        }
        else { return; }
        if (bounds == null || bounds.isEmpty()) { return; }

        event.setCanceled(true);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        EntityPlayer player = event.getPlayer();
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

        VoxelShape union = getSelectionShape(icBounds, bounds, pos, player, target);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        union.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
            buffer.pos(minX + pos.getX() - px, minY + pos.getY() - py, minZ + pos.getZ() - pz).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
            buffer.pos(maxX + pos.getX() - px, maxY + pos.getY() - py, maxZ + pos.getZ() - pz).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        });
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private static VoxelShape getSelectionShape(ISelectionBounds asb, List<AxisAlignedBB> bounds, BlockPos pos, EntityPlayer player, RayTraceResult target) {
        long mask = 0;
        int boundsHash = 1;
        for (int i = 0; i < bounds.size(); i++) {
            AxisAlignedBB aabb = bounds.get(i);
            boundsHash = 31 * boundsHash + aabb.hashCode();
            if (i < 64 && (asb == null || asb.isOverrideBox(aabb, player, target, bounds))) { mask |= 1L << i; }
        }
        if (cachedUnion != null && pos.equals(cachedPos) && mask == cachedMask && boundsHash == cachedBoundsHash) { return cachedUnion; }

        VoxelShape union = Shapes.empty();
        for (int i = 0; i < bounds.size(); i++) {
            boolean included = i < 64 ? (mask & (1L << i)) != 0 : (asb == null || asb.isOverrideBox(bounds.get(i), player, target, bounds));
            if (included) { union = Shapes.joinUnoptimized(union, Shapes.create(bounds.get(i)), IBooleanOp.OR); }
        }
        union = union.optimize();

        cachedPos = pos.toImmutable();
        cachedMask = mask;
        cachedBoundsHash = boundsHash;
        cachedUnion = union;
        return union;
    }
}
