package com.immersiveconvergence.api.wires;

import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused") public class ICWires {
    private static IICWireService service;
    private static IICWireClientView clientView;

    public static void install(@Nonnull IICWireService installed) { service = installed; }

    public static void installClient(@Nonnull IICWireClientView installed) { clientView = installed; }

    @Nonnull public static IICWireNetwork network(@Nonnull World world) { return required().network(world); }

    @Nonnull public static IICEnergyNetwork energy(@Nonnull World world) { return required().energy(world); }

    @Nonnull public static IICWireCollision collision() { return required().collision(); }

    @Nonnull public static EnumActionResult useCoil(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) { return required().useCoil(player, world, pos, hand, side, hitX, hitY, hitZ); }

    public static int connectionCount(@Nullable World world, @Nonnull BlockPos pos) { return required().connectionCount(world, pos); }

    public static void syncConnectionPoints(@Nonnull ICTileEntityConnectable tile) { required().syncConnectionPoints(tile); }

    public static void connectableLoaded(@Nonnull ICTileEntityConnectable tile) { required().connectableLoaded(tile); }

    public static void connectableInvalidated(@Nonnull ICTileEntityConnectable tile) { required().connectableInvalidated(tile); }

    @Nonnull public static IICWireClientView client() {
        if (clientView == null) { throw new IllegalStateException("The client wire view exists only on a client, from preInit"); }
        return clientView;
    }

    @Nonnull private static IICWireService required() {
        if (service == null) { throw new IllegalStateException("The wire service is installed at preInit"); }
        return service;
    }
}
