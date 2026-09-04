package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.lib.ICLib;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class MultiblockOverride {
    private static final Map<ResourceLocation, Optional<MultiblockOverride>> CACHE = new ConcurrentHashMap<>();
    private final MultiblockData data;
    private final List<Port> ports;
    @Nullable private ShapeData shape;

    private record Port(Capability<?> capability, BlockPos pos, @Nullable List<RelativeBlockFace> faces, BlockPos origin, @Nullable RelativeBlockFace originFace, boolean originFaceSet) {}

    private MultiblockOverride(MultiblockData data, List<Port> ports) {
        this.data = data;
        this.ports = ports;
    }

    @Nullable public static MultiblockOverride get(ResourceLocation id) { return CACHE.computeIfAbsent(id, key -> Optional.ofNullable(load(key))).orElse(null); }

    @Nullable private static MultiblockOverride load(ResourceLocation id) {
        if (QueueProcessor.MANAGED.contains(id)) { return null; }
        String modid = id.getNamespace();
        String name = id.getPath();
        if (ICLib.class.getResource("/assets/" + modid + "/multiblocks/" + name + ".json") == null) { return null; }
        MultiblockData data = MultiblockDataLoader.loadMultiblockData(ICLib.class, modid, name);
        if (data == null) { return null; }
        List<Port> ports = new ArrayList<>();
        if (data.pointsOfInterest != null) {
            for (PoIJSONSchema poi : data.pointsOfInterest) {
                Capability<?> capability = capabilityFor(poi.name);
                if (capability == null) { continue; }
                BlockPos pos = new BlockPos(poi.pos[0], poi.pos[1], poi.pos[2]);
                BlockPos origin = poi.origin == null ? pos : new BlockPos(poi.origin[0], poi.origin[1], poi.origin[2]);
                boolean originFaceSet = poi.originFacing != null;
                RelativeBlockFace originFace = originFaceSet && !poi.originFacing.isEmpty() && !poi.originFacing.equalsIgnoreCase("any") ? RelativeBlockFace.valueOf(poi.originFacing.toUpperCase(Locale.ROOT)) : null;
                List<RelativeBlockFace> faces = poi.relativeFaces.isEmpty() ? null : new ArrayList<>(poi.relativeFaces);
                ports.add(new Port(capability, pos, faces, origin, originFace, originFaceSet));
            }
        }
        ICLib.IC_LOGGER.info("Applying multiblock override for {}: {} shape, {} ports", id, data.shapeAABB == null ? "own" : "json", ports.size());
        return new MultiblockOverride(data, List.copyOf(ports));
    }

    @Nullable private static Capability<?> capabilityFor(String name) {
        if (name.startsWith("fluid_")) { return ForgeCapabilities.FLUID_HANDLER; }
        if (name.startsWith("item_")) { return ForgeCapabilities.ITEM_HANDLER; }
        if (name.startsWith("energy_")) { return ForgeCapabilities.ENERGY; }
        return null;
    }

    @Nullable public synchronized ShapeData shape(ResourceLocation id, Vec3i size) {
        if (data.shapeAABB == null) { return null; }
        if (shape == null) { shape = ShapeData.fromData(data, id.getPath(), size.getX(), size.getY(), size.getZ()); }
        return shape;
    }

    public boolean covers(Capability<?> capability) {
        for (Port port : ports) { if (port.capability == capability) { return true; } }
        return false;
    }

    @Nullable public CapabilityPosition map(Capability<?> capability, CapabilityPosition query) {
        for (Port port : ports) {
            if (port.capability != capability || !port.pos.equals(query.posInMultiblock())) { continue; }
            RelativeBlockFace side = query.side();
            if (side == null) { return new CapabilityPosition(port.origin, null); }
            if (port.faces == null || port.faces.contains(null) || port.faces.contains(side)) { return new CapabilityPosition(port.origin, port.originFaceSet ? port.originFace : side); }
        }
        return null;
    }
}
