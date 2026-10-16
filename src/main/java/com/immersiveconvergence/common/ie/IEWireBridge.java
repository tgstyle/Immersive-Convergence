package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.energy.ICConnectionPoint;
import com.immersiveconvergence.api.energy.ICTargetingInfo;
import com.immersiveconvergence.api.energy.ICWireType;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.IEnergyWire;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.items.WireCoilItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntSupplier;

public final class IEWireBridge {
    private IEWireBridge() {}

    @Nonnull public static ICWireType of(@Nonnull WireType wire) { return ICWireType.registerIfAbsent(wire.getUniqueName(), () -> mirror(wire)); }

    @Nonnull private static ICWireType mirror(@Nonnull WireType wire) {
        IntSupplier transferRate = wire instanceof IEnergyWire energy ? energy::getTransferRate : () -> Integer.MAX_VALUE;
        return new ICWireType(wire.getUniqueName(), wire.getCategory(), wire::getRenderDiameter, transferRate);
    }

    @Nullable public static ICWireType ofNullable(@Nullable WireType wire) { return wire == null ? null : of(wire); }

    @Nullable public static ICWireType byName(@Nonnull String name) { return ofNullable(WireType.getValue(name)); }

    @Nonnull public static ICConnectionPoint point(@Nonnull ConnectionPoint cp) { return new ICConnectionPoint(cp.position(), cp.index()); }

    @Nullable public static ConnectionPoint toIE(@Nullable ICConnectionPoint cp) { return cp == null ? null : new ConnectionPoint(cp.position(), cp.index()); }

    @Nonnull public static ICTargetingInfo targeting(@Nonnull TargetingInfo target) { return new ICTargetingInfo(target.hitX, target.hitY, target.hitZ); }

    public static boolean isWireCoil(ItemStack stack) { return stack.getItem() instanceof WireCoilItem; }

    public static boolean hasNoExternalConnections(Level level, ICConnectionPoint cp) {
        ConnectionPoint point = new ConnectionPoint(cp.position(), cp.index());
        return GlobalWireNetwork.getNetwork(level).getLocalNet(point).getConnections(point).stream().allMatch(Connection::isInternal);
    }

    public static int redstoneValue(Level level, BlockPos pos, int channel) {
        RedstoneNetworkHandler handler = redstoneHandler(level, pos);
        return handler != null ? handler.getValue(channel) : 0;
    }

    public static void updateRedstoneValues(Level level, BlockPos pos) {
        RedstoneNetworkHandler handler = redstoneHandler(level, pos);
        if (handler != null) { handler.updateValues(); }
    }

    @Nullable private static RedstoneNetworkHandler redstoneHandler(Level level, BlockPos pos) { return GlobalWireNetwork.getNetwork(level).getLocalNet(pos).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class); }
}
