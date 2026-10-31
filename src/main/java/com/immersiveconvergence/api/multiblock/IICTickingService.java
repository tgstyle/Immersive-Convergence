package com.immersiveconvergence.api.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICTickingService {
    <T extends TileEntity & ICBlockInterfaces.IGeneralMultiblock> void checkForNeedlessTicking(@Nonnull T tile);

    void removeFromTicking(@Nonnull TileEntity tile);

    void drain(@Nullable World world);
}
