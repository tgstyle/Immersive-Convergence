package com.immersiveconvergence.api.fluid;

import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import javax.annotation.Nonnull;

public class BaseFluidBlock extends LiquidBlock {
    private static FluidEntry entryStatic;
    private final FluidEntry entry;

    public BaseFluidBlock(FluidEntry entry, Properties props) {
        super(entry.getStillGetter(), Util.make(props, $ -> entryStatic = entry));
        this.entry = entry;
        entryStatic = null;
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        for (Property<?> p : (entry == null ? entryStatic : entry).properties()) { builder.add(p); }
    }

    @Override @Nonnull public FluidState getFluidState(@Nonnull BlockState state) {
        FluidState baseState = super.getFluidState(state);
        for (Property<?> prop : baseState.getProperties()) {
            if (state.hasProperty(prop)) { baseState = withCopiedValue(prop, baseState, state); }
        }
        return baseState;
    }

    public static <T extends StateHolder<?, T>, S extends Comparable<S>> T withCopiedValue(Property<S> prop, T oldState, StateHolder<?, ?> copyFrom) { return oldState.setValue(prop, copyFrom.getValue(prop)); }
}