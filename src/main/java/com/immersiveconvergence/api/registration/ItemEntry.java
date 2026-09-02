package com.immersiveconvergence.api.registration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record ItemEntry<T extends Item>(RegistryObject<T> regObject) implements Supplier<T>, ItemLike {
    @Override @Nonnull public T get() { return regObject.get(); }

    @Override @Nonnull public Item asItem() { return regObject.get(); }

    public ResourceLocation getId() { return regObject.getId(); }
}
