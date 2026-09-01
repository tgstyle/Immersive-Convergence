package com.immersiveconvergence.mixin;

import com.immersiveconvergence.client.ICManualHighlight;

import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Collection;

@Mixin(value = ClientProxy.class, remap = false)
public abstract class MixinIEManualConveyor {
    @Redirect(method = "drawConveyorInGui", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/client/ClientUtils;renderQuads(Ljava/util/Collection;FFFF)V"), remap = false)
    private void tintTriggerConveyor(Collection<BakedQuad> quads, float brightness, float red, float green, float blue) {
        if (!ICManualHighlight.isActive()) {
            ClientUtils.renderQuads(quads, brightness, red, green, blue);
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        for (BakedQuad quad : quads) {
            buffer.begin(7, DefaultVertexFormats.ITEM);
            buffer.addVertexData(quad.getVertexData());
            float dim = brightness * ICManualHighlight.OVERWRITE_BRIGHTNESS;
            buffer.putColorRGB_F4(dim * ICManualHighlight.RED, dim * ICManualHighlight.GREEN, dim * ICManualHighlight.BLUE);
            Vec3i normal = quad.getFace().getDirectionVec();
            buffer.putNormal(normal.getX(), normal.getY(), normal.getZ());
            tessellator.draw();
        }
    }
}
