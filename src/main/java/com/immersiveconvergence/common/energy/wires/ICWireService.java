package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.IICEnergyNetwork;
import com.immersiveconvergence.api.wires.IICWireCollision;
import com.immersiveconvergence.api.wires.IICWireNetwork;
import com.immersiveconvergence.api.wires.IICWireService;
import com.immersiveconvergence.api.wires.connectors.ICConnectableBlockEntity;
import com.immersiveconvergence.common.ie.IEWireBridge;
import com.immersiveconvergence.common.ie.IEWireView;
import com.immersiveconvergence.common.util.ICWireCollision;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ICWireService implements IICWireService {
    private final IICWireCollision collision = new ICWireCollision();

    @Nonnull @Override public IICEnergyNetwork energy(@Nonnull Level level) { return new ICEnergyView(level); }

    @Nonnull @Override public IICWireCollision collision() { return collision; }

    @Nonnull @Override public InteractionResult useCoil(@Nonnull Player player, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull InteractionHand hand, float hitX, float hitY, float hitZ) { return ICWireCoils.use(player, level, pos, hand, hitX, hitY, hitZ); }

    @Nonnull @Override public IICWireNetwork network(@Nonnull Level level) {
        if (ICMods.immersiveEngineering()) { return new IEWireView(level); }
        if (level instanceof ServerLevel server) { return ICWireSavedData.get(server); }
        ICWireNetwork empty = new ICWireNetwork(() -> {});
        empty.bind(level);
        return empty;
    }

    @Override public boolean hasNoExternalConnections(@Nullable Level level, @Nonnull ICConnectionPoint point) {
        if (level != null && ICMods.immersiveEngineering()) { return IEWireBridge.hasNoExternalConnections(level, point); }
        ICWireNetwork network = nativeNetwork(level);
        return network == null || network.wiresAt(point).isEmpty();
    }

    @Override public void syncConnectionPoints(@Nonnull ICConnectableBlockEntity tile) {
        ICWireNetwork network = nativeNetwork(tile.getLevel());
        if (network == null) { return; }
        List<ICConnectionPoint> points = new ArrayList<>(tile.points());
        Collection<ICInternalLink> internal = tile.links();
        for (ICConnectionPoint point : points) { network.setPasses(point, tile.passes(point)); }
        for (ICInternalLink link : internal) { network.setLink(link.getFirst(), link.getSecond(), link.getLoss()); }
        for (int first = 0; first < points.size(); first++) {
            for (int second = first + 1; second < points.size(); second++) {
                if (!linked(internal, points.get(first), points.get(second))) { network.removeLink(points.get(first), points.get(second)); }
            }
        }
    }

    @Override public void connectableLoaded(@Nonnull ICConnectableBlockEntity tile) {
        ICWireNetwork network = nativeNetwork(tile.getLevel());
        if (network != null) { network.syncLoaded(tile::syncConnectionPoints); }
    }

    @Override public void connectableInvalidated(@Nonnull ICConnectableBlockEntity tile) {
        if (tile.getLevel() instanceof ServerLevel server && !ICMods.immersiveEngineering()) { ICWireRemoval.checkLater(server, tile.getBlockPos()); }
    }

    @Override public int redstoneValue(Level level, BlockPos pos, int channel) { return IEWireBridge.redstoneValue(level, pos, channel); }

    @Override public void updateRedstoneValues(Level level, BlockPos pos) { IEWireBridge.updateRedstoneValues(level, pos); }

    private static boolean linked(Collection<ICInternalLink> internal, ICConnectionPoint a, ICConnectionPoint b) {
        for (ICInternalLink link : internal) {
            if ((link.getFirst().equals(a) && link.getSecond().equals(b)) || (link.getFirst().equals(b) && link.getSecond().equals(a))) { return true; }
        }
        return false;
    }

    @Nullable private static ICWireNetwork nativeNetwork(@Nullable Level level) { return level instanceof ServerLevel server && !ICMods.immersiveEngineering() ? ICWireSavedData.get(server) : null; }
}
