package com.immersiveconvergence.api.compat.computercraft;

import com.immersiveconvergence.api.compat.ICComputerTable;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ICPeripheral<T> implements IDynamicPeripheral {
    private final BlockEntity blockEntity;
    private final ICComputerTable<T> table;
    private final String[] methods;

    public ICPeripheral(BlockEntity blockEntity, ICComputerTable<T> table) {
        this.blockEntity = blockEntity;
        this.table = table;
        this.methods = table.methodNames();
    }

    @Nonnull @Override public String getType() { return table.type; }

    @Nonnull @Override public String[] getMethodNames() { return methods.clone(); }

    @Nonnull @Override public MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull IArguments arguments) throws LuaException {
        if (method < 0 || method >= methods.length) { throw new LuaException("No such method"); }
        String name = methods[method];
        Object[] args = arguments.getAll();
        return context.executeMainThreadTask(() -> call(name, args));
    }

    private Object[] call(String name, Object[] args) throws LuaException {
        T object = table.locate(blockEntity);
        if (object == null) { throw new LuaException("Machine is not formed"); }
        try { return ICLuaConvert.toLua(table.invoke(object, name, args)); }
        catch (RuntimeException e) { throw new LuaException(e.getMessage()); }
    }

    @Override public void attach(@Nonnull IComputerAccess computer) { onMainThread(table::attach); }

    @Override public void detach(@Nonnull IComputerAccess computer) { onMainThread(table::detach); }

    private void onMainThread(Consumer<T> action) {
        Level level = blockEntity.getLevel();
        MinecraftServer server = level == null ? null : level.getServer();
        if (server == null) { return; }
        server.execute(() -> {
            T object = table.locate(blockEntity);
            if (object != null) { action.accept(object); }
        });
    }

    @Nullable @Override public Object getTarget() { return blockEntity; }

    @Override public boolean equals(@Nullable IPeripheral other) { return other instanceof ICPeripheral<?> peripheral && blockEntity == peripheral.blockEntity && table == peripheral.table; }
}
