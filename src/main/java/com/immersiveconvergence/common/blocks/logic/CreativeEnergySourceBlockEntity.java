package com.immersiveconvergence.common.blocks.logic;

import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.block.Enums;
import com.immersiveconvergence.api.block.IServerTickableBE;
import com.immersiveconvergence.api.multiblock.util.CapabilityUtils;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.common.blocks.CreativeEnergySourceBlock;
import com.immersiveconvergence.core.registration.ICBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Objects;

public class CreativeEnergySourceBlockEntity extends BaseBlockEntity implements IServerTickableBE, BlockInterfaces.IConfigurableSides {
    private final SideStorage[] storages = new SideStorage[DirectionUtils.VALUES.length];

    public CreativeEnergySourceBlockEntity(BlockPos pos, BlockState state) {
        super(Objects.requireNonNull(ICBlockEntities.CREATIVE_ENERGY_SOURCE).get(), pos, state);
        for (Direction side : DirectionUtils.VALUES) { storages[side.ordinal()] = new SideStorage(side); }
    }

    @Override public void tickServer() {
        for (Direction side : DirectionUtils.VALUES) {
            if (getSideConfig(side) != Enums.IOSideConfig.OUTPUT) { continue; }
            IEnergyStorage neighbor = CapabilityUtils.neighborEnergy(getLevelNonnull(), worldPosition, side);
            if (neighbor != null) { neighbor.receiveEnergy(Integer.MAX_VALUE, false); }
        }
    }

    @Override public Enums.IOSideConfig getSideConfig(Direction side) { return getBlockState().getValue(CreativeEnergySourceBlock.SIDES.get(side)); }

    @Override public boolean toggleSide(Direction side, Player player) {
        Level level = getLevelNonnull();
        if (!level.isClientSide) { level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CreativeEnergySourceBlock.SIDES.get(side), Enums.IOSideConfig.next(getSideConfig(side)))); }
        return true;
    }

    @Override public IEnergyStorage getEnergyStorage() { return storages[Direction.UP.ordinal()]; }

    @Nullable public IEnergyStorage getEnergyStorage(@Nullable Direction side) { return side == null || getSideConfig(side) == Enums.IOSideConfig.NONE ? null : storages[side.ordinal()]; }

    @Override public void readCustomNBT(CompoundTag nbt, boolean descPacket) {}

    @Override public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {}

    private class SideStorage implements IEnergyStorage {
        private final Direction side;

        SideStorage(Direction side) { this.side = side; }

        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return getSideConfig(side) == Enums.IOSideConfig.INPUT ? maxReceive : 0; }

        @Override public int extractEnergy(int maxExtract, boolean simulate) { return getSideConfig(side) == Enums.IOSideConfig.OUTPUT ? maxExtract : 0; }

        @Override public int getEnergyStored() { return Integer.MAX_VALUE; }

        @Override public int getMaxEnergyStored() { return Integer.MAX_VALUE; }

        @Override public boolean canExtract() { return getSideConfig(side) == Enums.IOSideConfig.OUTPUT; }

        @Override public boolean canReceive() { return getSideConfig(side) == Enums.IOSideConfig.INPUT; }
    }
}
