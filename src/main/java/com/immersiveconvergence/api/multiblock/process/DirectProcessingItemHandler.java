package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.multiblock.process.ProcessContext.ProcessContextInWorld;
import com.immersiveconvergence.api.multiblock.util.InsertOnlyInventory;
import com.immersiveconvergence.api.multiblock.util.MultiblockItemUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class DirectProcessingItemHandler<R extends ICMultiblockRecipe> extends InsertOnlyInventory {
    private static final float TRANSFORMATION_POINT = .5f;
    private boolean doProcessStacking = false;
    protected final Supplier<Level> level;
    protected final MultiblockProcessor<R, ProcessContextInWorld<R>> processor;
    private final BiFunction<Level, ItemStack, @Nullable RecipeHolder<R>> getRecipeOnInsert;

    public DirectProcessingItemHandler(
            Supplier<Level> level,
            MultiblockProcessor<R, ProcessContextInWorld<R>> processor,
            BiFunction<Level, ItemStack, @Nullable RecipeHolder<R>> getRecipeOnInsert
    ) {
        this.level = level;
        this.processor = processor;
        this.getRecipeOnInsert = getRecipeOnInsert;
    }

    public DirectProcessingItemHandler<R> setProcessStacking(boolean stacking) {
        this.doProcessStacking = stacking;
        return this;
    }

    @Override protected ItemStack insert(ItemStack stack, boolean simulate) {
        stack = stack.copy();
        RecipeHolder<R> recipe = getRecipeOnInsert.apply(level.get(), stack);
        if (recipe == null) { return stack; }
        ItemStack displayStack;
        if (!doProcessStacking) {
            displayStack = recipe.value().getDisplayStack(stack);
            displayStack = stack.copyWithCount(displayStack.getCount());
        }
        else { displayStack = stack.copy(); }
        if (processor.addProcessToQueue(new MultiblockProcessInWorld<>(
                recipe,
                TRANSFORMATION_POINT,
                MultiblockItemUtils.createNonNullItemStackListFromItemStack(displayStack)
        ), level.get(), simulate, doProcessStacking))
            stack.shrink(displayStack.getCount());
        return stack;
    }
}
