package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class ClientMultiblocks {
    private static final Map<IMultiblock, MultiblockManualData> CACHED_PROPERTIES = new IdentityHashMap<>();

    public static MultiblockManualData get(IMultiblock multiblock) {
        return CACHED_PROPERTIES.computeIfAbsent(multiblock, mb -> {
            Mutable<MultiblockManualData> result = new MutableObject<>();
            mb.initializeClient(result::setValue);
            return Objects.requireNonNull(result.getValue(), "Did not get client properties for "+mb.getUniqueName());
        });
    }

    public interface MultiblockManualData {
        NonNullList<ItemStack> getTotalMaterials();

        boolean canRenderFormedStructure();

        void renderFormedStructure(PoseStack transform, MultiBufferSource buffer);
    }
}
