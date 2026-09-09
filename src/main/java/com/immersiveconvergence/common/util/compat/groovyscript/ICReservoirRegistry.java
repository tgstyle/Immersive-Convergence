package com.immersiveconvergence.common.util.compat.groovyscript;

import com.immersiveconvergence.api.petroleum.ICPowerTiers;
import com.immersiveconvergence.api.petroleum.ICReservoirContent;
import com.immersiveconvergence.api.petroleum.ICReservoirData;
import com.immersiveconvergence.api.petroleum.ICReservoirHolder;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")
public class ICReservoirRegistry extends VirtualizedRegistry<Pair<PumpjackHandler.ReservoirType, Integer>> {

    public ICReservoirRegistry() {
        super(Collections.singletonList("reservoir"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(entry -> PumpjackHandler.reservoirList.entrySet().removeIf(existing -> existing.getKey() == entry.getKey()));
        restoreFromBackup().forEach(entry -> PumpjackHandler.reservoirList.put(entry.getKey(), entry.getValue()));
    }

    public void definePowerTier(int tier, int capacity, int usage) {
        if (tier < 0) {
            GroovyLog.msg("Error defining Immersive Convergence reservoir power tier").add("tier must not be negative, got " + tier).error().post();
            return;
        }
        if (capacity < 1 || usage < 1) {
            GroovyLog.msg("Error defining Immersive Convergence reservoir power tier " + tier).add("capacity and usage must both be at least 1, got " + capacity + " and " + usage).error().post();
            return;
        }
        ICPowerTiers.define(tier, capacity, usage);
    }

    public boolean remove(String name) {
        if (name == null || name.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Convergence reservoir").add("cannot remove a reservoir without a name").error().post();
            return false;
        }
        boolean removed = false;
        Iterator<Map.Entry<PumpjackHandler.ReservoirType, Integer>> entries = PumpjackHandler.reservoirList.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<PumpjackHandler.ReservoirType, Integer> entry = entries.next();
            if (name.equals(entry.getKey().name)) {
                addBackup(Pair.of(entry.getKey(), entry.getValue()));
                entries.remove();
                removed = true;
            }
        }
        return removed;
    }

    public void removeAll() {
        PumpjackHandler.reservoirList.forEach((type, weight) -> addBackup(Pair.of(type, weight)));
        PumpjackHandler.reservoirList.clear();
    }

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    public class RecipeBuilder extends AbstractRecipeBuilder<PumpjackHandler.ReservoirType> {

        private String fluid;
        private int minSize;
        private int maxSize;
        private int replenishRate;
        private int pumpSpeed = -1;
        private int weight = 1;
        private int powerTier;
        private float drainChance = 1.0F;
        private ICReservoirContent content = ICReservoirContent.LIQUID;
        private int[] dimensionWhitelist;
        private int[] dimensionBlacklist;
        private String[] biomeWhitelist;
        private String[] biomeBlacklist;

        public RecipeBuilder fluid(String fluid) { this.fluid = fluid; return this; }

        public RecipeBuilder size(int minSize, int maxSize) { this.minSize = minSize; this.maxSize = maxSize; return this; }

        public RecipeBuilder replenishRate(int replenishRate) { this.replenishRate = replenishRate; return this; }

        public RecipeBuilder pumpSpeed(int pumpSpeed) { this.pumpSpeed = pumpSpeed; return this; }

        public RecipeBuilder weight(int weight) { this.weight = weight; return this; }

        public RecipeBuilder powerTier(int powerTier) { this.powerTier = powerTier; return this; }

        public RecipeBuilder drainChance(float drainChance) { this.drainChance = drainChance; return this; }

        public RecipeBuilder content(String content) { this.content = ICReservoirContent.byName(content == null ? null : content.toUpperCase()); return this; }

        public RecipeBuilder dimensions(int... dimensions) { this.dimensionWhitelist = dimensions; return this; }

        public RecipeBuilder blacklistDimensions(int... dimensions) { this.dimensionBlacklist = dimensions; return this; }

        public RecipeBuilder biomes(String... biomes) { this.biomeWhitelist = biomes; return this; }

        public RecipeBuilder blacklistBiomes(String... biomes) { this.biomeBlacklist = biomes; return this; }

        @Override public String getErrorMsg() { return "Error adding Immersive Convergence reservoir"; }

        @Override
        public void validate(GroovyLog.Msg msg) {
            if (name == null) { msg.add("the reservoir needs a name"); }
            if (fluid == null || !FluidRegistry.isFluidRegistered(fluid)) { msg.add("the reservoir names a fluid that is not registered: " + fluid); }
            if (minSize < 1) { msg.add("the reservoir needs a minimum size of at least 1 mB, got " + minSize); }
            if (maxSize < minSize) { msg.add("the reservoir has a maximum size of " + maxSize + " below its minimum of " + minSize); }
            if (weight < 1) { msg.add("the reservoir needs a spawn weight of at least 1, got " + weight); }
            if (pumpSpeed < 1) { msg.add("the reservoir needs a pump speed of at least 1 mB/t, got " + pumpSpeed); }
            if (replenishRate > pumpSpeed) { msg.add("the reservoir replenishes at " + replenishRate + " mB/t, faster than its pump speed of " + pumpSpeed); }
            if (drainChance <= 0 || drainChance > 1) { msg.add("the reservoir needs a drain chance above 0 and no greater than 1, got " + drainChance); }
            if (powerTier < 0) { msg.add("the reservoir power tier must not be negative, got " + powerTier); }
        }

        @Override
        public PumpjackHandler.ReservoirType register() {
            if (!validate()) { return null; }
            PumpjackHandler.ReservoirType type = new PumpjackHandler.ReservoirType(name.getPath(), fluid, minSize, maxSize, replenishRate);
            if (dimensionWhitelist != null) { type.dimensionWhitelist = dimensionWhitelist; }
            if (dimensionBlacklist != null) { type.dimensionBlacklist = dimensionBlacklist; }
            if (biomeWhitelist != null) { type.biomeWhitelist = biomeWhitelist; }
            if (biomeBlacklist != null) { type.biomeBlacklist = biomeBlacklist; }
            PumpjackHandler.reservoirList.put(type, weight);
            ICReservoirData data = ICReservoirHolder.of(type);
            if (data != null) {
                data.pumpSpeed = pumpSpeed;
                data.powerTier = powerTier;
                data.drainChance = drainChance;
                data.content = content;
            }
            addScripted(Pair.of(type, weight));
            return type;
        }
    }
}
