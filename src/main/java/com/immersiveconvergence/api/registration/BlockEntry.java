package com.immersiveconvergence.api.registration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
    private final DeferredBlock<T> regObject;
    private final Supplier<BlockBehaviour.Properties> properties;

    public BlockEntry(DeferredRegister.Blocks register, String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
        this.properties = properties;
        this.regObject = register.register(name, () -> make.apply(properties.get()));
    }

    @Override public T get() { return regObject.get(); }

    public ResourceLocation getId() { return regObject.getId(); }

    public BlockBehaviour.Properties getProperties() { return properties.get(); }

    public Supplier<T> getRegObject() { return regObject; }

    @Override @Nonnull public Item asItem() { return get().asItem(); }
}
