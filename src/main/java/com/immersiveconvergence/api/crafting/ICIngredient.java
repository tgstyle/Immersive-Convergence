package com.immersiveconvergence.api.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ICIngredient {
    public ItemStack stack = ItemStack.EMPTY;
    public List<ItemStack> stackList;
    public String oreName;
    public FluidStack fluid;
    public int inputSize = 1;
    public boolean useNBT;

    public ICIngredient(ItemStack stack) {
        this.stack = stack;
        this.inputSize = stack.getCount();
    }

    public ICIngredient(String oreName, int inputSize) {
        this.oreName = oreName;
        this.inputSize = inputSize;
    }

    public ICIngredient(String oreName) { this(oreName, 1); }

    public ICIngredient(List<ItemStack> stackList, int inputSize) {
        this.stackList = stackList;
        this.inputSize = inputSize;
    }

    public ICIngredient(List<ItemStack> stackList) { this(stackList, 1); }

    public ICIngredient(FluidStack fluid) { this.fluid = fluid; }

    public ICIngredient(ICIngredient other) {
        this.stack = other.stack;
        this.stackList = other.stackList;
        this.oreName = other.oreName;
        this.fluid = other.fluid;
        this.inputSize = other.inputSize;
        this.useNBT = other.useNBT;
    }

    @SuppressWarnings("unchecked")
    public static ICIngredient create(Object input) {
        if (input instanceof ICIngredient) { return (ICIngredient)input; }
        if (input instanceof ItemStack) { return new ICIngredient((ItemStack)input); }
        if (input instanceof Item) { return new ICIngredient(new ItemStack((Item)input)); }
        if (input instanceof Block) { return new ICIngredient(new ItemStack((Block)input)); }
        if (input instanceof Ingredient) { return new ICIngredient(Arrays.asList(((Ingredient)input).getMatchingStacks())); }
        if (input instanceof List) {
            List<?> list = (List<?>)input;
            if (list.isEmpty()) { return new ICIngredient(ItemStack.EMPTY); }
            if (list.get(0) instanceof ItemStack) { return new ICIngredient((List<ItemStack>)list); }
            if (list.get(0) instanceof String) { return new ICIngredient(oresOf((List<String>)list)); }
        }
        if (input instanceof ItemStack[]) { return new ICIngredient(Arrays.asList((ItemStack[])input)); }
        if (input instanceof String[]) { return new ICIngredient(oresOf(Arrays.asList((String[])input))); }
        if (input instanceof String) { return new ICIngredient((String)input); }
        if (input instanceof FluidStack) { return new ICIngredient((FluidStack)input); }
        throw new IllegalArgumentException("Recipe ingredients must be ItemStack, Item, Block, List<ItemStack>, String (ore dictionary name) or FluidStack; " + input + " is invalid");
    }

    private static List<ItemStack> oresOf(List<String> names) {
        List<ItemStack> ores = new ArrayList<>();
        for (String name : names) { ores.addAll(OreDictionary.getOres(name)); }
        return ores;
    }

    public ICIngredient setUseNBT(boolean useNBT) {
        this.useNBT = useNBT;
        return this;
    }

    private static boolean existingOreName(String name) { return OreDictionary.doesOreNameExist(name) && !OreDictionary.getOres(name).isEmpty(); }

    private static boolean matchesOreName(ItemStack stack, String oreName) {
        if (!existingOreName(oreName)) { return false; }
        for (ItemStack ore : OreDictionary.getOres(oreName)) {
            if (OreDictionary.itemMatches(ore, stack, false)) { return true; }
        }
        return false;
    }

    private static ItemStack copyWithAmount(ItemStack stack, int amount) {
        if (stack.isEmpty()) { return ItemStack.EMPTY; }
        ItemStack copy = stack.copy();
        copy.setCount(amount);
        return copy;
    }

    public boolean matches(Object input) {
        if (input == null) { return false; }
        if (input instanceof ICIngredient) { return this.equals(input) && this.inputSize <= ((ICIngredient)input).inputSize; }
        if (input instanceof ItemStack) { return matchesItemStack((ItemStack)input); }
        if (input instanceof ItemStack[]) {
            for (ItemStack candidate : (ItemStack[])input) {
                if (matchesItemStack(candidate)) { return true; }
            }
            return false;
        }
        if (input instanceof List) {
            for (Object candidate : (List<?>)input) {
                if (this.matches(candidate)) { return true; }
            }
            return false;
        }
        if (input instanceof String) {
            if (this.oreName != null) { return this.oreName.equals(input); }
            return matchesOreName(stack, (String)input);
        }
        return false;
    }

    public ICIngredient copyWithSize(int size) {
        ICIngredient copy = new ICIngredient(this);
        copy.inputSize = size;
        return copy;
    }

    public ICIngredient copyWithMultipliedSize(double multiplier) { return copyWithSize((int)Math.floor(this.inputSize * multiplier)); }

    private static boolean hasUniversalBucket() { return ForgeModContainer.getInstance().universalBucket != null; }

    @SuppressWarnings("deprecation")
    private static ItemStack filledBucket(FluidStack fluid) { return UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid.getFluid()); }

    public List<ItemStack> getStackList() {
        if (stackList != null) { return stackList; }
        if (oreName != null) { return OreDictionary.getOres(oreName); }
        if (fluid != null && hasUniversalBucket()) { return Collections.singletonList(filledBucket(fluid)); }
        return Collections.singletonList(stack);
    }

    public List<ItemStack> getSizedStackList() {
        if (oreName != null) {
            List<ItemStack> list = new ArrayList<>();
            for (ItemStack ore : OreDictionary.getOres(oreName)) { list.add(copyWithAmount(ore, inputSize)); }
            return list;
        }
        if (fluid != null && hasUniversalBucket()) { return Collections.singletonList(filledBucket(fluid)); }
        if (stackList != null) { return stackList; }
        return Collections.singletonList(copyWithAmount(stack, inputSize));
    }

    public ItemStack getRandomizedExampleStack(long rand) {
        ItemStack ret = stack;
        if (ret.isEmpty() && stackList != null && !stackList.isEmpty()) { ret = stackList.get((int)(rand / 20) % stackList.size()); }
        if (ret.isEmpty() && oreName != null) {
            List<ItemStack> ores = OreDictionary.getOres(oreName);
            if (ores != null && !ores.isEmpty()) { ret = ores.get((int)(rand / 20) % ores.size()); }
        }
        if (ret.isEmpty() && fluid != null && hasUniversalBucket()) { ret = filledBucket(fluid); }
        return ret;
    }

    public ItemStack getExampleStack() {
        ItemStack ret = stack;
        if (ret.isEmpty() && stackList != null && !stackList.isEmpty()) { ret = stackList.get(0); }
        if (ret.isEmpty() && oreName != null) {
            List<ItemStack> ores = OreDictionary.getOres(oreName);
            if (ores != null && !ores.isEmpty()) { ret = ores.get(0); }
        }
        if (ret.isEmpty() && fluid != null && hasUniversalBucket()) { ret = filledBucket(fluid); }
        return ret;
    }

    public Ingredient toRecipeIngredient() {
        Ingredient ret = stack != null ? Ingredient.fromStacks(stack) : null;
        if (ret == null && stackList != null && !stackList.isEmpty()) { ret = Ingredient.fromStacks(stackList.toArray(new ItemStack[0])); }
        if (ret == null && oreName != null) { ret = new OreIngredient(oreName); }
        if (ret == null && fluid != null && hasUniversalBucket()) { ret = new ICIngredientFluidStack(fluid); }
        return ret;
    }

    private boolean nbtMatches(ItemStack input) {
        if (!this.useNBT) { return true; }
        NBTTagCompound mine = this.stack.getTagCompound();
        NBTTagCompound theirs = input.getTagCompound();
        if (mine == null || theirs == null) { return mine == null && theirs == null; }
        return mine.equals(theirs);
    }

    public boolean matchesItemStack(ItemStack input) {
        if (input.isEmpty()) { return false; }
        if (this.fluid != null) {
            FluidStack contained = FluidUtil.getFluidContained(input);
            if (contained != null && contained.containsFluid(fluid)) { return true; }
        }
        if (this.oreName != null) { return matchesOreName(input, oreName) && this.inputSize <= input.getCount(); }
        if (this.stackList != null) {
            for (ItemStack candidate : this.stackList) {
                if (OreDictionary.itemMatches(candidate, input, false) && this.inputSize <= input.getCount()) { return true; }
            }
        }
        if (!OreDictionary.itemMatches(stack, input, false) || this.inputSize > input.getCount()) { return false; }
        return nbtMatches(input);
    }

    public boolean matchesItemStackIgnoringSize(ItemStack input) {
        if (input.isEmpty()) { return false; }
        if (this.fluid != null) {
            FluidStack contained = FluidUtil.getFluidContained(input);
            if (contained != null && contained.containsFluid(fluid)) { return true; }
        }
        if (this.oreName != null) { return matchesOreName(input, oreName); }
        if (this.stackList != null) {
            for (ItemStack candidate : this.stackList) {
                if (OreDictionary.itemMatches(candidate, input, false)) { return true; }
            }
        }
        if (!OreDictionary.itemMatches(stack, input, false)) { return false; }
        return nbtMatches(input);
    }

    @Override public boolean equals(Object object) {
        if (!(object instanceof ICIngredient)) { return false; }
        ICIngredient other = (ICIngredient)object;
        if (this.fluid != null && other.fluid != null) { return this.fluid.equals(other.fluid); }
        if (this.oreName != null && other.oreName != null) { return this.oreName.equals(other.oreName); }
        if (this.stackList != null && other.stackList != null) {
            for (ItemStack mine : this.stackList) {
                for (ItemStack theirs : other.stackList) {
                    if (OreDictionary.itemMatches(mine, theirs, false)) { return true; }
                }
            }
            return false;
        }
        if (this.stack.isEmpty() || other.stack.isEmpty()) { return false; }
        if (!OreDictionary.itemMatches(stack, other.stack, false)) { return false; }
        return nbtMatches(other.stack);
    }

    @Override public int hashCode() {
        if (fluid != null) { return fluid.hashCode(); }
        if (oreName != null) { return oreName.hashCode(); }
        return stack.isEmpty() ? 0 : stack.getItem().hashCode();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (this.fluid != null) {
            nbt.setString("fluid", FluidRegistry.getFluidName(fluid));
            nbt.setInteger("fluidAmount", fluid.amount);
            nbt.setInteger("nbtType", 3);
        }
        else if (this.oreName != null) {
            nbt.setString("oreName", oreName);
            nbt.setInteger("nbtType", 2);
        }
        else if (this.stackList != null) {
            NBTTagList list = new NBTTagList();
            for (ItemStack entry : stackList) {
                if (!entry.isEmpty()) { list.appendTag(entry.writeToNBT(new NBTTagCompound())); }
            }
            nbt.setTag("stackList", list);
            nbt.setInteger("nbtType", 1);
        }
        else {
            nbt.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
            nbt.setInteger("nbtType", 0);
            nbt.setBoolean("useNBT", useNBT);
        }
        nbt.setInteger("inputSize", inputSize);
        return nbt;
    }

    @Nullable public static ICIngredient readFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey("nbtType")) { return null; }
        switch (nbt.getInteger("nbtType")) {
            case 0: {
                ItemStack stack = new ItemStack(nbt.getCompoundTag("stack"));
                stack.setCount(nbt.getInteger("inputSize"));
                ICIngredient ingredient = new ICIngredient(stack);
                ingredient.useNBT = nbt.getBoolean("useNBT");
                return ingredient;
            }
            case 1: {
                NBTTagList list = nbt.getTagList("stackList", 10);
                List<ItemStack> stackList = new ArrayList<>();
                for (int i = 0; i < list.tagCount(); i++) { stackList.add(new ItemStack(list.getCompoundTagAt(i))); }
                return new ICIngredient(stackList, nbt.getInteger("inputSize"));
            }
            case 2: return new ICIngredient(nbt.getString("oreName"), nbt.getInteger("inputSize"));
            case 3: return new ICIngredient(new FluidStack(FluidRegistry.getFluid(nbt.getString("fluid")), nbt.getInteger("fluidAmount")));
            default: return null;
        }
    }
}
