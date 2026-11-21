package com.immersiveconvergence.common.blocks.logic;

import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.block.Enums;
import com.immersiveconvergence.api.block.IServerTickableBE;
import com.immersiveconvergence.api.multiblock.util.CapabilityUtils;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.common.blocks.CreativeEnergySourceBlock;
import com.immersiveconvergence.core.registration.ICBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class CreativeEnergySourceBlockEntity extends BaseBlockEntity implements IServerTickableBE, BlockInterfaces.IConfigurableSides {
    private static final String SIDE_KEY = "sideConfig_";
    private final Map<Direction, Enums.IOSideConfig> sides = new EnumMap<>(Direction.class);
    private final Map<Direction, LazyOptional<IEnergyStorage>> capabilities = new EnumMap<>(Direction.class);

    public CreativeEnergySourceBlockEntity(BlockPos pos, BlockState state) {
        super(Objects.requireNonNull(ICBlockEntities.CREATIVE_ENERGY_SOURCE).get(), pos, state);
        for (Direction side : DirectionUtils.VALUES) {
            sides.put(side, state.getValue(CreativeEnergySourceBlock.sideProperty(side)));
            capabilities.put(side, CapabilityUtils.constantOptional(new SideEnergy(side)));
        }
    }

    @Override public void tickServer() {
        for (Direction side : DirectionUtils.VALUES) {
            if (sides.get(side) != Enums.IOSideConfig.OUTPUT) { continue; }
            IEnergyStorage target = CapabilityUtils.neighborEnergy(getLevelNonnull(), worldPosition, side);
            if (target != null) { target.receiveEnergy(Integer.MAX_VALUE, false); }
        }
    }

    @Override public Enums.IOSideConfig getSideConfig(Direction side) { return sides.get(side); }

    @Override public boolean toggleSide(Direction side, Player player) {
        if (getLevelNonnull().isClientSide) { return true; }
        sides.put(side, Enums.IOSideConfig.next(sides.get(side)));
        capabilities.get(side).invalidate();
        capabilities.put(side, CapabilityUtils.constantOptional(new SideEnergy(side)));
        setChanged();
        syncState();
        return true;
    }

    private void syncState() {
        if (level == null || level.isClientSide || isRemoved()) { return; }
        BlockState state = getBlockState();
        BlockState wanted = state;
        for (Direction side : DirectionUtils.VALUES) { wanted = wanted.setValue(CreativeEnergySourceBlock.sideProperty(side), sides.get(side)); }
        if (wanted != state) { level.setBlock(worldPosition, wanted, Block.UPDATE_ALL); }
    }

    @Override public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) { ICUtils.addFutureServerTask(level, this::syncState); }
    }

    @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && side != null && sides.get(side) != Enums.IOSideConfig.NONE) { return capabilities.get(side).cast(); }
        return super.getCapability(cap, side);
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<IEnergyStorage> capability : capabilities.values()) { capability.invalidate(); }
    }

    @Override public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        for (Direction side : DirectionUtils.VALUES) {
            String key = SIDE_KEY + side.get3DDataValue();
            if (nbt.contains(key)) { sides.put(side, Enums.IOSideConfig.VALUES[Mth.clamp(nbt.getInt(key), 0, Enums.IOSideConfig.VALUES.length - 1)]); }
        }
    }

    @Override public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        for (Direction side : DirectionUtils.VALUES) { nbt.putInt(SIDE_KEY + side.get3DDataValue(), sides.get(side).ordinal()); }
    }

    private final class SideEnergy implements IEnergyStorage {
        private final Direction side;

        private SideEnergy(Direction side) { this.side = side; }

        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return canReceive() ? Math.max(maxReceive, 0) : 0; }

        @Override public int extractEnergy(int maxExtract, boolean simulate) { return canExtract() ? Math.max(maxExtract, 0) : 0; }

        @Override public int getEnergyStored() { return Integer.MAX_VALUE; }

        @Override public int getMaxEnergyStored() { return Integer.MAX_VALUE; }

        @Override public boolean canExtract() { return !getLevelNonnull().isClientSide && sides.get(side) == Enums.IOSideConfig.OUTPUT; }

        @Override public boolean canReceive() { return !getLevelNonnull().isClientSide && sides.get(side) == Enums.IOSideConfig.INPUT; }
    }
}
