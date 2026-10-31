package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface IICConnector {
    @Nonnull Collection<ICConnectionPoint> points();

    @Nullable ICConnectionPoint pointAt(@Nonnull ICTargetingInfo target, @Nonnull Vec3i offset);

    boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type);

    @Nonnull Vec3 offset(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type);

    @Nonnull BlockPos master();

    @Nonnull Collection<BlockPos> ignored();

    @Nonnull Collection<ICInternalLink> links();

    boolean passes(@Nonnull ICConnectionPoint point);

    void onWireAdded(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire);

    void onWireRemoved(@Nonnull ICConnectionPoint point, @Nonnull ICWire wire, @Nonnull ICWireCause cause);
}
