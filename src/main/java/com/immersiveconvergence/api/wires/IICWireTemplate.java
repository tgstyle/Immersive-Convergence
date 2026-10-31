package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public interface IICWireTemplate {
    String TAG = "wires";

    @Nonnull List<ICTemplateWire> wires();

    @Nonnull default List<ICConnectResult> placeWires(@Nonnull Level level, @Nonnull UnaryOperator<BlockPos> toWorld, @Nonnull UnaryOperator<ICTargetingInfo> targetToWorld) {
        List<ICConnectResult> results = new ArrayList<>();
        for (ICTemplateWire wire : wires()) { results.add(wire.place(level, toWorld, targetToWorld)); }
        return results;
    }
}
