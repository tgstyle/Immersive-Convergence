package com.immersiveconvergence.common.util.compat.computers;

import com.immersiveconvergence.api.compat.ICComputerTable;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public final class IEComputerLocators {
    private IEComputerLocators() {}

    public static <T extends TileEntityMultiblockMetal<?, ?>> ICComputerTable<T> metal(String type, Class<T> tileClass, boolean control) {
        ICComputerTable<T> table = new ICComputerTable<>(type, tileClass, tile -> port(tile, tileClass));
        return control ? table.control(tile -> tile.computerOn.orElse(null), (tile, on) -> tile.computerOn = Optional.ofNullable(on)) : table;
    }

    public static <T extends TileEntityMultiblockPart<?>> ICComputerTable<T> part(String type, Class<T> tileClass) { return new ICComputerTable<>(type, tileClass, tile -> master(tile, tileClass)); }

    public static <T extends TileEntity> ICComputerTable<T> block(String type, Class<T> tileClass, ToIntFunction<T> dummyOffset) { return new ICComputerTable<>(type, tileClass, base(tileClass, dummyOffset)); }

    public static <T extends TileEntity> ICComputerTable<T> exact(String type, Class<T> tileClass) { return new ICComputerTable<>(type, tileClass, tile -> tile.getClass() == tileClass ? tileClass.cast(tile) : null); }

    public static <T extends TileEntity> ICComputerTable<T> only(String type, Class<T> tileClass, Predicate<T> accepts) { return new ICComputerTable<>(type, tileClass, tile -> tileClass.isInstance(tile) && accepts.test(tileClass.cast(tile)) ? tileClass.cast(tile) : null); }

    @Nullable private static <T extends TileEntityMultiblockMetal<?, ?>> T port(TileEntity tile, Class<T> tileClass) {
        if (!tileClass.isInstance(tile) || !((TileEntityMultiblockMetal<?, ?>)tile).isRedstonePos()) { return null; }
        Object master = ((TileEntityMultiblockMetal<?, ?>)tile).master();
        return tileClass.isInstance(master) ? tileClass.cast(master) : null;
    }

    @Nullable public static <T extends TileEntityMultiblockPart<?>> T master(TileEntity tile, Class<T> tileClass) {
        if (!tileClass.isInstance(tile) || !((TileEntityMultiblockPart<?>)tile).formed) { return null; }
        Object master = ((TileEntityMultiblockPart<?>)tile).master();
        return tileClass.isInstance(master) ? tileClass.cast(master) : null;
    }

    private static <T extends TileEntity> Function<TileEntity, T> base(Class<T> tileClass, ToIntFunction<T> dummyOffset) {
        return tile -> {
            if (!tileClass.isInstance(tile)) { return null; }
            int offset = dummyOffset.applyAsInt(tileClass.cast(tile));
            if (offset == 0) { return tileClass.cast(tile); }
            TileEntity base = tile.getWorld().getTileEntity(tile.getPos().down(offset));
            return tileClass.isInstance(base) ? tileClass.cast(base) : null;
        };
    }
}
