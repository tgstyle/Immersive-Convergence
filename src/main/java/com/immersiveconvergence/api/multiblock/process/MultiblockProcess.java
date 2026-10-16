package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

public abstract class MultiblockProcess<R extends ICMultiblockRecipe, CTX extends ProcessContext<R>> {
    private final ResourceLocation recipeId;
    private final BiFunction<Level, ResourceLocation, R> getRecipe;
    public int processTick;
    private LevelDependentData<R> levelData;
    public boolean clearProcess = false;
    private int extraProcessTicks = -1;

    public MultiblockProcess(ResourceLocation recipeId, BiFunction<Level, ResourceLocation, R> getRecipe) {
        this.recipeId = recipeId;
        this.getRecipe = getRecipe;
        this.processTick = 0;
    }

    public MultiblockProcess(RecipeHolder<R> recipe) {
        this.recipeId = recipe.id();
        this.getRecipe = ($, $1) -> { throw new RuntimeException("A process initialized with a recipe should never query recipes"); };
        this.processTick = 0;
        populateLevelData(recipe.value());
    }

    public MultiblockProcess(BiFunction<Level, ResourceLocation, R> getRecipe, CompoundTag data) {
        this(ResourceLocation.parse(data.getString("recipe")), getRecipe);
        this.processTick = data.getInt("process_processTick");
    }

    protected List<ItemStack> getRecipeItemOutputs(Level level) {
        R recipe = getLevelData(level).recipe;
        if (recipe == null) { return List.of(); }
        return recipe.getItemOutputs();
    }

    protected List<FluidStack> getRecipeFluidOutputs(Level level) {
        R recipe = getLevelData(level).recipe;
        if (recipe == null) { return List.of(); }
        return recipe.getFluidOutputs();
    }

    public boolean canProcess(CTX context, Level level) {
        LevelDependentData<R> levelData = getLevelData(level);
        if (levelData.recipe == null) { return true; }
        if (context.getEnergy().extractEnergy(levelData.energyPerTick, true) == levelData.energyPerTick) {
            for (ItemStack output : getRecipeItemOutputs(level)) {
                if (!output.isEmpty() && !canOutputItem(context, output)) { return false; }
            }
            List<FluidStack> fluidOutputs = levelData.recipe.getFluidOutputs();
            if (fluidOutputs != null) {
                for (FluidStack output : fluidOutputs) {
                    if (!canOutputFluid(context, output)) { return false; }
                }
            }
            return true;
        }
        return false;
    }

    public void doProcessTick(CTX context, IMultiblockLevel level) {
        final Level rawLevel = level.getRawLevel();
        LevelDependentData<R> levelData = getLevelData(rawLevel);
        if (levelData.recipe == null) {
            this.clearProcess = true;
            return;
        }
        context.getEnergy().extractEnergy(levelData.energyPerTick, false);
        this.processTick += 1;

        if (extraProcessTicks < 0) { extraProcessTicks = levelData.recipe.getMultipleProcessTicks(); }

        if (extraProcessTicks >= 0 && levelData.energyPerTick > 0) {
            int averageInsertion = context.getEnergy().getAverageInsertion();
            int averageExtraction = context.getEnergy().getAverageExtraction();
            if (averageInsertion < averageExtraction) { extraProcessTicks = Math.max(0, extraProcessTicks - 1); }
            else if (averageInsertion > averageExtraction) { extraProcessTicks = Math.min(levelData.recipe.getMultipleProcessTicks(), extraProcessTicks + 1); }

            int possibleTicks = Math.min(
                    Math.min(
                            extraProcessTicks,
                            levelData.maxTicks - this.processTick
                    ),
                    Math.min(
                            averageInsertion / levelData.energyPerTick,
                            context.getEnergy().getEnergyStored() / levelData.energyPerTick
                    )
            );
            if (possibleTicks > 0) {
                context.getEnergy().extractEnergy(levelData.energyPerTick * possibleTicks, false);
                this.processTick += possibleTicks;
            }
        }

        if (this.processTick >= levelData.maxTicks) { this.processFinish(context, level); }
    }

    protected void processFinish(CTX context, IMultiblockLevel level) {
        final Level rawLevel = level.getRawLevel();
        for (ItemStack output : getRecipeItemOutputs(rawLevel)) { outputItem(context, output); }
        List<FluidStack> fluidOutputs = getRecipeFluidOutputs(rawLevel);
        if (fluidOutputs != null) {
            for (FluidStack output : fluidOutputs) { outputFluid(context, output); }
        }
        this.clearProcess = true;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("recipe", recipeId.toString());
        tag.putInt("process_processTick", processTick);
        writeExtraDataToNBT(tag);
        return tag;
    }

    protected abstract void writeExtraDataToNBT(CompoundTag nbt);

    protected abstract boolean canOutputItem(CTX context, ItemStack output);

    protected abstract boolean canOutputFluid(CTX context, FluidStack output);

    protected abstract void outputItem(CTX context, ItemStack output);

    protected abstract void outputFluid(CTX context, FluidStack output);

    protected LevelDependentData<R> getLevelData(Level level) {
        if (levelData == null) { populateLevelData(getRecipe.apply(level, recipeId)); }
        return levelData;
    }

    private void populateLevelData(R recipe) {
        if (recipe != null) {
            int maxTicks = recipe.getTotalProcessTime();
            int energyPerTick = recipe.getTotalProcessEnergy() / maxTicks;
            this.levelData = new LevelDependentData<>(recipe, maxTicks, energyPerTick);
        }
        else { this.levelData = new LevelDependentData<>(null, 20, 0); }
    }

    public ResourceLocation getRecipeId() { return recipeId; }

    public int getMaxTicks(Level level) { return getLevelData(level).maxTicks; }

    @Nullable public R getRecipe(Level level) { return getLevelData(level).recipe; }

    protected record LevelDependentData<R extends ICMultiblockRecipe>(@Nullable R recipe, int maxTicks, int energyPerTick) {}
}
