package com.immersiveconvergence.api.compat.computercraft;

import com.immersiveconvergence.api.compat.ICComputerTable;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ICPeripheral<T extends TileEntity> implements IPeripheral {
    private final World world;
    private final BlockPos pos;
    private final ICComputerTable<T> table;
    private final String[] methods;

    public ICPeripheral(World world, BlockPos pos, ICComputerTable<T> table) {
        this.world = world;
        this.pos = pos;
        this.table = table;
        this.methods = table.methodNames();
    }

    @Nonnull @Override public String getType() { return table.type; }

    @Nonnull @Override public String[] getMethodNames() { return methods.clone(); }

    @Nullable @Override public Object[] callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        if (method < 0 || method >= methods.length) { throw new LuaException("No such method"); }
        String name = methods[method];
        return context.executeMainThreadTask(() -> call(name, arguments));
    }

    private Object[] call(String name, Object[] arguments) throws LuaException {
        T tile = tile();
        if (tile == null) { throw new LuaException("Machine is not formed"); }
        try { return ICLuaConvert.toLua(table.invoke(tile, name, arguments)); }
        catch (IllegalArgumentException | IllegalStateException e) { throw new LuaException(e.getMessage()); }
    }

    @Override public void attach(@Nonnull IComputerAccess computer) { onMainThread(table::attach); }

    @Override public void detach(@Nonnull IComputerAccess computer) { onMainThread(table::detach); }

    private void onMainThread(Consumer<T> action) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            T tile = tile();
            if (tile != null) { action.accept(tile); }
        });
    }

    @Nullable private T tile() {
        TileEntity tile = world.getTileEntity(pos);
        return table.tileClass.isInstance(tile) ? table.tileClass.cast(tile) : null;
    }

    @Override public boolean equals(@Nullable IPeripheral other) {
        if (!(other instanceof ICPeripheral)) { return false; }
        ICPeripheral<?> peripheral = (ICPeripheral<?>)other;
        return world == peripheral.world && pos.equals(peripheral.pos) && table == peripheral.table;
    }
}
