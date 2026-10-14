package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICIngredientWithSize;
import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.process.ProcessContext.ProcessContextInWorld;
import com.immersiveconvergence.api.multiblock.util.MultiblockItemUtils;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockProcessInWorld<R extends ICMultiblockRecipe> extends MultiblockProcess<R, ProcessContextInWorld<R>> {
    public NonNullList<ItemStack> inputItems;
    protected float transformationPoint;

    public MultiblockProcessInWorld(
            ResourceLocation recipeId, BiFunction<Level, ResourceLocation, R> getRecipe,
            float transformationPoint, NonNullList<ItemStack> inputItem
    ) {
        super(recipeId, getRecipe);
        this.inputItems = inputItem;
        this.transformationPoint = transformationPoint;
    }

    public MultiblockProcessInWorld(
            RecipeHolder<R> recipe, float transformationPoint, NonNullList<ItemStack> inputItem
    ) {
        super(recipe);
        this.inputItems = inputItem;
        this.transformationPoint = transformationPoint;
    }

    public MultiblockProcessInWorld(
            BiFunction<Level, ResourceLocation, R> getRecipe, CompoundTag data, Provider provider
    ) {
        super(getRecipe, data);
        this.inputItems = NonNullList.withSize(data.getInt("numInputs"), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(data, this.inputItems, provider);
        this.transformationPoint = data.getFloat("process_transformationPoint");
    }

    public MultiblockProcessInWorld(RecipeHolder<R> recipe, ItemStack input) { this(recipe, 0.5f, MultiblockItemUtils.createNonNullItemStackListFromItemStack(input)); }

    public List<ItemStack> getDisplayItem(Level level) {
        LevelDependentData<R> levelData = getLevelData(level);
        if (processTick / (float)levelData.maxTicks() > transformationPoint && levelData.recipe() != null) {
            List<ItemStack> list = levelData.recipe().getItemOutputs();
            if (!list.isEmpty()) { return list; }
        }
        return inputItems;
    }

    @Override public void writeExtraDataToNBT(CompoundTag nbt, Provider provider) {
        ContainerHelper.saveAllItems(nbt, inputItems, provider);
        nbt.putInt("numInputs", inputItems.size());
        nbt.putFloat("process_transformationPoint", transformationPoint);
    }

    @Override protected boolean canOutputItem(ProcessContextInWorld<R> context, ItemStack output) { return true; }

    @Override protected boolean canOutputFluid(ProcessContextInWorld<R> context, FluidStack output) { return false; }

    @Override protected void outputItem(ProcessContextInWorld<R> context, ItemStack output, IMultiblockLevel level) { context.doProcessOutput(output, level); }

    @Override protected void outputFluid(ProcessContextInWorld<R> context, FluidStack output) { context.doProcessFluidOutput(output); }

    @Override protected void processFinish(ProcessContextInWorld<R> context, IMultiblockLevel level) {
        super.processFinish(context, level);
        int size = -1;
        R recipe = getLevelData(level.getRawLevel()).recipe();
        if (recipe == null) { return; }
        final List<ICIngredientWithSize> itemInputs = recipe.getICItemInputs();
        if (itemInputs == null) { return; }
        for (ItemStack inputItem : this.inputItems) {
            for (ICIngredientWithSize s : itemInputs) {
                if (s.test(inputItem)) {
                    size = s.getCount();
                    break;
                }
            }
            if (size > 0 && inputItem.getCount() > size) {
                inputItem.split(size);
                processTick = 0;
                clearProcess = false;
            }
        }
    }
}
