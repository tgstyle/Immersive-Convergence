package com.immersiveconvergence.common.event;

import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IGeneralMultiblock;
import com.immersiveconvergence.api.multiblock.IICTickingService;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ICTickingService implements IICTickingService {
    private static final Set<TileEntity> REMOVE_FROM_TICKING = Collections.synchronizedSet(new HashSet<>());

    @Override public <T extends TileEntity & IGeneralMultiblock> void checkForNeedlessTicking(@Nonnull T tile) {
        if (!tile.getWorld().isRemote && tile.isLogicDummy()) { REMOVE_FROM_TICKING.add(tile); }
    }

    @Override public void removeFromTicking(@Nonnull TileEntity tile) { REMOVE_FROM_TICKING.add(tile); }

    @Override public void drain(@Nullable World world) {
        if (world == null || REMOVE_FROM_TICKING.isEmpty()) { return; }
        synchronized (REMOVE_FROM_TICKING) {
            if (REMOVE_FROM_TICKING.isEmpty()) { return; }
            world.tickableTileEntities.removeIf(tile -> tile.getWorld() == world && REMOVE_FROM_TICKING.contains(tile));
            REMOVE_FROM_TICKING.removeIf(tile -> tile.getWorld() == world);
        }
    }

    private void forgetWorld(@Nonnull World world) {
        if (REMOVE_FROM_TICKING.isEmpty()) { return; }
        synchronized (REMOVE_FROM_TICKING) { REMOVE_FROM_TICKING.removeIf(tile -> tile.getWorld() == world); }
    }

    @SubscribeEvent public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { drain(event.world); }
    }

    @SubscribeEvent public void onWorldUnload(WorldEvent.Unload event) { forgetWorld(event.getWorld()); }
}
