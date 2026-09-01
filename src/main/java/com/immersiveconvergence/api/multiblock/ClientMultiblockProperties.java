package com.immersiveconvergence.api.multiblock;


import blusunrize.immersiveengineering.api.IEProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ClientMultiblockProperties implements ClientMultiblocks.MultiblockManualData {
    private final MachineTemplateMultiblock multiblock;
    @Nullable private NonNullList<ItemStack> materials;
    private final ItemStack renderStack;
    @Nullable private final Vec3 renderOffset;

    public ClientMultiblockProperties(MachineTemplateMultiblock multiblock, double offX, double offY, double offZ) { this(multiblock, new Vec3(offX, offY, offZ)); }

    private ClientMultiblockProperties(MachineTemplateMultiblock multiblock, @Nullable Vec3 renderOffset) {
        this.multiblock = multiblock;
        this.renderStack = new ItemStack(multiblock.getBlock());
        this.renderOffset = renderOffset;
    }

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

    @Override public boolean canRenderFormedStructure() { return this.renderOffset != null; }

    public void renderExtras(PoseStack matrix, MultiBufferSource buffer) {}

    public void renderCustomFormedStructure(PoseStack matrix, MultiBufferSource buffer) {}

    @Override public final void renderFormedStructure(PoseStack matrix, MultiBufferSource buffer) {
        Objects.requireNonNull(this.renderOffset);

        if (usingCustomRendering()) {
            renderCustomFormedStructure(matrix, buffer);
            return;
        }

        matrix.translate(this.renderOffset.x, this.renderOffset.y, this.renderOffset.z);
        Minecraft.getInstance().getItemRenderer().renderStatic(renderStack, ItemDisplayContext.NONE, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, matrix, buffer, null, 0);
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
