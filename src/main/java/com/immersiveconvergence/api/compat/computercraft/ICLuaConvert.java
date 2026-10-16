package com.immersiveconvergence.api.compat.computercraft;

import com.immersiveconvergence.api.util.ComputerValues;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
import java.util.HashMap;
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
        if (value instanceof ItemStack stack) { return item(stack); }
        if (value instanceof FluidStack stack) { return fluid(stack); }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> table = new LinkedHashMap<>();
            map.forEach((key, entry) -> table.put(key, toLua(entry)));
            return table;
        }
        return value;
    }

    public static Map<String, Object> item(ItemStack stack) {
        Map<String, Object> table = new HashMap<>();
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        table.put("name", key == null ? null : key.toString());
        table.put("count", stack.getCount());
        table.put("damage", stack.getDamageValue());
        table.put("maxDamage", stack.getMaxDamage());
        return table;
    }

    public static Map<String, Object> fluid(FluidStack stack) {
        Map<String, Object> table = new HashMap<>();
        table.put("name", ComputerValues.fluidName(stack.getFluid()));
        table.put("amount", stack.getAmount());
        return table;
    }
}
