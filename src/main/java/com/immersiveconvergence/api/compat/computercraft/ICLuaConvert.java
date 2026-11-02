package com.immersiveconvergence.api.compat.computercraft;

import com.immersiveconvergence.api.util.ComputerValues;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ICLuaConvert {
    private ICLuaConvert() {}

    public static Object[] toLua(@Nullable Object[] values) {
        if (values == null) { return new Object[0]; }
        Object[] converted = new Object[values.length];
        for (int i = 0; i < values.length; i++) { converted[i] = toLua(values[i]); }
        return converted;
    }

    @Nullable public static Object toLua(@Nullable Object value) {
        if (value instanceof ItemStack stack) { return ComputerValues.item(stack); }
        if (value instanceof FluidStack stack) { return ComputerValues.fluid(stack); }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> table = new LinkedHashMap<>();
            map.forEach((key, entry) -> table.put(key, toLua(entry)));
            return table;
        }
        return value;
    }

}
