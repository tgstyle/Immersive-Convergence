package com.immersiveconvergence.api.wires;

import com.immersiveconvergence.api.wires.connectors.ICConnectableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICWireService {
    @Nonnull IICWireNetwork network(@Nonnull Level level);

    @Nonnull IICEnergyNetwork energy(@Nonnull Level level);

    @Nonnull IICWireCollision collision();

    @Nonnull InteractionResult useCoil(@Nonnull Player player, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull InteractionHand hand, float hitX, float hitY, float hitZ);

    boolean hasNoExternalConnections(@Nullable Level level, @Nonnull ICConnectionPoint point);

    void syncConnectionPoints(@Nonnull ICConnectableBlockEntity tile);

    void connectableLoaded(@Nonnull ICConnectableBlockEntity tile);

    void connectableInvalidated(@Nonnull ICConnectableBlockEntity tile);

    int redstoneValue(Level level, BlockPos pos, int channel);

    void updateRedstoneValues(Level level, BlockPos pos);
}
