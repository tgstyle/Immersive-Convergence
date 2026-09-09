package com.immersiveconvergence.common.util.compat.crafttweaker;

import com.immersiveconvergence.api.petroleum.ICPowerTiers;
import com.immersiveconvergence.api.petroleum.ICReservoirHolder;
import com.immersiveconvergence.api.petroleum.ICReservoirContent;
import com.immersiveconvergence.api.petroleum.ICReservoirData;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.liquid.ILiquidStack;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import net.minecraftforge.fluids.FluidRegistry;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@ZenClass("mods.immersiveconvergence.Reservoir")
public class Reservoir {

    @ZenMethod
    public static void definePowerTier(int tier, int capacity, int usage) {
        if (tier < 0) { CraftTweakerAPI.logError("Reservoir power tier must not be negative"); return; }
        if (capacity < 1 || usage < 1) { CraftTweakerAPI.logError("Reservoir power tier " + tier + " needs a capacity and a usage of at least 1"); return; }
        CraftTweakerAPI.apply(new DefineTier(tier, capacity, usage));
    }

    @ZenMethod
    public static void add(String name, ILiquidStack fluid, int minSize, int maxSize, int replenishRate, int pumpSpeed, int weight, int powerTier,
                           @Optional(valueDouble = 1.0D) double drainChance, @Optional String content,
                           @Optional int[] dimensionWhitelist, @Optional int[] dimensionBlacklist,
                           @Optional String[] biomeWhitelist, @Optional String[] biomeBlacklist) {
        List<String> problems = new ArrayList<>();
        if (name == null || name.isEmpty()) { problems.add("needs a name"); }
        if (fluid == null || FluidRegistry.getFluid(fluid.getName()) == null) { problems.add("names a fluid that is not registered"); }
        if (minSize < 1) { problems.add("needs a minimum size of at least 1 mB"); }
        if (maxSize < minSize) { problems.add("has a maximum size below its minimum"); }
        if (weight < 1) { problems.add("needs a spawn weight of at least 1"); }
        if (pumpSpeed < 1) { problems.add("needs a pump speed of at least 1 mB/t"); }
        if (replenishRate > pumpSpeed) { problems.add("replenishes faster than it can be pumped"); }
        if (drainChance <= 0 || drainChance > 1) { problems.add("needs a drain chance above 0 and no greater than 1"); }
        if (!problems.isEmpty()) {
            CraftTweakerAPI.logError("Reservoir '" + name + "' " + String.join(", ", problems));
            return;
        }
        CraftTweakerAPI.apply(new Add(name, fluid.getName(), minSize, maxSize, replenishRate, pumpSpeed, weight, powerTier,
                (float)drainChance, ICReservoirContent.byName(content == null ? ICReservoirContent.LIQUID.name() : content.toUpperCase()),
                dimensionWhitelist, dimensionBlacklist, biomeWhitelist, biomeBlacklist));
    }

    @ZenMethod
    public static void remove(String name) {
        if (name == null || name.isEmpty()) { CraftTweakerAPI.logError("Cannot remove a reservoir without a name"); return; }
        CraftTweakerAPI.apply(new Remove(name));
    }

    private static class DefineTier implements IAction {
        private final int tier;
        private final int capacity;
        private final int usage;

        DefineTier(int tier, int capacity, int usage) {
            this.tier = tier;
            this.capacity = capacity;
            this.usage = usage;
        }

        @Override public void apply() { ICPowerTiers.define(tier, capacity, usage); }

        @Override public String describe() { return "Defining reservoir power tier " + tier + " as " + capacity + " IF at " + usage + " IF/t"; }
    }

    private static class Add implements IAction {
        private final String name;
        private final String fluid;
        private final int minSize;
        private final int maxSize;
        private final int replenishRate;
        private final int pumpSpeed;
        private final int weight;
        private final int powerTier;
        private final float drainChance;
        private final ICReservoirContent content;
        private final int[] dimensionWhitelist;
        private final int[] dimensionBlacklist;
        private final String[] biomeWhitelist;
        private final String[] biomeBlacklist;

        Add(String name, String fluid, int minSize, int maxSize, int replenishRate, int pumpSpeed, int weight, int powerTier, float drainChance,
            ICReservoirContent content, int[] dimensionWhitelist, int[] dimensionBlacklist, String[] biomeWhitelist, String[] biomeBlacklist) {
            this.name = name;
            this.fluid = fluid;
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.replenishRate = replenishRate;
            this.pumpSpeed = pumpSpeed;
            this.weight = weight;
            this.powerTier = powerTier;
            this.drainChance = drainChance;
            this.content = content;
            this.dimensionWhitelist = dimensionWhitelist;
            this.dimensionBlacklist = dimensionBlacklist;
            this.biomeWhitelist = biomeWhitelist;
            this.biomeBlacklist = biomeBlacklist;
        }

        @Override public void apply() {
            PumpjackHandler.ReservoirType type = new PumpjackHandler.ReservoirType(name, fluid, minSize, maxSize, replenishRate);
            if (dimensionWhitelist != null) { type.dimensionWhitelist = dimensionWhitelist; }
            if (dimensionBlacklist != null) { type.dimensionBlacklist = dimensionBlacklist; }
            if (biomeWhitelist != null) { type.biomeWhitelist = biomeWhitelist; }
            if (biomeBlacklist != null) { type.biomeBlacklist = biomeBlacklist; }
            PumpjackHandler.reservoirList.put(type, weight);
            ICReservoirData data = ICReservoirHolder.of(type);
            if (data == null) { return; }
            data.pumpSpeed = pumpSpeed;
            data.powerTier = powerTier;
            data.drainChance = drainChance;
            data.content = content;
        }

        @Override public String describe() { return "Adding reservoir '" + name + "' of " + fluid + ", " + minSize + " to " + maxSize + " mB at " + pumpSpeed + " mB/t"; }
    }

    private static class Remove implements IAction {
        private final String name;

        Remove(String name) { this.name = name; }

        @Override public void apply() {
            Iterator<Map.Entry<PumpjackHandler.ReservoirType, Integer>> entries = PumpjackHandler.reservoirList.entrySet().iterator();
            while (entries.hasNext()) {
                if (name.equals(entries.next().getKey().name)) { entries.remove(); }
            }
        }

        @Override public String describe() { return "Removing reservoir '" + name + "'"; }
    }
}
