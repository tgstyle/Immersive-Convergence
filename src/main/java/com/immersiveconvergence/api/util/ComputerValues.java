package com.immersiveconvergence.api.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

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
        info.put("hasTag", fluid.hasTag());
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

    public static String fluidName(Fluid fluid) {
        ResourceLocation key = ForgeRegistries.FLUIDS.getKey(fluid);
        return key == null ? null : key.getPath();
    }

    public static ItemStack slot(IItemHandler handler, int slot, int slots, String error) {
        if (slot < 0 || slot >= slots) { throw new IllegalArgumentException(error); }
        return handler.getStackInSlot(slot);
    }

    private static void putFluid(Map<String, Object> info, FluidStack fluid) {
        info.put("amount", fluid.getAmount());
        info.put("name", fluidName(fluid.getFluid()));
        info.put("label", fluid.getDisplayName().getString());
    }
}
