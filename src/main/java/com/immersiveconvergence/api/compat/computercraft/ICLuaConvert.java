package com.immersiveconvergence.api.compat.computercraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ICLuaConvert {
    private ICLuaConvert() {}

    @Nullable public static Object[] toLua(@Nullable Object[] values) {
        return values == null ? null : convertAll(values);
    }

    private static Object[] convertAll(Object[] values) {
        Object[] converted = new Object[values.length];
        for (int i = 0; i < values.length; i++) { converted[i] = toLua(values[i]); }
        return converted;
    }

    @Nullable public static Object toLua(@Nullable Object value) {
        if (value instanceof FluidTankInfo) { return tank((FluidTankInfo)value); }
        if (value instanceof FluidStack) { return fluid((FluidStack)value, new HashMap<>()); }
        if (value instanceof ItemStack) { return item((ItemStack)value); }
        if (value instanceof Map) {
            Map<Object, Object> table = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>)value).entrySet()) { table.put(entry.getKey(), toLua(entry.getValue())); }
            return table;
        }
        if (value instanceof Collection) {
            List<Object> list = new ArrayList<>();
            for (Object element : (Collection<?>)value) { list.add(toLua(element)); }
            return list;
        }
        if (value instanceof Object[]) { return Arrays.asList(convertAll((Object[])value)); }
        return value;
    }

    private static Map<String, Object> tank(FluidTankInfo info) {
        Map<String, Object> table = new HashMap<>();
        table.put("capacity", info.capacity);
        if (info.fluid == null) { table.put("amount", 0); }
        else { fluid(info.fluid, table); }
        return table;
    }

    private static Map<String, Object> fluid(FluidStack fluid, Map<String, Object> table) {
        table.put("amount", fluid.amount);
        table.put("name", fluid.getFluid().getName());
        table.put("label", fluid.getLocalizedName());
        return table;
    }

    private static Map<String, Object> item(ItemStack stack) {
        Map<String, Object> table = new HashMap<>();
        if (stack.isEmpty()) { return table; }
        table.put("name", String.valueOf(stack.getItem().getRegistryName()));
        table.put("damage", stack.getItemDamage());
        table.put("size", stack.getCount());
        table.put("maxSize", stack.getMaxStackSize());
        table.put("label", stack.getDisplayName());
        return table;
    }
}
