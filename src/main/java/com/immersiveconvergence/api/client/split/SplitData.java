package com.immersiveconvergence.api.client.split;

import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record SplitData(Set<Vec3i> parts, Vec3i size) {
    private static final Set<ResourceLocation> REPORTED_MISSING = Collections.synchronizedSet(new HashSet<>());

    public static SplitData fromParts(List<Vec3i> parts) { return new SplitData(new HashSet<>(parts), sizeOf(parts)); }

    @Nullable public static SplitData fromMultiblock(ResourceLocation name, boolean mirrored) {
        Level level = Minecraft.getInstance().level;
        if (level == null) { return null; }
        if (!(MultiblockHandler.getByUniqueName(name) instanceof TemplateMultiblock template)) {
            if (REPORTED_MISSING.add(name)) { ICLib.IC_LOGGER.error("Split model references \"{}\", which is not a registered template multiblock", name); }
            return null;
        }
        TemplateMultiblock.TemplateData templateData;
        try { templateData = template.getTemplate(level); }
        catch (RuntimeException e) { return null; }
        Vec3i templateSize = templateData.template().getSize();
        BlockPos master = template.getMasterFromOriginOffset();
        List<Vec3i> parts = new ArrayList<>(templateData.blocksWithoutAir().size());
        for (StructureBlockInfo info : templateData.blocksWithoutAir()) {
            BlockPos pos = info.pos();
            if (mirrored) { pos = new BlockPos(templateSize.getX() - pos.getX() - 1, pos.getY(), pos.getZ()); }
            parts.add(pos.subtract(master));
        }
        if (parts.isEmpty()) { return null; }
        return fromParts(parts);
    }

    private static Vec3i sizeOf(List<Vec3i> parts) {
        List<BlockPos> positions = parts.stream().map(BlockPos::new).collect(Collectors.toList());
        BoundingBox box = BoundingBox.encapsulatingPositions(positions).orElseThrow(() -> new IllegalStateException("No positions to encapsulate"));
        return new Vec3i(box.getXSpan(), box.getYSpan(), box.getZSpan());
    }
}
