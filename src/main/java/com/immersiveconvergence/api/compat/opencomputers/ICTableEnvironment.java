package com.immersiveconvergence.api.compat.opencomputers;

import com.immersiveconvergence.api.compat.ICComputerTable;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ICTableEnvironment<T extends TileEntity> extends AbstractManagedEnvironment implements NamedBlock {
    private final World world;
    private final BlockPos pos;
    private final ICComputerTable<T> table;

    protected ICTableEnvironment(World world, BlockPos pos, ICComputerTable<T> table) {
        this.world = world;
        this.pos = pos;
        this.table = table;
        setNode(Network.newNode(this, Visibility.Network).withComponent(preferredName(), Visibility.Network).create());
    }

    protected Object[] call(String method, Arguments args) {
        T tile = tile();
        if (tile == null) { throw new IllegalStateException("Machine is not formed"); }
        return table.invoke(tile, method, args.toArray());
    }

    private T tile() {
        TileEntity tile = world.getTileEntity(pos);
        return table.tileClass.isInstance(tile) ? table.tileClass.cast(tile) : null;
    }

    @Override public void onConnect(Node node) {
        T tile = tile();
        if (tile != null) { table.attach(tile); }
    }

    @Override public void onDisconnect(Node node) {
        T tile = tile();
        if (tile != null) { table.detach(tile); }
    }

    @Override public String preferredName() { return table.type; }

    @Override public int priority() { return 1000; }
}
