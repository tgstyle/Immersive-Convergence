package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.util.ICUtils;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "unchecked"}) public class ICFinishedRecipe<R extends ICFinishedRecipe<R>> implements FinishedRecipe {
    private final ICRecipeSerializer<?> serializer;
    private final List<Consumer<JsonObject>> writerFunctions = new ArrayList<>();
    private ResourceLocation id;
    protected JsonArray inputArray = null;
    protected int inputCount = 0;
    protected int maxInputCount = 1;
    protected JsonArray resultArray = null;
    protected int resultCount = 0;
    protected int maxResultCount = 1;
    protected JsonArray conditions = null;

    protected ICFinishedRecipe(ICRecipeSerializer<?> serializer) { this.serializer = serializer; }

    protected boolean isComplete() { return true; }

    public void build(Consumer<FinishedRecipe> out, ResourceLocation id) {
        Preconditions.checkArgument(isComplete(), "This recipe is incomplete");
        this.id = id;
        out.accept(this);
    }

    public R addWriter(Consumer<JsonObject> writer) {
        Preconditions.checkArgument(id == null, "This recipe has already been finalized");
        writerFunctions.add(writer);
        return (R)this;
    }

    public R addCondition(ICondition condition) {
        if (conditions == null) {
            conditions = new JsonArray();
            addWriter(json -> json.add("conditions", conditions));
        }
        conditions.add(CraftingHelper.serialize(condition));
        return (R)this;
    }

    public R setTime(int time) { return addWriter(json -> json.addProperty("time", time)); }

    public R setEnergy(int energy) { return addWriter(json -> json.addProperty("energy", energy)); }

    public R setMultipleResults(int maxResultCount) {
        resultArray = new JsonArray();
        this.maxResultCount = maxResultCount;
        return addWriter(json -> json.add("results", resultArray));
    }

    public R addMultiResult(JsonElement obj) {
        Preconditions.checkArgument(maxResultCount > 1, "This recipe does not support multiple results");
        Preconditions.checkArgument(resultCount < maxResultCount, "Recipe can only have " + maxResultCount + " results");
        resultArray.add(obj);
        resultCount++;
        return (R)this;
    }

    public R addResult(ItemLike itemProvider) { return addResult(new ItemStack(itemProvider)); }

    public R addResult(ItemStack itemStack) { return resultArray != null ? addMultiResult(serializeItemStack(itemStack)) : addItem("result", itemStack); }

    public R addResult(Ingredient ingredient) { return resultArray != null ? addMultiResult(ingredient.toJson()) : addWriter(json -> json.add("result", ingredient.toJson())); }

    public R addResult(ICIngredientWithSize ingredient) { return resultArray != null ? addMultiResult(ingredient.serialize()) : addWriter(json -> json.add("result", ingredient.serialize())); }

    public R setUseInputArray(int maxInputCount, String key) {
        inputArray = new JsonArray();
        this.maxInputCount = maxInputCount;
        return addWriter(json -> json.add(key, inputArray));
    }

    public R setUseInputArray(int maxInputCount) { return setUseInputArray(maxInputCount, "inputs"); }

    public R addMultiInput(JsonElement obj) {
        Preconditions.checkArgument(maxInputCount > 1, "This recipe does not support multiple inputs");
        Preconditions.checkArgument(inputCount < maxInputCount, "Recipe can only have " + maxInputCount + " inputs");
        inputArray.add(obj);
        inputCount++;
        return (R)this;
    }

    public R addMultiInput(Ingredient ingredient) { return addMultiInput(ingredient.toJson()); }

    public R addMultiInput(ICIngredientWithSize ingredient) { return addMultiInput(ingredient.serialize()); }

    protected String generateSafeInputKey() {
        Preconditions.checkArgument(inputCount < maxInputCount, "Recipe can only have " + maxInputCount + " inputs");
        String key = maxInputCount == 1 ? "input" : "input" + inputCount;
        inputCount++;
        return key;
    }

    public R addInput(ItemLike... itemProviders) { return inputArray != null ? addMultiInput(Ingredient.of(itemProviders)) : addIngredient(generateSafeInputKey(), itemProviders); }

    public R addInput(ItemStack... itemStacks) { return inputArray != null ? addMultiInput(Ingredient.of(itemStacks)) : addIngredient(generateSafeInputKey(), itemStacks); }

    public R addInput(TagKey<Item> tag) { return inputArray != null ? addMultiInput(Ingredient.of(tag)) : addIngredient(generateSafeInputKey(), tag); }

    public R addInput(Ingredient input) { return inputArray != null ? addMultiInput(input) : addIngredient(generateSafeInputKey(), input); }

    public R addInput(ICIngredientWithSize input) { return inputArray != null ? addMultiInput(input) : addIngredient(generateSafeInputKey(), input); }

    public JsonObject serializeItemStack(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
        if (stack.getCount() > 1) { obj.addProperty("count", stack.getCount()); }
        if (stack.hasTag()) { obj.addProperty("nbt", String.valueOf(stack.getTag())); }
        return obj;
    }

    protected R addSimpleItem(String key, ItemLike item) { return addWriter(json -> json.addProperty(key, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.asItem())).toString())); }

    public R addItem(String key, ItemLike item) { return addItem(key, new ItemStack(item)); }

    public R addItem(String key, ItemStack stack) {
        Preconditions.checkArgument(!stack.isEmpty(), "May not add empty ItemStack to recipe");
        return addWriter(json -> json.add(key, serializeItemStack(stack)));
    }

    public R addIngredient(String key, ItemLike... itemProviders) { return addIngredient(key, Ingredient.of(itemProviders)); }

    public R addIngredient(String key, ItemStack... itemStacks) { return addIngredient(key, Ingredient.of(itemStacks)); }

    public R addIngredient(String key, TagKey<Item> tag) { return addIngredient(key, Ingredient.of(tag)); }

    public R addIngredient(String key, Ingredient ingredient) { return addWriter(json -> json.add(key, ingredient.toJson())); }

    public R addIngredient(String key, ICIngredientWithSize ingredient) { return addWriter(json -> json.add(key, ingredient.serialize())); }

    public R addFluid(String key, FluidStack fluidStack) { return addWriter(json -> json.add(key, ICUtils.jsonSerializeFluidStack(fluidStack))); }

    public R addFluid(FluidStack fluidStack) { return addFluid("fluid", fluidStack); }

    public R addFluid(Fluid fluid, int amount) { return addFluid("fluid", new FluidStack(fluid, amount)); }

    public R addFluidTag(String key, ICFluidTagInput fluidTag) { return addWriter(json -> json.add(key, fluidTag.serialize())); }

    public R addFluidTag(String key, TagKey<Fluid> fluidTag, int amount) { return addFluidTag(key, new ICFluidTagInput(fluidTag, amount, null)); }

    public R addFluidTag(TagKey<Fluid> fluidTag, int amount) { return addFluidTag("fluid", new ICFluidTagInput(fluidTag, amount, null)); }

    @Override public void serializeRecipeData(@Nonnull JsonObject json) {
        for (Consumer<JsonObject> writer : writerFunctions) { writer.accept(json); }
    }

    @Override @Nonnull public ResourceLocation getId() { return id; }

    @Override @Nonnull public RecipeSerializer<?> getType() { return serializer; }

    @Nullable @Override public JsonObject serializeAdvancement() { return null; }

    @Nullable @Override public ResourceLocation getAdvancementId() { return null; }

    protected static JsonObject serializeStackWithChance(ICIngredientWithSize ingredient, float chance, ICondition... conditions) {
        JsonObject json = new JsonObject();
        json.addProperty("chance", chance);
        json.add("output", ingredient.serialize());
        if (conditions.length > 0) {
            JsonArray conditionArray = new JsonArray();
            for (ICondition condition : conditions) { conditionArray.add(CraftingHelper.serialize(condition)); }
            json.add("conditions", conditionArray);
        }
        return json;
    }
}
