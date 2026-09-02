package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.registration.BlockEntry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public interface FluidRegisters {
    DeferredRegister<Fluid> fluids();

    DeferredRegister<FluidType> fluidTypes();

    DeferredRegister<Item> items();

    <T extends Block> BlockEntry<T> block(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make);
}
