package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.api.energy.ICTargetingInfo;
import com.immersiveconvergence.api.energy.ICWireType;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.WireType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class IEWireBridge {
    private IEWireBridge() {}

    public static void mirrorWireTypes() {
        for (WireType wire : WireType.getValues()) {
            if (wire != null) { required(wire); }
        }
    }

    @Nullable public static ICWireType of(@Nullable WireType wire) { return wire == null ? null : required(wire); }

    @Nonnull public static ICWireType required(@Nonnull WireType wire) {
        return ICWireType.register(new ICWireType(wire.getUniqueName(), wire.getCategory(), wire.getRenderDiameter(), wire.getTransferRate()));
    }

    @Nullable public static WireType toIE(@Nullable ICWireType type) { return type == null ? null : WireType.getValue(type.getUniqueName()); }

    public static ICTargetingInfo targeting(@Nonnull TargetingInfo target) { return new ICTargetingInfo(target.side, target.hitX, target.hitY, target.hitZ); }
}
