package com.immersiveconvergence.api.compat;

import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ICComputerTable<T extends TileEntity> {
    private static final Object[] NO_ARGS = new Object[0];
    public final String type;
    public final Class<T> tileClass;
    private final Function<TileEntity, T> locator;
    private final Map<String, ICComputerMethod<T>> methods = new LinkedHashMap<>();
    private Consumer<T> attach = tile -> {};
    private Consumer<T> detach = tile -> {};

    public ICComputerTable(String type, Class<T> tileClass, Function<TileEntity, T> locator) {
        this.type = type;
        this.tileClass = tileClass;
        this.locator = locator;
    }

    public ICComputerTable<T> add(String name, Function<T, Object> getter) { return call(name, (tile, args) -> new Object[] {getter.apply(tile)}); }

    public ICComputerTable<T> call(String name, ICComputerMethod<T> method) {
        methods.put(name, method);
        return this;
    }

    public ICComputerTable<T> control(Function<T, Boolean> read, BiConsumer<T, Boolean> write) {
        call(ICComputerControl.ENABLE_COMPUTER_CONTROL, (tile, args) -> {
            write.accept(tile, ICComputerArgs.checkBoolean(args, 0) ? Boolean.TRUE : null);
            return null;
        });
        return call(ICComputerControl.SET_ENABLED, (tile, args) -> {
            boolean enabled = ICComputerArgs.checkBoolean(args, 0);
            if (read.apply(tile) == null) { throw new IllegalStateException(ICComputerControl.CONTROL_DISABLED); }
            write.accept(tile, enabled);
            return null;
        });
    }

    public ICComputerTable<T> connection(Consumer<T> attach, Consumer<T> detach) {
        this.attach = attach;
        this.detach = detach;
        return this;
    }

    public void attach(T tile) { attach.accept(tile); }

    public void detach(T tile) { detach.accept(tile); }

    @Nullable public T locate(@Nullable TileEntity tile) { return tile == null ? null : locator.apply(tile); }

    public Object[] invoke(T tile, String name, Object[] args) { return methods.get(name).call(tile, args); }

    public Object[] invoke(T tile, String name) { return invoke(tile, name, NO_ARGS); }

    public String[] methodNames() { return methods.keySet().toArray(new String[0]); }
}
