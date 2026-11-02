package com.immersiveconvergence.api.multiblock.client;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.multiblock.MachineTemplateMultiblock;
import com.immersiveconvergence.api.util.ICUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ClientMultiblockProperties implements ClientMultiblocks.MultiblockManualData {
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
                if (info.state().hasProperty(ModProperties.MULTIBLOCKSLAVE) && info.state().getValue(ModProperties.MULTIBLOCKSLAVE)) { continue; }
                ItemStack picked = getPickBlock(info.state());
                boolean added = false;
                for (ItemStack existing : this.materials)
                    if (ItemStack.isSameItem(existing, picked)) {
                        existing.grow(1);
                        added = true;
                        break;
                    }
                if (!added) { this.materials.add(picked.copy()); }
            }
        }
        return this.materials;
    }

    @Override public boolean canRenderFormedStructure() { return true; }

    public void renderExtras() {}

    public void renderCustomFormedStructure() {}

    @Override public final void renderFormedStructure(PoseStack matrix, MultiBufferSource buffer) {
        if (usingCustomRendering()) {
            renderCustomFormedStructure();
            return;
        }

        BlockPos master = this.multiblock.getMasterFromOriginOffset();
        matrix.translate(master.getX(), master.getY(), master.getZ());
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(this.multiblock.getBlock().defaultBlockState());
        VertexConsumer consumer = buffer.getBuffer(ICMods.immersiveEngineering() ? ICBridges.client().translucentFullbright() : RenderType.translucent());
        for (BakedQuad quad : model.getQuads(null, null, ICUtils.RANDOM_SOURCE, ModelData.EMPTY, null)) { consumer.putBulkData(matrix.last(), quad, 1, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY); }
        matrix.pushPose();
        { renderExtras(); }
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
