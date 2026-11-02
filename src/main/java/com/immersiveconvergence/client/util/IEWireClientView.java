package com.immersiveconvergence.client.util;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICWireClientView;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireBridge;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireView;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public final class IEWireClientView implements IICWireClientView {
    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull BlockPos pos) {
        Set<ICWire> wires = new HashSet<>();
        Level level = Minecraft.getInstance().level;
        if (level == null) { return wires; }
        IEWireView view = new IEWireView(level);
        for (ICConnectionPoint point : view.pointsAt(pos)) { wires.addAll(view.wiresAt(point)); }
        return wires;
    }

    @Nonnull @Override public Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk) {
        Level level = Minecraft.getInstance().level;
        return level == null ? new HashSet<>() : new IEWireView(level).wiresIn(chunk);
    }

    @Override public void repaint(@Nonnull ICWire wire) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) { return; }
        for (SectionPos section : IEWireBridge.sections(minecraft.level, wire)) { minecraft.levelRenderer.setSectionDirty(section.x(), section.y(), section.z()); }
    }
}
