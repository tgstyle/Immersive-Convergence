package com.immersiveconvergence.api.registration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
    private final RegistryObject<T> regObject;
    private final Supplier<BlockBehaviour.Properties> properties;

    public BlockEntry(DeferredRegister<Block> register, String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
        this.properties = properties;
        this.regObject = register.register(name, () -> make.apply(properties.get()));
    }

    @Override public T get() { return regObject.get(); }

    public ResourceLocation getId() { return regObject.getId(); }

    public BlockBehaviour.Properties getProperties() { return properties.get(); }

    public RegistryObject<T> getRegObject() { return regObject; }

    @Override @Nonnull public Item asItem() { return get().asItem(); }
}
