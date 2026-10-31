package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.IICConnector;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID)
public final class ICWireRemoval {
    private static final Map<Integer, Set<BlockPos>> PENDING = new HashMap<>();

    private ICWireRemoval() {}

    public static void checkLater(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote) { PENDING.computeIfAbsent(world.provider.getDimension(), dimension -> new LinkedHashSet<>()).add(pos.toImmutable()); }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) { return; }
        Set<BlockPos> pending = PENDING.remove(event.world.provider.getDimension());
        if (pending == null) { return; }
        for (BlockPos pos : pending) {
            if (event.world.isBlockLoaded(pos) && connectable(event.world, pos) == null) { removeBlock(event.world, pos); }
        }
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) { PENDING.remove(event.getWorld().provider.getDimension()); }
    }

    public static void removeBlock(@Nonnull World world, @Nonnull BlockPos pos) {
        ICWires.network(world).removeAt(pos, ICWireCause.BLOCK_BROKEN);
    }

    public static void breakWires(@Nonnull World world, @Nonnull Collection<ICWire> wires, @Nonnull Vec3d dropAt, @Nonnull ICWireCause cause) {
        for (ICWire wire : ICWireSavedData.get(world).cutAll(wires, cause)) { InventoryHelper.spawnItemStack(world, dropAt.x, dropAt.y, dropAt.z, ICWireCoils.coilFor(wire)); }
    }

    @Nullable static IICConnector connectable(@Nonnull World world, @Nonnull BlockPos pos) {
        TileEntity tile = world.getChunk(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        return tile instanceof IICConnector ? (IICConnector)tile : null;
    }
}
