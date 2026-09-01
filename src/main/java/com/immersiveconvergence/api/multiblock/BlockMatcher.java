package com.immersiveconvergence.api.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unused")
public class BlockMatcher {
    private static final Set<String> genericOreNames = new LinkedHashSet<>();
    private static final Set<Block> wildcardBlocks = new LinkedHashSet<>();
    private static final Random RANDOM = new Random();

    public static void addGenericOreNames(String... names) { Collections.addAll(genericOreNames, names); }

    public static void addWildcardBlocks(Block... blocks) { Collections.addAll(wildcardBlocks, blocks); }

    public static boolean matches(IBlockState expected, IBlockState found) {
        if (expected == null || found == null) { return false; }
        if (expected.getBlock() == found.getBlock() && expected.getBlock().getMetaFromState(expected) == found.getBlock().getMetaFromState(found)) { return true; }
        if (expected.getBlock() == found.getBlock() && wildcardBlocks.contains(expected.getBlock())) { return true; }
        if (genericOreNames.isEmpty()) { return false; }
        ItemStack expectedStack = stackFromState(expected);
        ItemStack foundStack = stackFromState(found);
        if (expectedStack.isEmpty() || foundStack.isEmpty()) { return false; }
        Set<String> expectedNames = oreNamesOf(expectedStack);
        if (expectedNames.isEmpty()) { return false; }
        Set<String> foundNames = oreNamesOf(foundStack);
        if (foundNames.isEmpty()) { return false; }
        for (String name : genericOreNames) {
            if (expectedNames.contains(name) && foundNames.contains(name)) { return true; }
        }
        return false;
    }

    public static String getGenericOreName(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        Set<String> names = oreNamesOf(stack);
        if (names.isEmpty()) { return null; }
        for (String name : genericOreNames) {
            if (names.contains(name)) { return name; }
        }
        return null;
    }

    private static Set<String> oreNamesOf(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ids.length == 0) { return Collections.emptySet(); }
        Set<String> names = new HashSet<>(ids.length);
        for (int id : ids) { names.add(OreDictionary.getOreName(id)); }
        return names;
    }

    public static ItemStack stackFromState(IBlockState state) {
        Block block = state.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (item == Items.AIR) { item = block.getItemDropped(state, RANDOM, 0); }
        if (item == Items.AIR) { return ItemStack.EMPTY; }
        return new ItemStack(item, 1, block.getMetaFromState(state));
    }

    public static boolean hasOreName(ItemStack stack, String name) {
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(id).equals(name)) { return true; }
        }
        return false;
    }
}
