package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

@SuppressWarnings("unused") public interface IICWireNetwork {
    @Nonnull Set<ICConnectionPoint> pointsAt(@Nonnull BlockPos pos);

    @Nonnull Collection<ICWire> wiresAt(@Nonnull ICConnectionPoint point);

    @Nullable ICWire wire(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second);

    @Nonnull Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk);

    @Nullable Integer component(@Nonnull ICConnectionPoint point);

    @Nonnull Set<ICConnectionPoint> componentPoints(int component);

    int version(int component);

    @Nullable IICConnector connector(@Nonnull ICConnectionPoint point);

    @Nullable ICConnectionPoint pointFor(@Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target);

    @Nonnull ICConnectResult connect(@Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type);

    boolean cut(@Nonnull ICWire wire, @Nonnull ICWireCause cause);

    void removeAt(@Nonnull BlockPos pos, @Nonnull ICWireCause cause);
}
