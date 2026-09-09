package com.immersiveconvergence.common.event;

import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IGeneralMultiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ICTickingRegistry {
    private static final Set<TileEntity> REMOVE_FROM_TICKING = Collections.synchronizedSet(new HashSet<>());

    public static <T extends TileEntity & IGeneralMultiblock> void checkForNeedlessTicking(T tile) {
        if (!tile.getWorld().isRemote && tile.isLogicDummy()) { REMOVE_FROM_TICKING.add(tile); }
    }

    @SubscribeEvent public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || REMOVE_FROM_TICKING.isEmpty()) { return; }
        int dimension = event.world.provider.getDimension();
        synchronized (REMOVE_FROM_TICKING) {
            event.world.tickableTileEntities.removeAll(REMOVE_FROM_TICKING);
            REMOVE_FROM_TICKING.removeIf(tile -> tile.getWorld().provider.getDimension() == dimension);
        }
    }
}
