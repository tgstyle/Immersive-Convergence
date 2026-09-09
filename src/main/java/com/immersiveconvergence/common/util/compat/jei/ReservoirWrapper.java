package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.petroleum.ICPowerTier;
import com.immersiveconvergence.api.petroleum.ICPowerTiers;
import com.immersiveconvergence.api.petroleum.ICPumpjackHandler;
import com.immersiveconvergence.api.petroleum.ICReservoirData;
import com.immersiveconvergence.api.petroleum.ICReservoirHolder;
import com.immersiveconvergence.core.ICCommonConfig;

import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import flaxbeard.immersivepetroleum.common.Config;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservoirWrapper implements IRecipeWrapper, ITooltipCallback<FluidStack> {
    private static final NumberFormat NUMBERS = NumberFormat.getInstance();
    private static final int TEXT_X = 57;
    private static final int TEXT_WIDTH = 96;

    public final PumpjackHandler.ReservoirType reservoir;
    private final ICReservoirData data;
    private final int weight;
    private final int totalWeight;

    public ReservoirWrapper(PumpjackHandler.ReservoirType reservoir, int weight, int totalWeight) {
        this.reservoir = reservoir;
        this.data = ICReservoirHolder.of(reservoir);
        this.weight = weight;
        this.totalWeight = totalWeight;
    }

    private Fluid fluid() { return reservoir.getFluid(); }

    public int averageSize() { return (int)(((long)reservoir.maxSize + reservoir.minSize) / 2); }

    public int pumpSpeed() { return ICPumpjackHandler.pumpSpeedOf(data); }

    private float drainChance() { return data == null ? 1F : data.drainChance; }

    private String displayName() {
        String key = "desc.immersivepetroleum.info.reservoir." + reservoir.name;
        return I18n.hasKey(key) ? I18n.format(key) : reservoir.name;
    }

    @Override public void getIngredients(@Nonnull IIngredients ingredients) {
        if (fluid() == null) { return; }
        List<FluidStack> outputs = new ArrayList<>();
        outputs.add(new FluidStack(fluid(), averageSize()));
        outputs.add(new FluidStack(fluid(), Math.max(1, reservoir.replenishRate)));
        ingredients.setOutputs(VanillaTypes.FLUID, outputs);
    }

    @Override public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String name = minecraft.fontRenderer.trimStringToWidth(displayName(), TEXT_WIDTH);
        minecraft.fontRenderer.drawString(name, TEXT_X, 10, 0x404040, false);
        int y = 24;
        minecraft.fontRenderer.drawString(NUMBERS.format(reservoir.maxSize) + " mB", TEXT_X, y, 0x8B8B8B, false);
        y += 10;
        minecraft.fontRenderer.drawString(pumpSpeed() + " mB/t", TEXT_X, y, 0x8B8B8B, false);
        y += 10;
        if (ICCommonConfig.petroleum.jeiDrawPowerTier) {
            ICPowerTier tier = ICPowerTiers.get(data == null ? 0 : data.powerTier);
            minecraft.fontRenderer.drawString(NUMBERS.format(tier.getUsage()) + " IF/t", TEXT_X, y, 0x8B8B8B, false);
            y += 10;
        }
        if (ICCommonConfig.petroleum.jeiDrawSpawnWeight) {
            int chance = totalWeight > 0 ? Math.round(100F * weight / totalWeight) : 0;
            minecraft.fontRenderer.drawString(I18n.format(ICLib.DESC_INFO + "chance") + " " + chance + "%", TEXT_X, y, 0x8B8B8B, false);
        }
    }

    @Override @Nonnull public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX < TEXT_X || mouseX > TEXT_X + TEXT_WIDTH || mouseY < 8 || mouseY > 62) { return Collections.emptyList(); }
        List<String> lines = new ArrayList<>();
        lines.add(displayName());
        ICPowerTier tier = ICPowerTiers.get(data == null ? 0 : data.powerTier);
        lines.add(I18n.format(ICLib.DESC_INFO + "reservoir.powerTier", NUMBERS.format(tier.getUsage()), NUMBERS.format(tier.getCapacity())));
        lines.add(I18n.format(ICLib.DESC_INFO + "reservoir.weight", weight, totalWeight));
        if (drainChance() != 1F) { lines.add(I18n.format(ICLib.DESC_INFO + "reservoir.drainChance", Math.round(drainChance() * 100))); }
        if (Config.IPConfig.Extraction.req_pipes) { lines.add(I18n.format(ICLib.DESC_INFO + "reservoir.reqPipes")); }
        return lines;
    }

    @Override public void onTooltip(int slotIndex, boolean input, FluidStack ingredient, @Nonnull List<String> tooltip) {
        tooltip.clear();
        tooltip.add(ingredient.getLocalizedName());
        if (slotIndex == 0) {
            tooltip.add(I18n.format(ICLib.DESC_INFO + "reservoir.maxSize", NUMBERS.format(reservoir.maxSize)));
            tooltip.add(I18n.format(ICLib.DESC_INFO + "reservoir.minSize", NUMBERS.format(reservoir.minSize)));
            tooltip.add(I18n.format(ICLib.DESC_INFO + "reservoir.average", NUMBERS.format(averageSize())));
            return;
        }
        tooltip.add(I18n.format(ICLib.DESC_INFO + "reservoir.replenish", NUMBERS.format(reservoir.replenishRate)));
        tooltip.add(I18n.format(ICLib.DESC_INFO + "reservoir.pumpSpeed", NUMBERS.format(pumpSpeed())));
    }
}
