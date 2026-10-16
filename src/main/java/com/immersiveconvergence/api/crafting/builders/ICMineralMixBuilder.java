package com.immersiveconvergence.api.crafting.builders;

import com.google.gson.JsonArray;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class ICMineralMixBuilder extends ICIERecipeBuilder<ICMineralMixBuilder> {
    private ICMineralMixBuilder() { super("mineral_mix"); }

    public static ICMineralMixBuilder builder() { return new ICMineralMixBuilder(); }

    public ICMineralMixBuilder dimensionOverworld() {
        JsonArray predicate = new JsonArray();
        predicate.add(BiomeTags.IS_OVERWORLD.location().toString());
        array("biome_predicates").add(predicate);
        return this;
    }

    public ICMineralMixBuilder addOverworldSpoils() { return spoil(Items.GRAVEL, 0.2f).spoil(Items.COBBLESTONE, 0.5f).spoil(Items.COBBLED_DEEPSLATE, 0.3f); }

    public ICMineralMixBuilder spoil(ItemLike item, float chance) {
        array("spoils").add(chance(stack(item, 1), chance));
        return this;
    }

    public ICMineralMixBuilder ore(ItemLike item, float chance) {
        array("ores").add(chance(stack(item, 1), chance));
        return this;
    }

    public ICMineralMixBuilder ore(TagKey<Item> tag, float chance) {
        array("ores").add(chance(sized(tag, 1), chance));
        return this;
    }

    public ICMineralMixBuilder weight(int weight) {
        json.addProperty("weight", weight);
        return this;
    }

    public ICMineralMixBuilder failchance(float chance) {
        json.addProperty("fail_chance", chance);
        return this;
    }
}
