package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.connectors.ICConnectableBlockEntity;
import com.immersiveconvergence.common.ie.IEWireBridge;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ICWireSavedData extends SavedData {
    public static final String NAME = "immersiveconvergence_wires";
    private static final String LEDGER = "ledger";
    private static final String WAITING = "waiting";
    private final ICWireNetwork network = new ICWireNetwork(this::setDirty);
    private final List<CompoundTag> dormant = new ArrayList<>();
    private final List<ICWire> restoring = new ArrayList<>();
    private final List<BlockPos[]> unrecorded = new ArrayList<>();
    private final List<BlockPos[]> held = new ArrayList<>();
    private boolean restore;
    private boolean prune;
    private boolean backfill = true;
    private boolean scanned;

    @Nonnull public static ICWireNetwork get(@Nonnull ServerLevel level) { return data(level).network; }

    public static void ieWireAdded(@Nullable Level level, @Nonnull ICConnectionPoint self, @Nonnull ICConnectionPoint other) {
        if (!(level instanceof ServerLevel server) || !ICMods.immersiveEngineering() || IEWireBridge.linking()) { return; }
        record(data(server).network, server, self, other);
    }

    private static void record(ICWireNetwork ledger, ServerLevel level, ICConnectionPoint self, ICConnectionPoint other) {
        if (connectable(level, self.position()) && connectable(level, other.position())) {
            ICWire wire = IEWireBridge.wire(level, self, other);
            if (wire != null) { ledger.addWire(wire); }
        }
    }

    public static void ieWireRemoved(@Nullable Level level, @Nonnull ICConnectionPoint self, @Nullable ICConnectionPoint other) {
        if (!(level instanceof ServerLevel server) || !ICMods.immersiveEngineering()) { return; }
        ICWireNetwork ledger = data(server).network;
        for (ICWire wire : new ArrayList<>(ledger.wiresAt(self))) {
            ICConnectionPoint far = wire.other(self);
            if (other == null || far.equals(other)) { ledger.removeWire(self, far); }
        }
    }

    public static void reconcile(@Nonnull ServerLevel level) {
        ICWireSavedData data = data(level);
        if (data.restore || data.prune || data.backfill) { data.reconcileLoaded(level); }
    }

    private void reconcileLoaded(ServerLevel level) {
        if (!scanned) {
            scanned = true;
            if (restore) {
                for (ICWire wire : network.getAllWires()) {
                    if (IEWireBridge.unlinked(level, wire)) { restoring.add(wire); }
                }
            }
            if (prune || backfill) {
                for (BlockPos[] pair : IEWireBridge.pairs(level)) {
                    if (!recorded(network, pair[0], pair[1])) { unrecorded.add(pair); }
                }
                held.removeIf(pair -> !contains(unrecorded, pair));
            }
        }
        restoreLoaded(level);
        settleUnrecorded(level);
        if (restoring.isEmpty() && unrecorded.isEmpty()) {
            restore = false;
            prune = false;
            backfill = false;
            setDirty();
        }
    }

    private void restoreLoaded(ServerLevel level) {
        for (Iterator<ICWire> iterator = restoring.iterator(); iterator.hasNext(); ) {
            ICWire wire = iterator.next();
            BlockPos a = wire.getA().position();
            BlockPos b = wire.getB().position();
            if (!level.isLoaded(a) || !level.isLoaded(b)) { continue; }
            iterator.remove();
            if (!connectable(level, a) || !connectable(level, b)) { continue; }
            if (network.wire(wire.getA(), wire.getB()) != null && IEWireBridge.unlinked(level, wire)) { IEWireBridge.link(level, wire); }
        }
    }

    private void settleUnrecorded(ServerLevel level) {
        for (Iterator<BlockPos[]> iterator = unrecorded.iterator(); iterator.hasNext(); ) {
            BlockPos[] pair = iterator.next();
            if (!level.isLoaded(pair[0]) || !level.isLoaded(pair[1])) { continue; }
            iterator.remove();
            boolean keep = contains(held, pair);
            held.removeIf(waiting -> Arrays.equals(waiting, pair));
            if (!connectable(level, pair[0]) || !connectable(level, pair[1]) || recorded(network, pair[0], pair[1])) { continue; }
            if (prune && !keep) { IEWireBridge.prune(level, pair[0], pair[1]); }
            else {
                for (ICWire wire : IEWireBridge.wires(level, pair[0], pair[1])) { network.addWire(wire); }
            }
        }
    }

    private static boolean contains(List<BlockPos[]> pairs, BlockPos[] pair) {
        for (BlockPos[] other : pairs) {
            if (Arrays.equals(other, pair)) { return true; }
        }
        return false;
    }

    private static boolean recorded(ICWireNetwork ledger, BlockPos first, BlockPos second) {
        for (ICConnectionPoint point : ledger.pointsAt(first)) {
            for (ICWire wire : ledger.wiresAt(point)) {
                if (wire.getA().position().equals(second) || wire.getB().position().equals(second)) { return true; }
            }
        }
        return false;
    }

    private static boolean connectable(ServerLevel level, BlockPos pos) { return level.getBlockEntity(pos) instanceof ICConnectableBlockEntity; }

    private static ICWireSavedData data(ServerLevel level) {
        ICWireSavedData data = level.getDataStorage().computeIfAbsent(ICWireSavedData::load, ICWireSavedData::new, NAME);
        data.network.bind(level);
        return data;
    }

    private static ICWireSavedData load(CompoundTag nbt) {
        ICWireSavedData data = new ICWireSavedData();
        data.read(nbt);
        return data;
    }

    private void read(CompoundTag nbt) {
        restore = !nbt.getBoolean(LEDGER);
        prune = restore && nbt.contains(LEDGER);
        backfill = !nbt.contains(LEDGER);
        held.clear();
        ListTag waiting = nbt.getList(WAITING, Tag.TAG_LONG);
        for (int i = 0; i + 1 < waiting.size(); i += 2) { held.add(new BlockPos[]{BlockPos.of(((LongTag)waiting.get(i)).getAsLong()), BlockPos.of(((LongTag)waiting.get(i + 1)).getAsLong())}); }
        Map<ICConnectionPoint, Integer> components = new HashMap<>();
        ListTag points = nbt.getList("points", Tag.TAG_COMPOUND);
        for (int i = 0; i < points.size(); i++) {
            CompoundTag tag = points.getCompound(i);
            components.put(readPoint(tag, "pos", "idx"), tag.getInt("comp"));
        }
        List<ICConnectionPoint> closed = new ArrayList<>();
        ListTag closedTags = nbt.getList("closed", Tag.TAG_COMPOUND);
        for (int i = 0; i < closedTags.size(); i++) { closed.add(readPoint(closedTags.getCompound(i), "pos", "idx")); }
        List<ICWire> wires = new ArrayList<>();
        List<ICConnectionPoint[]> dropped = new ArrayList<>();
        Set<List<ICConnectionPoint>> pairs = new HashSet<>();
        ListTag wireTags = nbt.getList("wires", Tag.TAG_COMPOUND);
        for (int i = 0; i < wireTags.size(); i++) {
            CompoundTag tag = wireTags.getCompound(i);
            ICConnectionPoint a = readPoint(tag, "a", "ai");
            ICConnectionPoint b = readPoint(tag, "b", "bi");
            ICWireType type = resolve(tag);
            if (type == null) {
                ICLib.IC_LOGGER.warn("Keeping the wire {}-{} in {} dormant: no type {} and no default for category {}.", a, b, NAME, tag.getString("type"), tag.getString("cat"));
                dormant.add(tag.copy());
                dropped.add(new ICConnectionPoint[]{a, b});
                continue;
            }
            ICWire wire = new ICWire(a, readOffset(tag, "ao"), b, readOffset(tag, "bo"), type);
            if (pairs.add(Arrays.asList(wire.getA(), wire.getB()))) { wires.add(wire); }
            else {
                ICLib.IC_LOGGER.warn("Keeping the wire {} in {} dormant: its point pair was wired again while its type was missing.", wire, NAME);
                dormant.add(tag.copy());
            }
        }
        Map<ICConnectionPoint[], Double> links = new HashMap<>();
        ListTag linkTags = nbt.getList("links", Tag.TAG_COMPOUND);
        for (int i = 0; i < linkTags.size(); i++) {
            CompoundTag tag = linkTags.getCompound(i);
            BlockPos pos = BlockPos.of(tag.getLong("pos"));
            links.put(new ICConnectionPoint[]{new ICConnectionPoint(pos, tag.getInt("ai")), new ICConnectionPoint(pos, tag.getInt("bi"))}, tag.getDouble("loss"));
        }
        network.load(nbt.getInt("next"), components, closed, wires, links, dropped);
    }

    @Nullable private static ICWireType resolve(CompoundTag tag) {
        ICWireType type = ICWireType.byName(tag.getString("type"));
        if (type != null) { return type; }
        ICWireCategory category = ICWireCategory.byName(tag.contains("cat") ? tag.getString("cat") : null);
        return category == null ? null : category.getDefaultType();
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag nbt) {
        if (!ICMods.immersiveEngineering() || prune) { nbt.putBoolean(LEDGER, false); }
        else if (!restore && !backfill) { nbt.putBoolean(LEDGER, true); }
        ListTag waiting = new ListTag();
        for (BlockPos[] pair : scanned && !prune ? unrecorded : held) {
            waiting.add(LongTag.valueOf(pair[0].asLong()));
            waiting.add(LongTag.valueOf(pair[1].asLong()));
        }
        if (!waiting.isEmpty()) { nbt.put(WAITING, waiting); }
        nbt.putInt("next", network.getNextComponent());
        ListTag points = new ListTag();
        for (Map.Entry<ICConnectionPoint, Integer> entry : network.getComponents().entrySet()) {
            CompoundTag tag = new CompoundTag();
            writePoint(tag, "pos", "idx", entry.getKey());
            tag.putInt("comp", entry.getValue());
            points.add(tag);
        }
        nbt.put("points", points);
        ListTag closed = new ListTag();
        for (ICConnectionPoint point : network.getClosedPoints()) {
            CompoundTag tag = new CompoundTag();
            writePoint(tag, "pos", "idx", point);
            closed.add(tag);
        }
        nbt.put("closed", closed);
        ListTag wires = new ListTag();
        for (ICWire wire : network.getAllWires()) {
            CompoundTag tag = new CompoundTag();
            writePoint(tag, "a", "ai", wire.getA());
            writeOffset(tag, "ao", wire.getOffsetA());
            writePoint(tag, "b", "bi", wire.getB());
            writeOffset(tag, "bo", wire.getOffsetB());
            tag.putString("type", wire.getType().getUniqueName());
            if (wire.getType().getCategory() != null) { tag.putString("cat", wire.getType().getCategory().getName()); }
            wires.add(tag);
        }
        for (CompoundTag tag : dormant) { wires.add(tag.copy()); }
        nbt.put("wires", wires);
        ListTag links = new ListTag();
        for (Map.Entry<ICConnectionPoint[], Double> link : network.getAllLinks().entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putLong("pos", link.getKey()[0].position().asLong());
            tag.putInt("ai", link.getKey()[0].index());
            tag.putInt("bi", link.getKey()[1].index());
            tag.putDouble("loss", link.getValue());
            links.add(tag);
        }
        nbt.put("links", links);
        return nbt;
    }

    private static ICConnectionPoint readPoint(CompoundTag tag, String posKey, String indexKey) { return new ICConnectionPoint(BlockPos.of(tag.getLong(posKey)), tag.getInt(indexKey)); }

    private static void writePoint(CompoundTag tag, String posKey, String indexKey, ICConnectionPoint point) {
        tag.putLong(posKey, point.position().asLong());
        tag.putInt(indexKey, point.index());
    }

    private static Vec3 readOffset(CompoundTag tag, String key) {
        ListTag list = tag.getList(key, Tag.TAG_DOUBLE);
        return new Vec3(list.getDouble(0), list.getDouble(1), list.getDouble(2));
    }

    private static void writeOffset(CompoundTag tag, String key, Vec3 offset) {
        ListTag list = new ListTag();
        list.add(DoubleTag.valueOf(offset.x));
        list.add(DoubleTag.valueOf(offset.y));
        list.add(DoubleTag.valueOf(offset.z));
        tag.put(key, list);
    }
}
