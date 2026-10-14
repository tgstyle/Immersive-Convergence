package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused") @EventBusSubscriber(modid = ICLib.MODID) public class ICCachedRecipeList<R extends Recipe<?>> {
    private static final int INVALID_RELOAD_COUNT = -1;
    private static int reloadCount = 0;
    private final ICRecipeType<R> type;
    private Map<ResourceLocation, R> recipes;
    private boolean cachedDataIsClient;
    private int cachedAtReloadCount = INVALID_RELOAD_COUNT;

    public ICCachedRecipeList(ICRecipeType<R> type) { this.type = type; }

    @SubscribeEvent public static void onTagsUpdated(TagsUpdatedEvent ev) { ++reloadCount; }

    @SubscribeEvent public static void onRecipeUpdatedClient(RecipesUpdatedEvent ev) { ++reloadCount; }

    public static int reloadCount() { return reloadCount; }

    public Collection<R> getRecipes(@Nonnull Level level) {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return Objects.requireNonNull(recipes).values();
    }

    public Collection<ResourceLocation> getRecipeNames(@Nonnull Level level) {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return Objects.requireNonNull(recipes).keySet();
    }

    public R getById(@Nonnull Level level, ResourceLocation name) {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return recipes.get(name);
    }

    private void updateCache(RecipeManager manager, boolean isClient) {
        if (recipes != null && cachedAtReloadCount == reloadCount && (!cachedDataIsClient || isClient)) { return; }
        recipes = manager.getRecipes().stream().filter(recipe -> recipe.getType() == type.get()).map(type.recipeClass()::cast).collect(Collectors.toMap(R::getId, Function.identity()));
        cachedDataIsClient = isClient;
        cachedAtReloadCount = reloadCount;
    }
}
