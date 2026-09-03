package com.immersiveconvergence.api.jei;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MultiblockIngredient {
    public static final List<MultiblockIngredient> list = new ArrayList<>();

    public interface ModelRender { void render(int x, int y); }

    public final ItemStack renderStack;
    public final ModelRender modelRender;
    public final String[] catalystUids;

    public MultiblockIngredient(ItemStack renderStack, String... catalystUids) { this(renderStack, null, catalystUids); }

    public MultiblockIngredient(ItemStack renderStack, ModelRender modelRender, String... catalystUids) {
        this.renderStack = renderStack;
        this.modelRender = modelRender;
        this.catalystUids = catalystUids;
        list.add(this);
    }
}
