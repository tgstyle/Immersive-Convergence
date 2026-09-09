package com.immersiveconvergence.api.energy;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.AbstractConnection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@SuppressWarnings("unused")
public class ICWires {
    public static void distributeToNetwork(World world, BlockPos pos, @Nullable BlockPos excludedFirstHop, IntSupplier available, IntConsumer onMoved) {
        Set<AbstractConnection> outputs = ImmersiveNetHandler.INSTANCE.getIndirectEnergyConnections(pos, world, true);
        for (AbstractConnection con : outputs) {
            int remaining = available.getAsInt();
            if (remaining <= 0) { break; }
            if (!con.isEnergyOutput || con.cableType == null) { continue; }
            if (leavesBy(con, excludedFirstHop)) { continue; }
            IImmersiveConnectable end = ApiUtils.toIIC(con.end, world);
            if (end == null) { continue; }
            int moved = end.outputEnergy(Math.min(remaining, con.cableType.getTransferRate()), false, 0);
            if (moved > 0) { onMoved.accept(moved); }
        }
    }

    private static boolean leavesBy(AbstractConnection con, @Nullable BlockPos excludedFirstHop) {
        if (con.subConnections == null || con.subConnections.length == 0) { return false; }
        return con.subConnections[0].end.equals(excludedFirstHop);
    }
}
