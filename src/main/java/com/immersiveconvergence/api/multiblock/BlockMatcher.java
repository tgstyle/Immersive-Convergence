package com.immersiveconvergence.api.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@SuppressWarnings("unused")
public class BlockMatcher {
    private static final List<String> genericOreNames = new ArrayList<>();

    public static void addGenericOreNames(String... names) { Collections.addAll(genericOreNames, names); }

    public static boolean matches(IBlockState expected, IBlockState found) {
        if (expected == null || found == null) { return false; }
        if (expected.getBlock() == found.getBlock() && expected.getBlock().getMetaFromState(expected) == found.getBlock().getMetaFromState(found)) { return true; }
        ItemStack expectedStack = stackFromState(expected);
        ItemStack foundStack = stackFromState(found);
        if (expectedStack.isEmpty() || foundStack.isEmpty()) { return false; }
        for (String name : genericOreNames) {
            if (hasOreName(expectedStack, name) && hasOreName(foundStack, name)) { return true; }
        }
        return false;
    }

    public static String getGenericOreName(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        for (String name : genericOreNames) {
            if (hasOreName(stack, name)) { return name; }
        }
        return null;
    }

    public static ItemStack stackFromState(IBlockState state) {
        Item item = Item.getItemFromBlock(state.getBlock());
        if (item == Items.AIR) { return ItemStack.EMPTY; }
        return new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
    }

    private static boolean hasOreName(ItemStack stack, String name) {
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(id).equals(name)) { return true; }
        }
        return false;
    }
}
