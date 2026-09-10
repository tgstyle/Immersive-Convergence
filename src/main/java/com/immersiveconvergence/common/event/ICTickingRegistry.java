package com.immersiveconvergence.common.event;

import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IGeneralMultiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
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

    public static void removeFromTicking(TileEntity tile) { REMOVE_FROM_TICKING.add(tile); }

    public static void drain(World world) {
        if (world == null || REMOVE_FROM_TICKING.isEmpty()) { return; }
        synchronized (REMOVE_FROM_TICKING) {
            if (REMOVE_FROM_TICKING.isEmpty()) { return; }
            world.tickableTileEntities.removeIf(tile -> tile.getWorld() == world && REMOVE_FROM_TICKING.contains(tile));
            REMOVE_FROM_TICKING.removeIf(tile -> tile.getWorld() == world);
        }
    }

    public static void forgetWorld(World world) {
        if (REMOVE_FROM_TICKING.isEmpty()) { return; }
        synchronized (REMOVE_FROM_TICKING) { REMOVE_FROM_TICKING.removeIf(tile -> tile.getWorld() == world); }
    }

    @SubscribeEvent public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { drain(event.world); }
    }

    @SubscribeEvent public void onWorldUnload(WorldEvent.Unload event) { forgetWorld(event.getWorld()); }
}
