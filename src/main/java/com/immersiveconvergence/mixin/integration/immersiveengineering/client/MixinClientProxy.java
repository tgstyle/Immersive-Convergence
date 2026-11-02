package com.immersiveconvergence.mixin.integration.immersiveengineering.client;

import com.immersiveconvergence.client.ICManualHighlight;
import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblock;
import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry;
import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import java.util.Collection;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientProxy.class) public abstract class MixinClientProxy {
    @Redirect(method = "handleTileSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;getPos()Lnet/minecraft/util/math/BlockPos;", remap = true), remap = false) private BlockPos redirectSoundPos(TileEntity tile) {
        if (!(tile instanceof TileEntityMultiblockPart)) { return tile.getPos(); }
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)tile;
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        if (template == null || part.pos < 0) { return tile.getPos(); }
        int[] declared = template.positionsNamed("sound");
        return declared == null ? tile.getPos() : part.getBlockPosForPos(declared[0]);
    }

    @Redirect(method = "drawConveyorInGui", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/client/ClientUtils;renderQuads(Ljava/util/Collection;FFFF)V", remap = false), remap = false, require = 0) private void tintTriggerConveyor(Collection<BakedQuad> quads, float brightness, float red, float green, float blue) {
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
