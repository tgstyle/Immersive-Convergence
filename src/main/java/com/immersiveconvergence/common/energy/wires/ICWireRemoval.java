package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.connectors.ICConnectableBlockEntity;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ICLib.MODID) public final class ICWireRemoval {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> PENDING = new HashMap<>();

    private ICWireRemoval() {}

    public static void checkLater(@Nonnull ServerLevel level, @Nonnull BlockPos pos) {
        if (!ICMods.immersiveEngineering()) { PENDING.computeIfAbsent(level.dimension(), dimension -> new LinkedHashSet<>()).add(pos.immutable()); }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) { return; }
        Set<BlockPos> pending = PENDING.remove(level.dimension());
        if (pending == null) { return; }
        for (BlockPos pos : pending) {
            if (level.isLoaded(pos) && connectable(level, pos) == null) { removeBlock(level, pos); }
        }
    }

    @SubscribeEvent public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) { PENDING.remove(level.dimension()); }
    }

    public static void removeBlock(@Nonnull ServerLevel level, @Nonnull BlockPos pos) { ICWires.network(level).removeAt(pos, ICWireCause.BLOCK_BROKEN); }

    public static void breakWires(@Nonnull ServerLevel level, @Nonnull Collection<ICWire> wires, @Nonnull Vec3 dropAt, @Nonnull ICWireCause cause) {
        for (ICWire wire : ICWireSavedData.get(level).cutAll(wires, cause)) { Containers.dropItemStack(level, dropAt.x, dropAt.y, dropAt.z, ICWireCoils.coilFor(wire)); }
    }

    @Nullable static ICConnectableBlockEntity connectable(@Nonnull Level level, @Nonnull BlockPos pos) {
        BlockEntity entity = level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
        return entity instanceof ICConnectableBlockEntity connectable ? connectable : null;
    }
}
