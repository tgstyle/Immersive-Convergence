package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.util.ICUtils;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
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

@SuppressWarnings("unchecked") public class ICFinishedRecipe<R extends ICFinishedRecipe<R>> implements FinishedRecipe {
    private final ICRecipeSerializer<?> serializer;
    private final List<Consumer<JsonObject>> writerFunctions = new ArrayList<>();
    private ResourceLocation id;
    protected int inputCount = 0;
    protected int maxInputCount = 1;
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

    protected String generateSafeInputKey() {
        Preconditions.checkArgument(inputCount < maxInputCount, "Recipe can only have " + maxInputCount + " inputs");
        String key = maxInputCount == 1 ? "input" : "input" + inputCount;
        inputCount++;
        return key;
    }

    public R addInput(ItemLike... itemProviders) { return addIngredient(generateSafeInputKey(), itemProviders); }

    public R addInput(ItemStack... itemStacks) { return addIngredient(generateSafeInputKey(), itemStacks); }

    public R addInput(TagKey<Item> tag) { return addIngredient(generateSafeInputKey(), tag); }

    public R addInput(Ingredient input) { return addIngredient(generateSafeInputKey(), input); }

    public R addInput(ICIngredientWithSize input) { return addIngredient(generateSafeInputKey(), input); }

    public JsonObject serializeItemStack(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
        if (stack.getCount() > 1) { obj.addProperty("count", stack.getCount()); }
        if (stack.hasTag()) { obj.addProperty("nbt", String.valueOf(stack.getTag())); }
        return obj;
    }

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
}
