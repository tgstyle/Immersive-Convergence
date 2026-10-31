package com.immersiveconvergence.client;

import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICWireClientView;
import com.immersiveconvergence.common.energy.IEWireView;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class IEWireClientView implements IICWireClientView {
    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull BlockPos pos) {
        Set<ICWire> wires = new HashSet<>();
        World world = Minecraft.getMinecraft().world;
        if (world == null) { return wires; }
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        if (connections == null) { return wires; }
        for (Connection connection : connections) {
            ICWire wire = IEWireView.wireOf(world, connection);
            if (wire != null) { wires.add(wire); }
        }
        return wires;
    }

    @Nonnull @Override public Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk) {
        Set<ICWire> wires = new HashSet<>();
        World world = Minecraft.getMinecraft().world;
        if (world == null) { return wires; }
        for (Connection connection : ImmersiveNetHandler.INSTANCE.getAllConnections(world)) {
            ICWire wire = IEWireView.wireOf(world, connection);
            if (wire != null && wire.getChunks().contains(chunk)) { wires.add(wire); }
        }
        return wires;
    }

    @Override public void repaint(@Nonnull ICWire wire) {
        ImmersiveEngineering.proxy.clearConnectionModelCache();
        ICClientWires.repaint(wire);
    }
}
