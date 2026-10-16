package com.immersiveconvergence.api.compat;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ICComputerTable<T> {
    public final String type;
    private final List<Supplier<? extends BlockEntityType<?>>> blockEntities;
    private final Predicate<BlockEntity> attachable;
    private final Function<BlockEntity, T> locator;
    private final Map<String, ICComputerMethod<T>> methods = new LinkedHashMap<>();
    private Consumer<T> attach = object -> {};
    private Consumer<T> detach = object -> {};

    public ICComputerTable(String type, List<Supplier<? extends BlockEntityType<?>>> blockEntities, Predicate<BlockEntity> attachable, Function<BlockEntity, T> locator) {
        this.type = type;
        this.blockEntities = blockEntities;
        this.attachable = attachable;
        this.locator = locator;
    }

    public ICComputerTable<T> add(String name, Function<T, Object> getter) { return call(name, (object, args) -> new Object[] {getter.apply(object)}); }

    public ICComputerTable<T> call(String name, ICComputerMethod<T> method) {
        methods.put(name, method);
        return this;
    }

    public ICComputerTable<T> connection(Consumer<T> attach, Consumer<T> detach) {
        this.attach = attach;
        this.detach = detach;
        return this;
    }

    public List<Supplier<? extends BlockEntityType<?>>> blockEntities() { return blockEntities; }

    public boolean attachesTo(BlockEntity blockEntity) { return attachable.test(blockEntity); }

    public void attach(T object) { attach.accept(object); }

    public void detach(T object) { detach.accept(object); }

    @Nullable public T locate(BlockEntity blockEntity) { return locator.apply(blockEntity); }

    public Object[] invoke(T object, String name, Object[] args) { return methods.get(name).call(object, args); }

    public String[] methodNames() { return methods.keySet().toArray(new String[0]); }
}
