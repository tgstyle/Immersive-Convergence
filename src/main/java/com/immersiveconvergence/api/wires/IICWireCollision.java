package com.immersiveconvergence.api.wires;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

@SuppressWarnings("unused")
public interface IICWireCollision {
    @Nonnull Set<ICWire> wiresHitting(@Nonnull Level level, @Nonnull AABB box);

    @Nullable ICWireHit raytrace(@Nonnull Level level, @Nonnull Vec3 from, @Nonnull Vec3 to);
}
