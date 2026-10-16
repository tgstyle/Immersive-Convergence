package com.immersiveconvergence.api.multiblock.util;

import com.immersiveconvergence.api.crafting.ICIngredientWithSize;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class IngredientUtils {
    private IngredientUtils() {}

    public static boolean stacksMatchIngredientWithSizeList(List<ICIngredientWithSize> list, NonNullList<ItemStack> stacks) {
        List<ItemStack> queryList = new ArrayList<>(stacks.size());
        for (ItemStack s : stacks) {
            if (!s.isEmpty()) { queryList.add(s.copy()); }
        }
        for (ICIngredientWithSize ingr : list) {
            if (ingr == null) { continue; }
            int amount = ingr.getCount();
            Iterator<ItemStack> it = queryList.iterator();
            while (it.hasNext()) {
                ItemStack query = it.next();
                if (query.isEmpty()) { continue; }
                if (ingr.testIgnoringSize(query)) {
                    if (query.getCount() > amount) {
                        query.shrink(amount);
                        amount = 0;
                    }
                    else {
                        amount -= query.getCount();
                        query.setCount(0);
                    }
                }
                if (query.getCount() <= 0) { it.remove(); }
                if (amount <= 0) { break; }
            }
            if (amount > 0) { return false; }
        }
        return true;
    }
}
