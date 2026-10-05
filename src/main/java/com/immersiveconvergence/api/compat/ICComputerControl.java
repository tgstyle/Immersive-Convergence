package com.immersiveconvergence.api.compat;

import com.immersiveconvergence.api.multiblock.ICTileEntityMultiblockMetal;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Loader;

public final class ICComputerControl {
    public static final String ENABLE_COMPUTER_CONTROL = "enableComputerControl";
    public static final String SET_ENABLED = "setEnabled";
    public static final String CONTROL_DISABLED = "Computer control must be enabled to enable or disable the machine";

    private ICComputerControl() {}

    public static boolean computerModLoaded() { return Loader.isModLoaded("opencomputers") || Loader.isModLoaded("computercraft"); }

    public static void enableComputerControl(ICTileEntityMultiblockMetal<?, ?> tile, boolean allow) { tile.computerOn = allow ? Boolean.TRUE : null; }

    public static void setEnabled(ICTileEntityMultiblockMetal<?, ?> tile, boolean enabled) {
        if (tile.computerOn == null) { throw new IllegalStateException(CONTROL_DISABLED); }
        tile.computerOn = enabled;
    }

    public static <T extends ICTileEntityMultiblockMetal<?, ?>> ICComputerTable<T> multiblock(String type, Class<T> tileClass, boolean control) {
        ICComputerTable<T> table = new ICComputerTable<>(type, tileClass, tile -> redstonePortMaster(tile, tileClass));
        return control ? table.control(tile -> tile.computerOn, (tile, on) -> tile.computerOn = on) : table;
    }

    private static <T extends ICTileEntityMultiblockMetal<?, ?>> T redstonePortMaster(TileEntity tile, Class<T> tileClass) {
        if (!(tile instanceof ICTileEntityMultiblockMetal) || !((ICTileEntityMultiblockMetal<?, ?>)tile).isRedstonePos()) { return null; }
        TileEntity master = ((ICTileEntityMultiblockMetal<?, ?>)tile).master();
        return tileClass.isInstance(master) ? tileClass.cast(master) : null;
    }
}
