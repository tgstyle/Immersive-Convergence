package com.immersiveconvergence.api.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ComputerValues {
    private ComputerValues() {}

    public static Map<String, Object> tankInfo(IFluidTank tank) {
        Map<String, Object> info = new HashMap<>();
        info.put("capacity", tank.getCapacity());
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty()) {
            info.put("amount", 0);
            return info;
        }
        info.put("hasTag", !fluid.isComponentsPatchEmpty());
        putFluid(info, fluid);
        return info;
    }

    public static List<Map<String, Object>> fluids(List<FluidStack> fluids) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (FluidStack fluid : fluids) {
            Map<String, Object> info = new HashMap<>();
            putFluid(info, fluid);
            result.add(info);
        }
        return result;
    }

    public static String fluidName(Fluid fluid) { return BuiltInRegistries.FLUID.getKey(fluid).getPath(); }

    public static ItemStack slot(IItemHandler handler, int slot, int slots, String error) {
        if (slot < 0 || slot >= slots) { throw new IllegalArgumentException(error); }
        return handler.getStackInSlot(slot);
    }

    private static void putFluid(Map<String, Object> info, FluidStack fluid) {
        info.put("amount", fluid.getAmount());
        info.put("name", fluidName(fluid.getFluid()));
        info.put("label", fluid.getHoverName().getString());
    }
}
