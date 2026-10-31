package com.immersiveconvergence.api.wires;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

@SuppressWarnings("unused")
public interface IICWireCollision {
    @Nonnull Set<ICWire> wiresHitting(@Nonnull World world, @Nonnull AxisAlignedBB box);

    @Nullable ICWireHit raytrace(@Nonnull World world, @Nonnull Vec3d from, @Nonnull Vec3d to);
}
