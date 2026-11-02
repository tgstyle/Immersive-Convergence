package com.immersiveconvergence.client.util;

import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICWireClientView;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class ICWireClientView implements IICWireClientView {
    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull BlockPos pos) { return ICClientWires.getWiresAt(pos); }

    @Nonnull @Override public Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk) { return ICClientWires.getWiresIn(chunk); }

    @Override public void repaint(@Nonnull ICWire wire) { ICClientWires.repaint(wire); }
}
