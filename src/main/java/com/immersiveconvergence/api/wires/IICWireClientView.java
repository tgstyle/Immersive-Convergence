package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nonnull;
import java.util.Collection;

@SuppressWarnings("unused")
public interface IICWireClientView {
    @Nonnull Collection<ICWire> wiresAt(@Nonnull BlockPos pos);

    @Nonnull Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk);

    void repaint(@Nonnull ICWire wire);
}
