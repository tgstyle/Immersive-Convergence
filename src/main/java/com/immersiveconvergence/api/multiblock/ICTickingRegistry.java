package com.immersiveconvergence.api.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICTickingRegistry {
    private static IICTickingService service;

    private ICTickingRegistry() {}

    public static void install(@Nonnull IICTickingService installed) { service = installed; }

    public static <T extends TileEntity & ICBlockInterfaces.IGeneralMultiblock> void checkForNeedlessTicking(@Nonnull T tile) { required().checkForNeedlessTicking(tile); }

    public static void removeFromTicking(@Nonnull TileEntity tile) { required().removeFromTicking(tile); }

    public static void drain(@Nullable World world) { required().drain(world); }

    @Nonnull private static IICTickingService required() {
        if (service == null) { throw new IllegalStateException("The ticking service is installed at preInit"); }
        return service;
    }
}
