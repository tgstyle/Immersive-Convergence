package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectResult;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.api.wires.IICEnergyConnector;
import com.immersiveconvergence.api.wires.IICWireNetwork;
import com.immersiveconvergence.core.network.ICWireDeltaPacket;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public final class ICWireNetwork implements ICWireGraph, IICWireNetwork {
    private final Map<ICConnectionPoint, Map<ICConnectionPoint, ICWire>> byPoint = new HashMap<>();
    private final Map<ICConnectionPoint, Map<ICConnectionPoint, Double>> links = new HashMap<>();
    private final Map<ChunkPos, Set<ICWire>> byChunk = new HashMap<>();
    private final Map<ICConnectionPoint, Integer> componentOf = new HashMap<>();
    private final Map<BlockPos, Set<ICConnectionPoint>> byPos = new HashMap<>();
    private final Map<Integer, Set<ICConnectionPoint>> components = new HashMap<>();
    private final Map<Integer, Integer> versions = new HashMap<>();
    private final Set<ICConnectionPoint> closed = new HashSet<>();
    private final Runnable onChange;
    private int nextComponent = 0;
    private boolean loading;
    private World world;

    public ICWireNetwork(@Nonnull Runnable onChange) { this.onChange = onChange; }

    void bind(@Nonnull World world) { this.world = world; }

    public boolean addWire(@Nonnull ICWire wire) {
        if (wire.getA().equals(wire.getB()) || wire(wire.getA(), wire.getB()) != null) { return false; }
        boolean joined = insert(wire);
        onChange.run();
        if (joined) { post(new ICWireEvent.ComponentChanged(world, wire.getA(), wire.getB(), true)); }
        return true;
    }

    public void removeWire(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        ICWire wire = wire(first, second);
        if (wire != null) { disconnect(wire); }
    }

    private boolean disconnect(ICWire wire) {
        unlink(wire);
        boolean split = separate(wire.getA(), wire.getB());
        onChange.run();
        return split;
    }

    public void setPasses(@Nonnull ICConnectionPoint point, boolean passes) {
        boolean changed = passes ? closed.remove(point) : closed.add(point);
        if (!changed) { return; }
        Integer component = componentOf.get(point);
        if (component != null) { bump(component); }
        onChange.run();
    }

    public boolean passes(@Nonnull ICConnectionPoint point) { return !closed.contains(point); }

    public void setLink(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, double loss) {
        if (first.equals(second) || !first.getPos().equals(second.getPos())) { return; }
        Double known = getLinkLoss(first, second);
        if (known != null && known == loss) { return; }
        if (known == null) {
            boolean bothWired = byPoint.containsKey(first) && byPoint.containsKey(second);
            if (putLink(first, second, loss) && bothWired) { post(new ICWireEvent.ComponentChanged(world, first, second, true)); }
        }
        else {
            links.get(first).put(second, loss);
            links.get(second).put(first, loss);
            bump(componentOf.get(first));
        }
        onChange.run();
    }

    public void removeLink(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        if (getLinkLoss(first, second) == null) { return; }
        dropEnd(links, first, second);
        dropEnd(links, second, first);
        boolean bothWired = byPoint.containsKey(first) && byPoint.containsKey(second);
        if (separate(first, second) && bothWired) { post(new ICWireEvent.ComponentChanged(world, first, second, false)); }
        onChange.run();
    }

    @Nullable public Double getLinkLoss(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        Map<ICConnectionPoint, Double> linked = links.get(first);
        return linked == null ? null : linked.get(second);
    }

    @Nonnull public Map<ICConnectionPoint, Double> getLinks(@Nonnull ICConnectionPoint point) {
        Map<ICConnectionPoint, Double> linked = links.get(point);
        return linked == null ? Collections.emptyMap() : Collections.unmodifiableMap(linked);
    }

    @Nonnull public Map<ICConnectionPoint[], Double> getAllLinks() {
        Map<ICConnectionPoint[], Double> all = new HashMap<>();
        for (Map.Entry<ICConnectionPoint, Map<ICConnectionPoint, Double>> entry : links.entrySet()) {
            for (Map.Entry<ICConnectionPoint, Double> link : entry.getValue().entrySet()) {
                if (entry.getKey().compareTo(link.getKey()) < 0) { all.put(new ICConnectionPoint[]{entry.getKey(), link.getKey()}, link.getValue()); }
            }
        }
        return all;
    }

    public int getWireCount(@Nonnull BlockPos pos) {
        int count = 0;
        for (ICConnectionPoint point : pointsAt(pos)) { count += wiresAt(point).size(); }
        return count;
    }

    public void forgetBlock(@Nonnull BlockPos pos) {
        for (ICConnectionPoint point : new ArrayList<>(pointsAt(pos))) {
            for (ICConnectionPoint linked : new ArrayList<>(getLinks(point).keySet())) { removeLink(point, linked); }
        }
        if (closed.removeIf(point -> point.getPos().equals(pos))) { onChange.run(); }
    }

    @Nullable @Override public ICWire wire(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        Map<ICConnectionPoint, ICWire> wires = byPoint.get(first);
        return wires == null ? null : wires.get(second);
    }

    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull ICConnectionPoint point) {
        Map<ICConnectionPoint, ICWire> wires = byPoint.get(point);
        return wires == null ? Collections.emptyList() : Collections.unmodifiableCollection(wires.values());
    }

    @Nonnull @Override public Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk) {
        Set<ICWire> wires = byChunk.get(chunk);
        return wires == null ? Collections.emptySet() : Collections.unmodifiableSet(wires);
    }

    @Nonnull public List<ICWire> getAllWires() {
        List<ICWire> all = new ArrayList<>();
        for (Map.Entry<ICConnectionPoint, Map<ICConnectionPoint, ICWire>> entry : byPoint.entrySet()) {
            for (ICWire wire : entry.getValue().values()) {
                if (wire.getA().equals(entry.getKey())) { all.add(wire); }
            }
        }
        return all;
    }

    @Nonnull @Override public Set<ICConnectionPoint> pointsAt(@Nonnull BlockPos pos) {
        Set<ICConnectionPoint> points = byPos.get(pos);
        return points == null ? Collections.emptySet() : Collections.unmodifiableSet(points);
    }

    @Nullable @Override public Integer component(@Nonnull ICConnectionPoint point) { return componentOf.get(point); }

    @Nonnull @Override public Set<ICConnectionPoint> componentPoints(int component) {
        Set<ICConnectionPoint> points = components.get(component);
        return points == null ? Collections.emptySet() : Collections.unmodifiableSet(points);
    }

    @Override public int version(int component) { return versions.getOrDefault(component, 0); }

    @Nullable @Override public IICConnector connector(@Nonnull ICConnectionPoint point) {
        if (world == null || !world.isBlockLoaded(point.getPos())) { return null; }
        TileEntity tile = world.getTileEntity(point.getPos());
        return tile instanceof IICConnector && !tile.isInvalid() ? (IICConnector)tile : null;
    }

    @Nullable @Override public ICConnectionPoint pointFor(@Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) { return ICWiring.INSTANCE.point(world, pos, type, target); }

    @Nonnull @Override public ICConnectResult connect(@Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type) {
        requireServer();
        return ICWireWriter.connect(world, ICWiring.INSTANCE, first, firstTarget, second, secondTarget, type);
    }

    @Override public boolean cut(@Nonnull ICWire wire, @Nonnull ICWireCause cause) {
        requireServer();
        return !cutAll(Collections.singletonList(wire), cause).isEmpty();
    }

    @Nonnull public List<ICWire> cutAll(@Nonnull Collection<ICWire> wires, @Nonnull ICWireCause cause) {
        requireServer();
        List<ICWire> removed = new ArrayList<>();
        for (ICWire wire : new ArrayList<>(wires)) {
            if (detach(wire, cause)) { removed.add(wire); }
        }
        ICWireDeltaPacket.removed(world, removed);
        return removed;
    }

    @Override public void removeAt(@Nonnull BlockPos pos, @Nonnull ICWireCause cause) {
        Set<ICWire> wires = new LinkedHashSet<>();
        for (ICConnectionPoint point : pointsAt(pos)) { wires.addAll(wiresAt(point)); }
        for (ICWire wire : cutAll(wires, cause)) { InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ICWireCoils.coilFor(wire)); }
        forgetBlock(pos);
    }

    private void post(ICWireEvent event) {
        if (world != null && !world.isRemote && !loading && !ICMods.immersiveEngineering()) { MinecraftForge.EVENT_BUS.post(event); }
    }

    private void requireServer() {
        if (world == null || world.isRemote) { throw new IllegalStateException("Wires are written on the server"); }
    }

    private boolean detach(ICWire wire, ICWireCause cause) {
        ICWire found = wire(wire.getA(), wire.getB());
        if (found == null) { return false; }
        boolean split = disconnect(found);
        release(found.getA(), found, cause);
        release(found.getB(), found, cause);
        post(new ICWireEvent.Removed(world, found, cause));
        if (split) { post(new ICWireEvent.ComponentChanged(world, found.getA(), found.getB(), false)); }
        return true;
    }

    private void release(ICConnectionPoint end, ICWire wire, ICWireCause cause) {
        IICConnector connector = world.isBlockLoaded(end.getPos()) ? ICWireRemoval.connectable(world, end.getPos()) : null;
        if (connector != null) { connector.onWireRemoved(end, wire, cause); }
    }

    @Nullable public ICEnergyNode node(@Nonnull World world, @Nonnull ICConnectionPoint point) {
        if (!world.isBlockLoaded(point.getPos())) { return null; }
        TileEntity tile = world.getTileEntity(point.getPos());
        return tile instanceof IICEnergyConnector && !tile.isInvalid() ? ICWireGraph.energyNode((IICEnergyConnector)tile, point) : null;
    }

    public void syncLoaded(@Nonnull IICConnector connector) {
        loading = true;
        try { sync(connector); }
        finally { loading = false; }
    }

    public void sync(@Nonnull IICConnector connector) {
        List<ICConnectionPoint> points = new ArrayList<>(connector.points());
        Collection<ICInternalLink> internal = connector.links();
        for (ICConnectionPoint point : points) { setPasses(point, connector.passes(point)); }
        for (ICInternalLink link : internal) { setLink(link.getFirst(), link.getSecond(), link.getLoss()); }
        for (int first = 0; first < points.size(); first++) {
            for (int second = first + 1; second < points.size(); second++) {
                if (!linked(internal, points.get(first), points.get(second))) { removeLink(points.get(first), points.get(second)); }
            }
        }
    }

    private static boolean linked(Collection<ICInternalLink> internal, ICConnectionPoint a, ICConnectionPoint b) {
        for (ICInternalLink link : internal) {
            if ((link.getFirst().equals(a) && link.getSecond().equals(b)) || (link.getFirst().equals(b) && link.getSecond().equals(a))) { return true; }
        }
        return false;
    }

    @Nonnull public Map<ICConnectionPoint, Integer> getComponents() { return Collections.unmodifiableMap(componentOf); }

    @Nonnull public Set<ICConnectionPoint> getClosedPoints() { return Collections.unmodifiableSet(closed); }

    public int getNextComponent() { return nextComponent; }

    void load(int next, @Nonnull Map<ICConnectionPoint, Integer> savedComponents, @Nonnull Collection<ICConnectionPoint> savedClosed,
              @Nonnull Collection<ICWire> wires, @Nonnull Map<ICConnectionPoint[], Double> savedLinks, @Nonnull Collection<ICConnectionPoint[]> dropped) {
        nextComponent = next;
        for (Map.Entry<ICConnectionPoint, Integer> entry : savedComponents.entrySet()) {
            int component = entry.getValue();
            componentOf.put(entry.getKey(), component);
            byPos.computeIfAbsent(entry.getKey().getPos(), pos -> new HashSet<>()).add(entry.getKey());
            components.computeIfAbsent(component, id -> new HashSet<>()).add(entry.getKey());
            nextComponent = Math.max(nextComponent, component + 1);
        }
        closed.addAll(savedClosed);
        for (ICWire wire : wires) {
            if (!wire.getA().equals(wire.getB()) && wire(wire.getA(), wire.getB()) == null) { insert(wire); }
        }
        for (Map.Entry<ICConnectionPoint[], Double> link : savedLinks.entrySet()) {
            ICConnectionPoint first = link.getKey()[0];
            ICConnectionPoint second = link.getKey()[1];
            if (!first.equals(second) && first.getPos().equals(second.getPos()) && getLinkLoss(first, second) == null) { putLink(first, second, link.getValue()); }
        }
        for (ICConnectionPoint point : new ArrayList<>(componentOf.keySet())) {
            if (!connected(point)) { detach(point); }
        }
        for (ICConnectionPoint[] pair : dropped) {
            if (connected(pair[0]) && connected(pair[1]) && componentOf.get(pair[0]).equals(componentOf.get(pair[1]))) { split(pair[0], pair[1]); }
        }
    }

    private boolean insert(ICWire wire) {
        byPoint.computeIfAbsent(wire.getA(), point -> new HashMap<>()).put(wire.getB(), wire);
        byPoint.computeIfAbsent(wire.getB(), point -> new HashMap<>()).put(wire.getA(), wire);
        for (ChunkPos chunk : wire.getChunks()) { byChunk.computeIfAbsent(chunk, pos -> new HashSet<>()).add(wire); }
        return connect(wire.getA(), wire.getB());
    }

    private boolean putLink(ICConnectionPoint first, ICConnectionPoint second, double loss) {
        links.computeIfAbsent(first, point -> new HashMap<>()).put(second, loss);
        links.computeIfAbsent(second, point -> new HashMap<>()).put(first, loss);
        return connect(first, second);
    }

    private boolean connect(ICConnectionPoint a, ICConnectionPoint b) {
        int first = join(a);
        int second = join(b);
        if (first != second) {
            merge(first, second);
            return true;
        }
        bump(first);
        return false;
    }

    private boolean connected(ICConnectionPoint point) { return byPoint.containsKey(point) || links.containsKey(point); }

    private void unlink(ICWire wire) {
        dropEnd(byPoint, wire.getA(), wire.getB());
        dropEnd(byPoint, wire.getB(), wire.getA());
        for (ChunkPos chunk : wire.getChunks()) {
            Set<ICWire> wires = byChunk.get(chunk);
            if (wires == null) { continue; }
            wires.remove(wire);
            if (wires.isEmpty()) { byChunk.remove(chunk); }
        }
    }

    private static <V> void dropEnd(Map<ICConnectionPoint, Map<ICConnectionPoint, V>> edges, ICConnectionPoint from, ICConnectionPoint to) {
        Map<ICConnectionPoint, V> ends = edges.get(from);
        if (ends == null) { return; }
        ends.remove(to);
        if (ends.isEmpty()) { edges.remove(from); }
    }

    private int join(ICConnectionPoint point) {
        Integer component = componentOf.get(point);
        if (component != null) { return component; }
        int created = nextComponent++;
        componentOf.put(point, created);
        byPos.computeIfAbsent(point.getPos(), pos -> new HashSet<>()).add(point);
        components.computeIfAbsent(created, id -> new HashSet<>()).add(point);
        return created;
    }

    private void merge(int first, int second) {
        Set<ICConnectionPoint> firstPoints = components.get(first);
        Set<ICConnectionPoint> secondPoints = components.get(second);
        boolean firstKeeps = firstPoints.size() >= secondPoints.size();
        int kept = firstKeeps ? first : second;
        int absorbed = firstKeeps ? second : first;
        Set<ICConnectionPoint> moved = firstKeeps ? secondPoints : firstPoints;
        for (ICConnectionPoint point : moved) { componentOf.put(point, kept); }
        components.get(kept).addAll(moved);
        components.remove(absorbed);
        versions.remove(absorbed);
        bump(kept);
    }

    private boolean separate(ICConnectionPoint a, ICConnectionPoint b) {
        boolean bareA = !connected(a);
        boolean bareB = !connected(b);
        int component = componentOf.get(a);
        if (bareA) { detach(a); }
        if (bareB) { detach(b); }
        if (!bareA && !bareB) { return split(a, b); }
        if (components.containsKey(component)) { bump(component); }
        return true;
    }

    private void detach(ICConnectionPoint point) {
        Integer component = componentOf.remove(point);
        if (component == null) { return; }
        Set<ICConnectionPoint> atPos = byPos.get(point.getPos());
        atPos.remove(point);
        if (atPos.isEmpty()) { byPos.remove(point.getPos()); }
        Set<ICConnectionPoint> points = components.get(component);
        points.remove(point);
        if (points.isEmpty()) {
            components.remove(component);
            versions.remove(component);
        }
    }

    private boolean split(ICConnectionPoint a, ICConnectionPoint b) {
        Set<ICConnectionPoint> seenA = new HashSet<>();
        Set<ICConnectionPoint> seenB = new HashSet<>();
        ArrayDeque<ICConnectionPoint> queueA = new ArrayDeque<>();
        ArrayDeque<ICConnectionPoint> queueB = new ArrayDeque<>();
        seenA.add(a);
        queueA.add(a);
        seenB.add(b);
        queueB.add(b);
        int component = componentOf.get(a);
        while (true) {
            if (queueA.isEmpty()) {
                relabel(component, seenA);
                return true;
            }
            if (expand(queueA, seenA, seenB)) { break; }
            if (queueB.isEmpty()) {
                relabel(component, seenB);
                return true;
            }
            if (expand(queueB, seenB, seenA)) { break; }
        }
        bump(component);
        return false;
    }

    private boolean expand(ArrayDeque<ICConnectionPoint> queue, Set<ICConnectionPoint> seen, Set<ICConnectionPoint> other) {
        ICConnectionPoint point = queue.poll();
        return reaches(byPoint.get(point), queue, seen, other) || reaches(links.get(point), queue, seen, other);
    }

    private static boolean reaches(@Nullable Map<ICConnectionPoint, ?> ends, ArrayDeque<ICConnectionPoint> queue, Set<ICConnectionPoint> seen, Set<ICConnectionPoint> other) {
        if (ends == null) { return false; }
        for (ICConnectionPoint next : ends.keySet()) {
            if (other.contains(next)) { return true; }
            if (seen.add(next)) { queue.add(next); }
        }
        return false;
    }

    private void relabel(int from, Set<ICConnectionPoint> part) {
        int created = nextComponent++;
        for (ICConnectionPoint point : part) { componentOf.put(point, created); }
        components.get(from).removeAll(part);
        components.put(created, part);
        bump(from);
        bump(created);
    }

    private void bump(int component) { versions.merge(component, 1, Integer::sum); }
}
