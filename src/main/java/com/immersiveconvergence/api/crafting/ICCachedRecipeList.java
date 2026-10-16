package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ICLib.MODID) public class ICCachedRecipeList<R extends Recipe<?>> {
    private static final int INVALID_RELOAD_COUNT = -1;
    private static int reloadCount = 0;
    private final ICRecipeType<R> type;
    private Map<ResourceLocation, RecipeHolder<R>> recipes;
    private List<RecipeHolder<R>> recipeHolders;
    private boolean cachedDataIsClient;
    private int cachedAtReloadCount = INVALID_RELOAD_COUNT;

    public ICCachedRecipeList(ICRecipeType<R> type) { this.type = type; }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onTagsUpdated(TagsUpdatedEvent ev) { ++reloadCount; }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onRecipeUpdatedClient(RecipesUpdatedEvent ev) { ++reloadCount; }

    public List<RecipeHolder<R>> getRecipes(@Nonnull Level level) {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return Objects.requireNonNull(recipeHolders);
    }

    @Nullable public RecipeHolder<R> findHolder(@Nonnull Level level, Predicate<R> matches, @Nullable RecipeHolder<R> hint) {
        if (hint != null && matches.test(hint.value())) { return hint; }
        for (RecipeHolder<R> holder : getRecipes(level)) {
            if (matches.test(holder.value())) { return holder; }
        }
        return null;
    }

    @Nullable public R find(@Nonnull Level level, Predicate<R> matches, @Nullable R hint) {
        if (hint != null && matches.test(hint)) { return hint; }
        RecipeHolder<R> holder = findHolder(level, matches, null);
        return holder != null ? holder.value() : null;
    }

    @Nullable public R getById(@Nonnull Level level, ResourceLocation name) {
        RecipeHolder<R> holder = holderById(level, name);
        return holder != null ? holder.value() : null;
    }

    @Nullable private RecipeHolder<R> holderById(@Nonnull Level level, ResourceLocation name) {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return recipes.get(name);
    }

    @SuppressWarnings("unchecked") private void updateCache(RecipeManager manager, boolean isClient) {
        if (recipes != null && cachedAtReloadCount == reloadCount && (!cachedDataIsClient || isClient)) { return; }
        recipes = manager.getRecipes().stream().filter(holder -> holder.value().getType() == type.get()).collect(Collectors.toMap(RecipeHolder::id, holder -> (RecipeHolder<R>) holder));
        recipeHolders = List.copyOf(recipes.values());
        cachedDataIsClient = isClient;
        cachedAtReloadCount = reloadCount;
    }
}
