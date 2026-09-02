package com.immersiveconvergence.api.gui;

import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MenuSyncData<T> {
    private final MenuSyncSerializers.DataSerializer<T> serializer;
    private final Supplier<T> get;
    private final Consumer<T> set;
    private T current;

    public MenuSyncData(MenuSyncSerializers.DataSerializer<T> serializer, Supplier<T> get, Consumer<T> set) {
        this.serializer = serializer;
        this.get = get;
        this.set = set;
    }

    public static MenuSyncData<Integer> int32(Supplier<Integer> get, Consumer<Integer> set) { return new MenuSyncData<>(MenuSyncSerializers.INT32, get, set); }

    public static MenuSyncData<Integer> energy(IMutableEnergyStorage storage) {
        Objects.requireNonNull(storage);
        Supplier<Integer> getEnergy = storage::getEnergyStored;
        Consumer<Integer> setEnergy = storage::setStoredEnergy;
        return int32(getEnergy, setEnergy);
    }

    public static MenuSyncData<FluidStack> fluid(FluidTank tank) {
        Objects.requireNonNull(tank);
        MenuSyncSerializers.DataSerializer<FluidStack> serializer = MenuSyncSerializers.FLUID_STACK;
        Supplier<FluidStack> getFluid = tank::getFluid;
        return new MenuSyncData<>(serializer, getFluid, tank::setFluid);
    }

    public static MenuSyncData<ItemStack> itemStack(Supplier<ItemStack> get, Consumer<ItemStack> set) { return new MenuSyncData<>(MenuSyncSerializers.ITEM_STACK, get, set); }

    public static MenuSyncData<Float> float32(Supplier<Float> get, Consumer<Float> set) { return new MenuSyncData<>(MenuSyncSerializers.FLOAT, get, set); }

    public boolean needsUpdate() {
        T newValue = this.get.get();
        if (newValue == null && this.current == null) { return false; }
        else if (this.current != null && newValue != null && this.serializer.equals().test(this.current, newValue)) { return false; }
        else { this.current = this.serializer.copy().apply(newValue); return true; }
    }

    @SuppressWarnings("unchecked")
    public void processSync(Object receivedData) { this.set.accept(this.serializer.copy().apply((T) receivedData)); }

    public MenuSyncSerializers.DataPair<T> dataPair() { return new MenuSyncSerializers.DataPair<>(this.serializer, this.current); }
}
