package com.immersiveconvergence.common.blocks.logic;

import com.immersiveconvergence.api.capability.HeatCapabilities;
import com.immersiveconvergence.api.capability.IHeatProvider;
import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.core.registration.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;

public class HeatCreativeBlockEntity extends BaseBlockEntity {
    private final Provider heatProvider = new Provider();

    public HeatCreativeBlockEntity(BlockPos pos, BlockState state) { super(ICBlockEntities.HEAT_CREATIVE.get(), pos, state); }

    public IHeatProvider getHeatProvider(@Nullable Direction side) { return heatProvider; }

    private static class Provider implements IHeatProvider {
        @Override public double getHeatLevel() { return HeatCapabilities.MAX_HEAT; }
    }

    @Override public void readCustomNBT(CompoundTag nbt, boolean descPacket) {}

    @Override public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {}
}
