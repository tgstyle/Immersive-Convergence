package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.client.models.split.PolygonUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PolygonUtils.class)
public abstract class PolygonUtilsMixin {
    @Shadow(remap = false) private static int getOffset(VertexFormatElement element) { throw new IllegalStateException(); }

    @ModifyArg(method = "toBakedQuad(Lmalte0811/modelsplitter/model/Polygon;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/renderer/block/model/BakedQuad;", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/client/models/split/PolygonUtils;toBakedQuad(Ljava/util/List;Lblusunrize/immersiveengineering/client/models/split/PolygonUtils$ExtraQuadData;Lcom/mojang/math/Transformation;ZZ)Lnet/minecraft/client/renderer/block/model/BakedQuad;", remap = false), index = 4, remap = false)
    private static boolean ic$noVanillaShade(boolean shade) { return false; }

    @Inject(method = "toBakedQuad(Lmalte0811/modelsplitter/model/Polygon;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/renderer/block/model/BakedQuad;", at = @At("RETURN"), remap = false)
    private static void ic$bakeSplitLighting(CallbackInfoReturnable<BakedQuad> cir) {
        final int stride = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
        final int colorOffset = getOffset(VertexFormatElement.COLOR);
        final int normalOffset = getOffset(VertexFormatElement.NORMAL);
        int[] verts = cir.getReturnValue().getVertices();
        for (int v = 0; v < 4; ++v) {
            final int base = v * stride;
            final int packedNormal = verts[base + normalOffset];
            final float nx = ((byte) packedNormal) / 127f;
            final float ny = ((byte) (packedNormal >> 8)) / 127f;
            final float nz = ((byte) (packedNormal >> 16)) / 127f;
            final float shade = Math.min(nx * nx * 0.6f + ny * ny * ((3 + ny) / 4f) + nz * nz * 0.8f, 1);
            final int c = verts[base + colorOffset];
            final int r = Math.min((int) ((c & 255) * shade), 255);
            final int g = Math.min((int) (((c >> 8) & 255) * shade), 255);
            final int b = Math.min((int) (((c >> 16) & 255) * shade), 255);
            verts[base + colorOffset] = r | (g << 8) | (b << 16) | (c & 0xFF000000);
        }
    }
}
