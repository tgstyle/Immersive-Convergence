package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.TemplateMultiblock;
import com.immersiveconvergence.client.ICManualHighlight;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.api.ManualPageMultiblock;
import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = ManualPageMultiblock.class, remap = false)
public abstract class MixinIEManualTrigger {
    @Shadow IMultiblock multiblock;

    @Unique private final List<BlockPos> immersiveconvergence$pendingVolumes = new ArrayList<>();

    @Redirect(method = "renderPage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z", remap = true), remap = false)
    private boolean highlightTriggerBlock(BlockRendererDispatcher dispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn) {
        int before = bufferBuilderIn.getVertexCount();
        boolean rendered = dispatcher.renderBlock(state, pos, blockAccess, bufferBuilderIn);
        if (rendered && immersiveconvergence$isTrigger(pos)) {
            int added = bufferBuilderIn.getVertexCount() - before;
            for (int i = 1; i <= added; i++) { bufferBuilderIn.putColorMultiplier(ICManualHighlight.RED, ICManualHighlight.GREEN, ICManualHighlight.BLUE, i); }
            if (!state.isFullCube()) { immersiveconvergence$pendingVolumes.add(pos); }
        }
        return rendered;
    }

    @Redirect(method = "renderPage", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/MultiblockHandler$IMultiblock;overwriteBlockRender(Lnet/minecraft/item/ItemStack;I)Z"), remap = false)
    private boolean highlightOverwrittenBlock(IMultiblock target, ItemStack stack, int iterator) {
        boolean trigger = immersiveconvergence$isTriggerIndex(iterator);
        if (trigger) { ICManualHighlight.set(true); }
        boolean overwritten;
        try { overwritten = target.overwriteBlockRender(stack, iterator); }
        finally { if (trigger) { ICManualHighlight.set(false); } }
        if (trigger && overwritten) {
            TemplateMultiblock template = immersiveconvergence$template();
            BlockPos cell = template == null ? null : template.primaryTrigger();
            if (cell != null) { immersiveconvergence$pendingVolumes.add(new BlockPos(cell.getZ(), cell.getY(), cell.getX())); }
        }
        return overwritten;
    }

    @Inject(method = "renderPage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V", ordinal = 0, remap = true), remap = false)
    private void drawTriggerVolumes(CallbackInfo ci) {
        if (immersiveconvergence$pendingVolumes.isEmpty()) { return; }
        for (BlockPos cell : immersiveconvergence$pendingVolumes) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(cell.getX(), cell.getY(), cell.getZ());
            immersiveconvergence$renderVolume();
            GlStateManager.popMatrix();
        }
        immersiveconvergence$pendingVolumes.clear();
    }

    @Unique private void immersiveconvergence$renderVolume() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        float lo = -0.002f, hi = 1.002f;
        float r = ICManualHighlight.RED, g = ICManualHighlight.GREEN, b = ICManualHighlight.BLUE, a = ICManualHighlight.VOLUME_ALPHA;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(lo, lo, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, lo, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, lo, hi).color(r, g, b, a).endVertex();
        buffer.pos(lo, lo, hi).color(r, g, b, a).endVertex();
        buffer.pos(lo, hi, hi).color(r, g, b, a).endVertex();
        buffer.pos(hi, hi, hi).color(r, g, b, a).endVertex();
        buffer.pos(hi, hi, lo).color(r, g, b, a).endVertex();
        buffer.pos(lo, hi, lo).color(r, g, b, a).endVertex();
        buffer.pos(lo, lo, lo).color(r, g, b, a).endVertex();
        buffer.pos(lo, hi, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, hi, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, lo, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, lo, hi).color(r, g, b, a).endVertex();
        buffer.pos(hi, hi, hi).color(r, g, b, a).endVertex();
        buffer.pos(lo, hi, hi).color(r, g, b, a).endVertex();
        buffer.pos(lo, lo, hi).color(r, g, b, a).endVertex();
        buffer.pos(lo, lo, lo).color(r, g, b, a).endVertex();
        buffer.pos(lo, lo, hi).color(r, g, b, a).endVertex();
        buffer.pos(lo, hi, hi).color(r, g, b, a).endVertex();
        buffer.pos(lo, hi, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, lo, hi).color(r, g, b, a).endVertex();
        buffer.pos(hi, lo, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, hi, lo).color(r, g, b, a).endVertex();
        buffer.pos(hi, hi, hi).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    @Unique private boolean immersiveconvergence$isTriggerIndex(int iterator) {
        TemplateMultiblock template = immersiveconvergence$template();
        return template != null && template.primaryTriggerRenderIndex() == iterator;
    }

    @Unique private boolean immersiveconvergence$isTrigger(BlockPos pos) {
        TemplateMultiblock template = immersiveconvergence$template();
        BlockPos trigger = template == null ? null : template.primaryTrigger();
        return trigger != null && pos.getX() == trigger.getZ() && pos.getY() == trigger.getY() && pos.getZ() == trigger.getX();
    }

    @Unique private TemplateMultiblock immersiveconvergence$template() {
        if (multiblock == null) { return null; }
        if (multiblock instanceof TemplateMultiblock) { return (TemplateMultiblock)multiblock; }
        return IEMultiblockRegistry.get(multiblock.getUniqueName());
    }
}
