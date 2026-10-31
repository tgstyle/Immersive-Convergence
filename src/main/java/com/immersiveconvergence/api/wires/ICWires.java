package com.immersiveconvergence.api.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.wires.connectors.ICConnectableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public final class ICWires {
    private static IICWireService service;
    private static IICWireClientView clientView;

    private ICWires() {}

    public static void install(@Nonnull IICWireService installed) { service = installed; }

    public static void installClient(@Nonnull IICWireClientView installed) { clientView = installed; }

    @Nonnull public static IICWireClientView client() {
        if (clientView == null) { throw new IllegalStateException("The client wire view exists only on a client, from client setup"); }
        return clientView;
    }

    @Nonnull public static IICWireNetwork network(@Nonnull Level level) { return required().network(level); }

    @Nonnull public static IICEnergyNetwork energy(@Nonnull Level level) { return required().energy(level); }

    @Nonnull public static IICWireCollision collision() { return required().collision(); }

    @Nonnull public static InteractionResult useCoil(@Nonnull Player player, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull InteractionHand hand, float hitX, float hitY, float hitZ) { return required().useCoil(player, level, pos, hand, hitX, hitY, hitZ); }

    public static boolean isWireCoil(ItemStack stack) { return !stack.isEmpty() && (stack.getItem() instanceof IICWireCoil || (ICMods.immersiveEngineering() && ICBridges.integration().isWireCoil(stack))); }

    public static boolean hasNoExternalConnections(@Nullable Level level, @Nonnull ICConnectionPoint point) { return required().hasNoExternalConnections(level, point); }

    public static void syncConnectionPoints(@Nonnull ICConnectableBlockEntity tile) { required().syncConnectionPoints(tile); }

    public static void connectableLoaded(@Nonnull ICConnectableBlockEntity tile) { required().connectableLoaded(tile); }

    public static void connectableInvalidated(@Nonnull ICConnectableBlockEntity tile) { required().connectableInvalidated(tile); }

    public static int redstoneValue(Level level, BlockPos pos, int channel) { return required().redstoneValue(level, pos, channel); }

    public static void updateRedstoneValues(Level level, BlockPos pos) { required().updateRedstoneValues(level, pos); }

    @Nonnull private static IICWireService required() {
        if (service == null) { throw new IllegalStateException("The wire service is installed at mod construction"); }
        return service;
    }
}
