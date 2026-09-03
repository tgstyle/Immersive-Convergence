package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.jei.MultiblockIngredient;

import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class MultiblockIngredientHelper implements IIngredientHelper<MultiblockIngredient> {

    @Override @Nullable public MultiblockIngredient getMatch(@Nonnull Iterable<MultiblockIngredient> iterable, @Nonnull MultiblockIngredient ingredient) {
        for (MultiblockIngredient candidate : iterable) {
            if (candidate.renderStack.isItemEqual(ingredient.renderStack)) { return candidate; }
        }
        return null;
    }

    @Override @Nonnull public String getDisplayName(@Nonnull MultiblockIngredient ingredient) { return ingredient.renderStack.getDisplayName(); }

    @Override @Nonnull public String getUniqueId(@Nonnull MultiblockIngredient ingredient) { return ingredient.renderStack.getTranslationKey() + ingredient.renderStack.getMetadata(); }

    @Override @Nonnull public String getWildcardId(@Nonnull MultiblockIngredient ingredient) { return getUniqueId(ingredient); }

    @Override @Nonnull public String getModId(@Nonnull MultiblockIngredient ingredient) { return Objects.requireNonNull(ingredient.renderStack.getItem().getRegistryName()).getNamespace(); }

    @Override @Nonnull public String getResourceId(@Nonnull MultiblockIngredient ingredient) { return Objects.requireNonNull(ingredient.renderStack.getItem().getRegistryName()).toString(); }

    @Override @Nonnull public ItemStack getCheatItemStack(@Nonnull MultiblockIngredient ingredient) { return ItemStack.EMPTY; }

    @Override @Nonnull public MultiblockIngredient copyIngredient(@Nonnull MultiblockIngredient ingredient) { return ingredient; }

    @Override @Nonnull public String getErrorInfo(@Nullable MultiblockIngredient ingredient) { return ingredient == null ? "MultiblockIngredient is not supposed to be null!" : ""; }
}
