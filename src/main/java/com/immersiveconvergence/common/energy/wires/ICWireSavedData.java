package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectable;
import com.immersiveconvergence.common.energy.IEWireBridge;
import com.immersiveconvergence.common.energy.IEWireGraph;
import com.immersiveconvergence.api.util.ICLogger;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

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

@SuppressWarnings("unused") public class ICWireSavedData extends WorldSavedData {
    public static final String NAME = "immersiveconvergence_wires";
    private static final String LEDGER = "ledger";
    private static final String WAITING = "waiting";
    private final ICWireNetwork network = new ICWireNetwork(this::markDirty);
    private final List<NBTTagCompound> dormant = new ArrayList<>();
    private final List<ICWire> restoring = new ArrayList<>();
    private final List<BlockPos[]> unrecorded = new ArrayList<>();
    private final List<BlockPos[]> held = new ArrayList<>();
    private boolean restore;
    private boolean prune;
    private boolean backfill = true;
    private boolean scanned;

    public ICWireSavedData(String name) { super(name); }

    @Nonnull public static ICWireNetwork get(@Nonnull World world) { return data(world).network; }

    public static void ieWireAdded(@Nullable World world, @Nonnull BlockPos self, @Nullable BlockPos other, @Nonnull ICWireType type) {
        if (world == null || world.isRemote || other == null || IEWireBridge.linking() || !ICMods.immersiveEngineering()) { return; }
        record(data(world).network, world, self, other, type);
    }

    private static void record(ICWireNetwork ledger, World world, BlockPos self, BlockPos other, ICWireType type) {
        ICTileEntityConnectable here = connectable(world, self);
        ICTileEntityConnectable there = connectable(world, other);
        if (here == null || there == null) { return; }
        ICConnectionPoint herePoint = here.pointToward(other, type);
        ICConnectionPoint therePoint = there.pointToward(self, type);
        ICWire wire = new ICWire(herePoint, here.offset(herePoint, type), therePoint, there.offset(therePoint, type), type);
        if (ledger.wire(wire.getA(), wire.getB()) != null) { return; }
        drop(ledger, self, other);
        ledger.addWire(wire);
    }

    public static void ieWireRemoved(@Nullable World world, @Nonnull BlockPos self, @Nullable BlockPos other, @Nullable ICWireType type) {
        if (world == null || world.isRemote || !ICMods.immersiveEngineering()) { return; }
        drop(data(world).network, self, other);
    }

    private static void drop(ICWireNetwork ledger, BlockPos self, @Nullable BlockPos other) {
        for (ICConnectionPoint point : new ArrayList<>(ledger.pointsAt(self))) {
            for (ICWire wire : new ArrayList<>(ledger.wiresAt(point))) {
                ICConnectionPoint far = wire.getA().equals(point) ? wire.getB() : wire.getA();
                if (other == null || far.getPos().equals(other)) { ledger.removeWire(point, far); }
            }
        }
    }

    public static void reconcile(@Nonnull World world) {
        ICWireSavedData data = data(world);
        if (data.restore || data.prune || data.backfill) { data.reconcileLoaded(world); }
    }

    private void reconcileLoaded(World world) {
        if (!scanned) {
            scanned = true;
            if (restore) {
                for (ICWire wire : network.getAllWires()) {
                    if (IEWireBridge.unlinked(world, wire.getA().getPos(), wire.getB().getPos())) { restoring.add(wire); }
                }
            }
            if (prune || backfill) {
                for (BlockPos[] pair : IEWireBridge.pairs(world)) {
                    if (!recorded(network, pair[0], pair[1])) { unrecorded.add(pair); }
                }
                held.removeIf(pair -> !contains(unrecorded, pair));
            }
        }
        boolean restored = restoreLoaded(world);
        boolean pruned = settleUnrecorded(world);
        if (restored || pruned) { IEWireGraph.invalidate(world); }
        if (backfill && unrecorded.isEmpty()) {
            backfill = false;
            prune = restore;
            markDirty();
        }
        if (restoring.isEmpty() && unrecorded.isEmpty()) {
            restore = false;
            prune = false;
            markDirty();
        }
    }

    private boolean restoreLoaded(World world) {
        boolean changed = false;
        for (Iterator<ICWire> iterator = restoring.iterator(); iterator.hasNext(); ) {
            ICWire wire = iterator.next();
            BlockPos a = wire.getA().getPos();
            BlockPos b = wire.getB().getPos();
            if (!world.isBlockLoaded(a) || !world.isBlockLoaded(b)) { continue; }
            iterator.remove();
            if (connectable(world, a) == null || connectable(world, b) == null) { continue; }
            if (network.wire(wire.getA(), wire.getB()) != null && IEWireBridge.unlinked(world, a, b) && IEWireBridge.link(world, wire)) { changed = true; }
        }
        return changed;
    }

    private boolean settleUnrecorded(World world) {
        boolean changed = false;
        for (Iterator<BlockPos[]> iterator = unrecorded.iterator(); iterator.hasNext(); ) {
            BlockPos[] pair = iterator.next();
            if (!world.isBlockLoaded(pair[0]) || !world.isBlockLoaded(pair[1])) { continue; }
            iterator.remove();
            boolean keep = contains(held, pair);
            held.removeIf(waiting -> Arrays.equals(waiting, pair));
            if (connectable(world, pair[0]) == null || connectable(world, pair[1]) == null || recorded(network, pair[0], pair[1])) { continue; }
            if (prune && !keep) {
                if (IEWireBridge.prune(world, pair[0], pair[1])) { changed = true; }
            }
            else {
                ICWireType type = IEWireBridge.typeOf(world, pair[0], pair[1]);
                if (type != null) { record(network, world, pair[0], pair[1], type); }
            }
        }
        return changed;
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
                if (wire.getA().getPos().equals(second) || wire.getB().getPos().equals(second)) { return true; }
            }
        }
        return false;
    }

    @Nullable private static ICTileEntityConnectable connectable(World world, BlockPos pos) {
        TileEntity tile = world.isBlockLoaded(pos) ? world.getTileEntity(pos) : null;
        return tile instanceof ICTileEntityConnectable && !tile.isInvalid() ? (ICTileEntityConnectable)tile : null;
    }

    private static ICWireSavedData data(World world) {
        MapStorage storage = world.getPerWorldStorage();
        ICWireSavedData data = (ICWireSavedData)storage.getOrLoadData(ICWireSavedData.class, NAME);
        if (data == null) {
            data = new ICWireSavedData(NAME);
            storage.setData(NAME, data);
        }
        data.network.bind(world);
        return data;
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        restore = !nbt.getBoolean(LEDGER);
        prune = restore && nbt.hasKey(LEDGER);
        backfill = !nbt.hasKey(LEDGER);
        held.clear();
        NBTTagList waiting = nbt.getTagList(WAITING, Constants.NBT.TAG_LONG);
        for (int i = 0; i + 1 < waiting.tagCount(); i += 2) { held.add(new BlockPos[]{BlockPos.fromLong(((NBTTagLong)waiting.get(i)).getLong()), BlockPos.fromLong(((NBTTagLong)waiting.get(i + 1)).getLong())}); }
        Map<ICConnectionPoint, Integer> components = new HashMap<>();
        NBTTagList points = nbt.getTagList("points", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < points.tagCount(); i++) {
            NBTTagCompound tag = points.getCompoundTagAt(i);
            components.put(readPoint(tag, "pos", "idx"), tag.getInteger("comp"));
        }
        List<ICConnectionPoint> closed = new ArrayList<>();
        NBTTagList closedTags = nbt.getTagList("closed", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < closedTags.tagCount(); i++) { closed.add(readPoint(closedTags.getCompoundTagAt(i), "pos", "idx")); }
        List<ICWire> wires = new ArrayList<>();
        List<ICConnectionPoint[]> dropped = new ArrayList<>();
        Set<List<ICConnectionPoint>> pairs = new HashSet<>();
        NBTTagList wireTags = nbt.getTagList("wires", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < wireTags.tagCount(); i++) {
            NBTTagCompound tag = wireTags.getCompoundTagAt(i);
            ICConnectionPoint a = readPoint(tag, "a", "ai");
            ICConnectionPoint b = readPoint(tag, "b", "bi");
            ICWireType type = resolve(tag);
            if (type == null) {
                ICLogger.warn("Keeping the wire " + a + "-" + b + " in " + NAME + " dormant: no type " + tag.getString("type") + " and no default for category " + tag.getString("cat") + ".");
                dormant.add(tag.copy());
                dropped.add(new ICConnectionPoint[]{a, b});
                continue;
            }
            ICWire wire = new ICWire(a, readOffset(tag, "ao"), b, readOffset(tag, "bo"), type);
            if (pairs.add(Arrays.asList(wire.getA(), wire.getB()))) { wires.add(wire); }
            else {
                ICLogger.warn("Keeping the wire " + wire + " in " + NAME + " dormant: its point pair was wired again while its type was missing.");
                dormant.add(tag.copy());
            }
        }
        Map<ICConnectionPoint[], Double> links = new HashMap<>();
        NBTTagList linkTags = nbt.getTagList("links", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < linkTags.tagCount(); i++) {
            NBTTagCompound tag = linkTags.getCompoundTagAt(i);
            BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
            links.put(new ICConnectionPoint[]{new ICConnectionPoint(pos, tag.getInteger("ai")), new ICConnectionPoint(pos, tag.getInteger("bi"))}, tag.getDouble("loss"));
        }
        network.load(nbt.getInteger("next"), components, closed, wires, links, dropped);
    }

    @Nullable private static ICWireType resolve(NBTTagCompound tag) {
        ICWireType type = ICWireType.byName(tag.getString("type"));
        if (type != null) { return type; }
        ICWireCategory category = ICWireCategory.byName(tag.hasKey("cat") ? tag.getString("cat") : null);
        return category == null ? null : category.getDefaultType();
    }

    private static void writeType(NBTTagCompound tag, ICWireType type) {
        tag.setString("type", type.getUniqueName());
        if (type.getCategory() != null) { tag.setString("cat", type.getCategory().getName()); }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        if (prune || !ICMods.immersiveEngineering() && (!backfill || !held.isEmpty())) { nbt.setBoolean(LEDGER, false); }
        else if (!restore && !backfill) { nbt.setBoolean(LEDGER, true); }
        NBTTagList waiting = new NBTTagList();
        for (BlockPos[] pair : scanned && !prune ? unrecorded : held) {
            waiting.appendTag(new NBTTagLong(pair[0].toLong()));
            waiting.appendTag(new NBTTagLong(pair[1].toLong()));
        }
        if (waiting.tagCount() > 0) { nbt.setTag(WAITING, waiting); }
        nbt.setInteger("next", network.getNextComponent());
        NBTTagList points = new NBTTagList();
        for (Map.Entry<ICConnectionPoint, Integer> entry : network.getComponents().entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            writePoint(tag, "pos", "idx", entry.getKey());
            tag.setInteger("comp", entry.getValue());
            points.appendTag(tag);
        }
        nbt.setTag("points", points);
        NBTTagList closed = new NBTTagList();
        for (ICConnectionPoint point : network.getClosedPoints()) {
            NBTTagCompound tag = new NBTTagCompound();
            writePoint(tag, "pos", "idx", point);
            closed.appendTag(tag);
        }
        nbt.setTag("closed", closed);
        NBTTagList wires = new NBTTagList();
        for (ICWire wire : network.getAllWires()) {
            NBTTagCompound tag = new NBTTagCompound();
            writePoint(tag, "a", "ai", wire.getA());
            writeOffset(tag, "ao", wire.getOffsetA());
            writePoint(tag, "b", "bi", wire.getB());
            writeOffset(tag, "bo", wire.getOffsetB());
            writeType(tag, wire.getType());
            wires.appendTag(tag);
        }
        for (NBTTagCompound tag : dormant) { wires.appendTag(tag.copy()); }
        nbt.setTag("wires", wires);
        NBTTagList links = new NBTTagList();
        for (Map.Entry<ICConnectionPoint[], Double> link : network.getAllLinks().entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("pos", link.getKey()[0].getPos().toLong());
            tag.setInteger("ai", link.getKey()[0].getIndex());
            tag.setInteger("bi", link.getKey()[1].getIndex());
            tag.setDouble("loss", link.getValue());
            links.appendTag(tag);
        }
        nbt.setTag("links", links);
        return nbt;
    }

    private static ICConnectionPoint readPoint(NBTTagCompound tag, String posKey, String indexKey) { return new ICConnectionPoint(BlockPos.fromLong(tag.getLong(posKey)), tag.getInteger(indexKey)); }

    private static void writePoint(NBTTagCompound tag, String posKey, String indexKey, ICConnectionPoint point) {
        tag.setLong(posKey, point.getPos().toLong());
        tag.setInteger(indexKey, point.getIndex());
    }

    private static Vec3d readOffset(NBTTagCompound tag, String key) {
        NBTTagList list = tag.getTagList(key, Constants.NBT.TAG_DOUBLE);
        return new Vec3d(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2));
    }

    private static void writeOffset(NBTTagCompound tag, String key, Vec3d offset) {
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagDouble(offset.x));
        list.appendTag(new NBTTagDouble(offset.y));
        list.appendTag(new NBTTagDouble(offset.z));
        tag.setTag(key, list);
    }
}
