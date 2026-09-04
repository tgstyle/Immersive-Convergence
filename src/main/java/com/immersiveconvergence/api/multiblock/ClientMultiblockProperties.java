package com.immersiveconvergence.api.multiblock;


import blusunrize.immersiveengineering.api.IEProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import com.mojang.blaze3d.vertex.VertexConsumer;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.api.ApiUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import java.util.List;

import javax.annotation.Nullable;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ClientMultiblockProperties implements ClientMultiblocks.MultiblockManualData {
    private final MachineTemplateMultiblock multiblock;
    @Nullable private NonNullList<ItemStack> materials;
    public ClientMultiblockProperties(MachineTemplateMultiblock multiblock) { this.multiblock = multiblock; }

    protected boolean usingCustomRendering() { return false; }

    @Override public NonNullList<ItemStack> getTotalMaterials() {
        if (this.materials == null) {
            assert Minecraft.getInstance().level != null;
            List<StructureTemplate.StructureBlockInfo> structure = this.multiblock.getStructure(Minecraft.getInstance().level);
            this.materials = NonNullList.create();
            for (StructureTemplate.StructureBlockInfo info : structure) {
                if (info.state().hasProperty(IEProperties.MULTIBLOCKSLAVE) && info.state().getValue(IEProperties.MULTIBLOCKSLAVE)) continue;
                ItemStack picked = getPickBlock(info.state());
                boolean added = false;
                for (ItemStack existing : this.materials)
                    if (ItemStack.isSameItem(existing, picked)) {
                        existing.grow(1);
                        added = true;
                        break;
                    }
                if (!added) this.materials.add(picked.copy());
            }
        }
        return this.materials;
    }

    @Override public boolean canRenderFormedStructure() { return true; }

    public void renderExtras(PoseStack matrix, MultiBufferSource buffer) {}

    public void renderCustomFormedStructure(PoseStack matrix, MultiBufferSource buffer) {}

    @Override public final void renderFormedStructure(PoseStack matrix, MultiBufferSource buffer) {
        if (usingCustomRendering()) {
            renderCustomFormedStructure(matrix, buffer);
            return;
        }

        BlockPos master = this.multiblock.getMasterFromOriginOffset();
        matrix.translate(master.getX(), master.getY(), master.getZ());
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(this.multiblock.getBlock().defaultBlockState());
        VertexConsumer consumer = buffer.getBuffer(IERenderTypes.TRANSLUCENT_FULLBRIGHT);
        for (BakedQuad quad : model.getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null)) { consumer.putBulkData(matrix.last(), quad, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY); }
        matrix.pushPose();
        {
            renderExtras(matrix, buffer);
        }
        matrix.popPose();
    }

    private static ItemStack getPickBlock(BlockState state) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;
        BlockHitResult hit = new BlockHitResult(Vec3.ZERO, Direction.DOWN, BlockPos.ZERO, false);
        if (level != null && player != null) {
            try {
                ItemStack picked = state.getBlock().getCloneItemStack(state, hit, level, BlockPos.ZERO, player);
                if (!picked.isEmpty()) { return picked; }
            } catch (Exception ignored) { }
        }
        return new ItemStack(state.getBlock());
    }
}
