package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.registration.BlockEntry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public interface FluidRegisters {
    DeferredRegister<Fluid> fluids();

    DeferredRegister<FluidType> fluidTypes();

    DeferredRegister.Items items();

    <T extends Block> BlockEntry<T> block(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make);
}
