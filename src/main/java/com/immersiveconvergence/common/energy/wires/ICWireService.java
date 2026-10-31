package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.IICEnergyNetwork;
import com.immersiveconvergence.api.wires.IICWireCollision;
import com.immersiveconvergence.api.wires.IICWireNetwork;
import com.immersiveconvergence.api.wires.IICWireService;
import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectable;
import com.immersiveconvergence.common.energy.IEWireBridge;
import com.immersiveconvergence.common.energy.IEWireGraph;
import com.immersiveconvergence.common.energy.IEWireView;
import com.immersiveconvergence.common.util.ICWireCollision;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICWireService implements IICWireService {
    private final IICWireCollision collision = new ICWireCollision();

    @Nonnull public static ICWireGraph graph(@Nonnull World world) { return ICMods.immersiveEngineering() ? new IEWireGraph(world) : ICWireSavedData.get(world); }

    @Nonnull @Override public IICWireNetwork network(@Nonnull World world) { return ICMods.immersiveEngineering() ? new IEWireView(world) : ICWireSavedData.get(world); }

    @Nonnull @Override public IICEnergyNetwork energy(@Nonnull World world) { return new ICEnergyView(world); }

    @Nonnull @Override public IICWireCollision collision() { return collision; }

    @Nonnull @Override public EnumActionResult useCoil(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) { return ICWireCoils.use(player, world, pos, hand, side, hitX, hitY, hitZ); }

    @Override public int connectionCount(@Nullable World world, @Nonnull BlockPos pos) {
        if (ICMods.immersiveEngineering()) { return IEWireBridge.connectionCount(world, pos); }
        return world == null || world.isRemote ? 0 : ICWireSavedData.get(world).getWireCount(pos);
    }

    @Override public void syncConnectionPoints(@Nonnull ICTileEntityConnectable tile) {
        if (tile.hasWorld() && !tile.getWorld().isRemote && !ICMods.immersiveEngineering()) { ICWireSavedData.get(tile.getWorld()).sync(tile); }
    }

    @Override public void connectableLoaded(@Nonnull ICTileEntityConnectable tile) {
        if (ICMods.immersiveEngineering()) { IEWireBridge.onConnectableLoad(tile); }
        else if (tile.hasWorld() && !tile.getWorld().isRemote) { ICWireSavedData.get(tile.getWorld()).syncLoaded(tile); }
    }

    @Override public void connectableInvalidated(@Nonnull ICTileEntityConnectable tile) {
        if (ICMods.immersiveEngineering()) { IEWireBridge.onConnectableInvalidate(tile); }
        else if (tile.hasWorld()) { ICWireRemoval.checkLater(tile.getWorld(), tile.getPos()); }
    }
}
