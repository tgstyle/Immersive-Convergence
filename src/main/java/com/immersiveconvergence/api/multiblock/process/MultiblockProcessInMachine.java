package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICIngredientWithSize;
import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.process.ProcessContext.ProcessContextInMachine;
import com.immersiveconvergence.api.multiblock.util.IngredientUtils;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class MultiblockProcessInMachine<R extends ICMultiblockRecipe> extends MultiblockProcess<R, ProcessContextInMachine<R>> {
    protected final int[] inputSlots;
    protected int[] inputAmounts = null;
    protected int[] inputTanks = new int[0];

    public MultiblockProcessInMachine(RecipeHolder<R> recipe, int... inputSlots) {
        super(recipe);
        this.inputSlots = inputSlots;
    }

    public MultiblockProcessInMachine(BiFunction<Level, ResourceLocation, R> getRecipe, CompoundTag data) {
        super(getRecipe, data);
        this.inputSlots = data.getIntArray("process_inputSlots");
        setInputAmounts(data.getIntArray("process_inputAmounts"));
        setInputTanks(data.getIntArray("process_inputTanks"));
    }

    public void setInputTanks(int... inputTanks) { this.inputTanks = inputTanks; }

    public void setInputAmounts(int... inputAmounts) { this.inputAmounts = inputAmounts; }

    protected List<ICIngredientWithSize> getRecipeItemInputs(Level level) {
        R recipe = getLevelData(level).recipe();
        return recipe == null ? List.of() : recipe.getItemInputs();
    }

    protected List<SizedFluidIngredient> getRecipeFluidInputs(Level level) {
        R recipe = getLevelData(level).recipe();
        return recipe == null ? List.of() : recipe.getFluidInputs();
    }

    private static int findOutputSlot(ProcessContextInMachine<?> context, ItemStack output) {
        IItemHandlerModifiable inv = context.getInventory();
        for (int slot : context.getOutputSlots()) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s.isEmpty()) { return slot; }
            if (ItemStack.isSameItemSameComponents(s, output) && s.getCount() + output.getCount() <= inv.getSlotLimit(slot)) { return slot; }
        }
        return -1;
    }

    private static int findOutputTank(ProcessContextInMachine<?> context, FluidStack output) {
        IFluidTank[] tanks = context.getInternalTanks();
        for (int tank : context.getOutputTanks()) {
            if (tanks[tank].fill(output, FluidAction.SIMULATE) == output.getAmount()) { return tank; }
        }
        return -1;
    }

    @Override protected boolean canOutputItem(ProcessContextInMachine<R> context, ItemStack output) { return findOutputSlot(context, output) >= 0; }

    @Override protected boolean canOutputFluid(ProcessContextInMachine<R> context, FluidStack output) { return findOutputTank(context, output) >= 0; }

    @Override protected void outputFluid(ProcessContextInMachine<R> context, FluidStack output) {
        int tank = findOutputTank(context, output);
        if (tank >= 0) { context.getInternalTanks()[tank].fill(output, FluidAction.EXECUTE); }
    }

    @Override protected void outputItem(ProcessContextInMachine<R> context, ItemStack output) {
        int slot = findOutputSlot(context, output);
        if (slot < 0) { return; }
        IItemHandlerModifiable inv = context.getInventory();
        ItemStack s = inv.getStackInSlot(slot);
        if (s.isEmpty()) { inv.setStackInSlot(slot, output.copy()); }
        else { s.grow(output.getCount()); }
    }

    @Override public void doProcessTick(ProcessContextInMachine<R> context, IMultiblockLevel level) {
        R recipe = getLevelData(level.getRawLevel()).recipe();
        if (recipe == null) { return; }
        IItemHandlerModifiable inv = context.getInventory();
        if (recipe.getItemInputs() != null && inv != null) {
            NonNullList<ItemStack> query = NonNullList.withSize(inputSlots.length, ItemStack.EMPTY);
            for (int i = 0; i < inputSlots.length; i++) {
                if (inputSlots[i] >= 0 && inputSlots[i] < inv.getSlots()) { query.set(i, context.getInventory().getStackInSlot(inputSlots[i])); }
            }
            if (!IngredientUtils.stacksMatchIngredientWithSizeList(recipe.getItemInputs(), query)) {
                this.clearProcess = true;
                return;
            }
        }
        super.doProcessTick(context, level);
    }

    @Override protected void processFinish(ProcessContextInMachine<R> context, IMultiblockLevel level) {
        super.processFinish(context, level);
        IItemHandlerModifiable inv = context.getInventory();
        List<ICIngredientWithSize> itemInputList = this.getRecipeItemInputs(level.getRawLevel());
        if (inv != null && itemInputList != null) {
            if (this.inputAmounts != null && this.inputSlots.length == this.inputAmounts.length) {
                for (int i = 0; i < this.inputSlots.length; i++) {
                    if (this.inputAmounts[i] > 0) { inv.getStackInSlot(this.inputSlots[i]).shrink(this.inputAmounts[i]); }
                }
            }
            else {
                for (ICIngredientWithSize ingr : new ArrayList<>(itemInputList)) {
                    int ingrSize = ingr.getCount();
                    for (int slot : this.inputSlots) {
                        if (!inv.getStackInSlot(slot).isEmpty() && ingr.test(inv.getStackInSlot(slot))) {
                            int taken = Math.min(inv.getStackInSlot(slot).getCount(), ingrSize);
                            inv.getStackInSlot(slot).shrink(taken);
                            if (inv.getStackInSlot(slot).getCount() <= 0) { inv.setStackInSlot(slot, ItemStack.EMPTY); }
                            if ((ingrSize -= taken) <= 0) { break; }
                        }
                    }
                }
            }
        }
        IFluidTank[] tanks = context.getInternalTanks();
        List<SizedFluidIngredient> fluidInputList = this.getRecipeFluidInputs(level.getRawLevel());
        if (tanks != null && fluidInputList != null) {
            for (SizedFluidIngredient ingr : new ArrayList<>(fluidInputList)) {
                int ingrSize = ingr.amount();
                for (int tank : this.inputTanks) {
                    if (tanks[tank] != null && ingr.ingredient().test(tanks[tank].getFluid())) {
                        int taken = Math.min(tanks[tank].getFluidAmount(), ingrSize);
                        tanks[tank].drain(taken, FluidAction.EXECUTE);
                        if ((ingrSize -= taken) <= 0) { break; }
                    }
                }
            }
        }
    }

    @Override protected void writeExtraDataToNBT(CompoundTag nbt) {
        nbt.putIntArray("process_inputSlots", inputSlots);
        if (inputAmounts != null) { nbt.putIntArray("process_inputAmounts", inputAmounts); }
        nbt.putIntArray("process_inputTanks", inputTanks);
    }
}
