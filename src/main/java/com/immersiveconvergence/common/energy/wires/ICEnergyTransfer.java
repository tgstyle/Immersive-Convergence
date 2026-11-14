package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireRole;
import com.immersiveconvergence.common.energy.IEWireGraph;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID) public final class ICEnergyTransfer {
    private static final int ACTIVE_TICKS = 20;
    private static final Map<Integer, State> STATES = new HashMap<>();
    private static final Comparator<Node> NODE_ORDER = Comparator.<Node>comparingDouble(node -> node.cost).thenComparingDouble(node -> node.length)
            .thenComparing(node -> node.point).thenComparing(node -> node.parent == null ? node.point : node.parent.point);
    private static final Comparator<Route> ROUTE_ORDER = Comparator.<Route>comparingDouble(route -> route.cost).thenComparingDouble(route -> route.length)
            .thenComparing(route -> route.sink).thenComparing(route -> route.source);

    private ICEnergyTransfer() {}

    public static void offer(@Nonnull World world, @Nonnull ICConnectionPoint point, @Nullable ICConnectionPoint firstHop, @Nonnull IntSupplier available, @Nonnull IntConsumer onMoved) {
        if (world.isRemote) { return; }
        STATES.computeIfAbsent(world.provider.getDimension(), dimension -> new State()).offers.add(new Offer(point, firstHop, available, onMoved));
    }

    public static boolean energized(@Nonnull World world, @Nonnull ICWire wire) {
        State state = STATES.get(world.provider.getDimension());
        WireState wireState = state == null || state.graph == null ? null : state.wires.get(normal(state.graph, wire));
        return wireState != null && wireState.active >= 0 && world.getTotalWorldTime() - wireState.active <= ACTIVE_TICKS;
    }

    public static int throughput(@Nonnull World world, @Nonnull ICWire wire) {
        State state = STATES.get(world.provider.getDimension());
        WireState wireState = state == null || state.graph == null ? null : state.wires.get(normal(state.graph, wire));
        return wireState == null ? 0 : wireState.flow;
    }

    public static int throughput(@Nonnull World world, @Nonnull ICConnectionPoint point) {
        State state = STATES.get(world.provider.getDimension());
        return state == null || state.graph == null ? 0 : state.pointFlow.getOrDefault(state.graph.flowPoint(point), 0);
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) { return; }
        State state = STATES.get(event.world.provider.getDimension());
        if (state == null) { return; }
        if (state.offers.isEmpty()) { state.idle(); }
        else { state.run(event.world); }
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            STATES.remove(event.getWorld().provider.getDimension());
            if (ICMods.immersiveEngineering()) { IEWireGraph.forget(event.getWorld()); }
        }
    }

    private static final class State {
        private final List<Offer> offers = new ArrayList<>();
        private final Map<ICWire, WireState> wires = new HashMap<>();
        private final List<WireState> touched = new ArrayList<>();
        private final List<WireState> flowing = new ArrayList<>();
        private final Map<ICConnectionPoint, Integer> usedPoints = new HashMap<>();
        private final Map<ICConnectionPoint, Integer> pointFlow = new HashMap<>();
        private final Map<ICConnectionPoint, RouteSet> routes = new HashMap<>();
        private final Map<ICConnectionPoint, Map<ICConnectionPoint, Double>> carry = new HashMap<>();
        private ICWireGraph graph;

        private void idle() {
            for (WireState wireState : flowing) { wireState.flow = 0; }
            flowing.clear();
            pointFlow.clear();
        }

        private void run(World world) {
            long now = world.getTotalWorldTime();
            List<Offer> pending = new ArrayList<>(offers);
            offers.clear();
            for (WireState wireState : touched) {
                wireState.used = 0;
                wireState.ready = false;
            }
            touched.clear();
            usedPoints.clear();
            wires.values().removeIf(wireState -> wireState.flow == 0 && (wireState.active < 0 || now - wireState.active > ACTIVE_TICKS));
            pending.sort(Comparator.comparing((Offer offer) -> offer.point));
            graph = ICWireService.graph(world);
            for (Offer offer : pending) { transfer(world, graph, offer); }
            Map<ICWire, Integer> used = new HashMap<>();
            for (WireState wireState : touched) {
                if (wireState.used > 0) { used.put(wireState.wire, wireState.used); }
            }
            graph.settle(used);
            idle();
            for (WireState wireState : touched) {
                if (wireState.used <= 0) { continue; }
                wireState.flow = wireState.used;
                flowing.add(wireState);
            }
            pointFlow.putAll(usedPoints);
        }

        private WireState ready(ICWire wire) {
            WireState wireState = wires.computeIfAbsent(wire, WireState::new);
            if (!wireState.ready) {
                wireState.ready = true;
                touched.add(wireState);
            }
            return wireState;
        }

        private void transfer(World world, ICWireGraph network, Offer offer) {
            if (!world.isBlockLoaded(offer.point.getPos())) { return; }
            int budget = offer.available.getAsInt();
            if (budget <= 0) { return; }
            List<Demand> demands = new ArrayList<>();
            long total = 0;
            long now = world.getTotalWorldTime();
            for (Route route : candidates(network, offer)) {
                if (route.wake > now) { continue; }
                ICEnergyNode sink = network.node(world, route.sink);
                int request = sink == null || !sink.isEnergyOutput() ? 0 : sink.outputEnergy(Integer.MAX_VALUE, true);
                if (request <= 0) {
                    route.sleep(now);
                    continue;
                }
                route.dormant = 0;
                double held = carryOf(route);
                int cost = (int)Math.min(residual(route), Math.ceil((request - held) / route.keep));
                if (cost <= 0) { continue; }
                demands.add(new Demand(route, sink, cost));
                total += cost;
            }
            int left = budget;
            for (Demand demand : demands) {
                int share = total <= budget ? demand.cost : (int)(budget * (long)demand.cost / total);
                left -= deliver(world, demand, Math.min(share, left));
            }
            for (Demand demand : demands) {
                if (left <= 0) { break; }
                left -= deliver(world, demand, Math.min(demand.cost - demand.charged, left));
            }
            if (left < budget) { offer.onMoved.accept(budget - left); }
        }

        private List<Route> candidates(ICWireGraph network, Offer offer) {
            RouteSet set = routesFrom(network, offer.point);
            if (set == null) { return Collections.emptyList(); }
            if (offer.firstHop == null) { return set.unique; }
            Map<BlockPos, Route> bySink = new LinkedHashMap<>();
            for (Route route : set.ordered) {
                if (network.sameHop(offer.firstHop, route.firstHop)) { bySink.putIfAbsent(route.sink.getPos(), route); }
            }
            return new ArrayList<>(bySink.values());
        }

        @Nullable private RouteSet routesFrom(ICWireGraph network, ICConnectionPoint source) {
            Integer component = network.component(source);
            if (component == null) {
                routes.remove(source);
                return null;
            }
            int version = network.version(component);
            RouteSet cached = routes.get(source);
            if (cached != null && cached.component == component && cached.version == version) { return cached; }
            RouteSet found = new RouteSet(source, component, version, search(network, source));
            routes.put(source, found);
            return found;
        }

        private int deliver(World world, Demand demand, int offered) {
            Route route = demand.route;
            int amount = Math.min(offered, residual(route));
            if (amount <= 0) { return 0; }
            double held = carryOf(route);
            int due = (int)Math.floor(amount * route.keep + held);
            int accepted = due > 0 ? demand.sink.outputEnergy(due, false) : 0;
            int charged = accepted >= due ? amount : (accepted <= 0 ? 0 : Math.min(amount, (int)Math.ceil((accepted - held) / route.keep)));
            if (charged <= 0) { return 0; }
            setCarry(route, charged * route.keep + held - Math.min(accepted, due));
            long now = world.getTotalWorldTime();
            for (ICWire key : route.keys) {
                WireState wireState = ready(key);
                wireState.used += charged;
                wireState.active = now;
            }
            for (ICConnectionPoint point : route.points) { usedPoints.merge(point, charged, Integer::sum); }
            for (int i = 0; i < route.relays.size(); i++) {
                ICEnergyNode relay = graph.node(world, route.relays.get(i));
                if (relay != null) { relay.onEnergyPassthrough((int)Math.floor(charged * route.relayKeep.get(i))); }
            }
            demand.charged += charged;
            return charged;
        }

        private int residual(Route route) {
            int residual = Integer.MAX_VALUE;
            for (int i = 0; i < route.wires.size(); i++) {
                ICWire wire = route.wires.get(i);
                residual = Math.min(residual, wire.getType().getTransferRate() - ready(route.keys.get(i)).used - graph.reserved(wire));
            }
            return Math.max(0, residual);
        }

        private double carryOf(Route route) {
            Map<ICConnectionPoint, Double> bySink = carry.get(route.source);
            return bySink == null ? 0 : bySink.getOrDefault(route.sink, 0D);
        }

        private void setCarry(Route route, double value) {
            double clamped = Math.max(0, Math.min(value, Math.nextDown(1D)));
            carry.computeIfAbsent(route.source, point -> new HashMap<>()).put(route.sink, clamped);
        }
    }

    private static List<Route> search(ICWireGraph network, ICConnectionPoint source) {
        Map<ICConnectionPoint, Node> best = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_ORDER);
        Node start = new Node(source, null, null, 0, 0, 0);
        best.put(source, start);
        queue.add(start);
        Set<ICConnectionPoint> settled = new HashSet<>();
        List<Route> found = new ArrayList<>();
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (best.get(node.point) != node || !settled.add(node.point)) { continue; }
            if (node.parent != null) {
                found.add(route(network, source, node));
                if (!network.passes(node.point)) { continue; }
            }
            for (ICWire wire : network.wiresAt(node.point)) {
                if (wire.getType().getRole() == ICWireRole.ENERGY && wire.getType().getTransferRate() > 0) { relax(best, queue, settled, node, wire.other(node.point), wire, network.lossOf(wire), wire.getLength()); }
            }
            for (Map.Entry<ICConnectionPoint, Double> link : network.getLinks(node.point).entrySet()) { relax(best, queue, settled, node, link.getKey(), null, MathHelper.clamp(link.getValue(), 0, 1), 0); }
        }
        return found;
    }

    private static void relax(Map<ICConnectionPoint, Node> best, PriorityQueue<Node> queue, Set<ICConnectionPoint> settled, Node node, ICConnectionPoint next, @Nullable ICWire via, double loss, double length) {
        if (loss >= 1 || settled.contains(next)) { return; }
        Node candidate = new Node(next, node, via, loss, node.cost - Math.log1p(-loss), node.length + length);
        Node known = best.get(next);
        if (known == null || NODE_ORDER.compare(candidate, known) < 0) {
            best.put(next, candidate);
            queue.add(candidate);
        }
    }

    private static ICWire normal(ICWireGraph network, ICWire wire) {
        ICConnectionPoint first = network.flowPoint(wire.getA());
        ICConnectionPoint second = network.flowPoint(wire.getB());
        return first.equals(wire.getA()) && second.equals(wire.getB()) ? wire : new ICWire(first, wire.getOffsetA(), second, wire.getOffsetB(), wire.getType());
    }

    private static Route route(ICWireGraph network, ICConnectionPoint source, Node end) {
        List<Node> path = new ArrayList<>();
        path.add(end);
        for (Node node = end.parent; node != null; node = node.parent) { path.add(node); }
        Collections.reverse(path);
        List<ICWire> wires = new ArrayList<>();
        List<ICWire> keys = new ArrayList<>();
        Map<ICConnectionPoint, Double> relayKeep = new LinkedHashMap<>();
        List<ICConnectionPoint> points = new ArrayList<>();
        points.add(network.flowPoint(source));
        ICConnectionPoint firstHop = null;
        double keep = 1;
        for (int i = 1; i < path.size(); i++) {
            Node node = path.get(i);
            if (node.via != null) {
                wires.add(node.via);
                keys.add(normal(network, node.via));
            }
            keep *= 1 - node.loss;
            BlockPos at = node.point.getPos();
            points.add(network.flowPoint(node.point));
            if (firstHop == null && !at.equals(source.getPos())) { firstHop = node.point; }
            if (i < path.size() - 1 && !at.equals(source.getPos()) && !at.equals(end.point.getPos())) { relayKeep.put(node.point, keep); }
        }
        return new Route(source, end.point, firstHop, end.cost, end.length, keep, wires, keys, points, new ArrayList<>(relayKeep.keySet()), new ArrayList<>(relayKeep.values()));
    }

    private static final class Offer {
        private final ICConnectionPoint point;
        private final ICConnectionPoint firstHop;
        private final IntSupplier available;
        private final IntConsumer onMoved;

        private Offer(ICConnectionPoint point, @Nullable ICConnectionPoint firstHop, IntSupplier available, IntConsumer onMoved) {
            this.point = point;
            this.firstHop = firstHop;
            this.available = available;
            this.onMoved = onMoved;
        }
    }

    private static final class Node {
        private final ICConnectionPoint point;
        private final Node parent;
        private final ICWire via;
        private final double loss;
        private final double cost;
        private final double length;

        private Node(ICConnectionPoint point, @Nullable Node parent, @Nullable ICWire via, double loss, double cost, double length) {
            this.point = point;
            this.parent = parent;
            this.via = via;
            this.loss = loss;
            this.cost = cost;
            this.length = length;
        }
    }

    private static final class Route {
        private final ICConnectionPoint source;
        private final ICConnectionPoint sink;
        private final ICConnectionPoint firstHop;
        private final double cost;
        private final double length;
        private final double keep;
        private final List<ICWire> wires;
        private final List<ICWire> keys;
        private final List<ICConnectionPoint> points;
        private final List<ICConnectionPoint> relays;
        private final List<Double> relayKeep;
        private int dormant = 0;
        private long wake = 0;

        private void sleep(long now) {
            wake = now + Math.min(ACTIVE_TICKS, 2L << dormant);
            dormant = Math.min(dormant + 1, 4);
        }

        private Route(ICConnectionPoint source, ICConnectionPoint sink, ICConnectionPoint firstHop, double cost, double length, double keep, List<ICWire> wires, List<ICWire> keys, List<ICConnectionPoint> points, List<ICConnectionPoint> relays, List<Double> relayKeep) {
            this.keys = keys;
            this.source = source;
            this.sink = sink;
            this.firstHop = firstHop;
            this.cost = cost;
            this.length = length;
            this.keep = keep;
            this.wires = wires;
            this.points = points;
            this.relays = relays;
            this.relayKeep = relayKeep;
        }
    }

    private static final class WireState {
        private final ICWire wire;
        private int used = 0;
        private int flow = 0;
        private long active = -1;
        private boolean ready = false;

        private WireState(ICWire wire) { this.wire = wire; }
    }

    private static final class RouteSet {
        private final int component;
        private final int version;
        private final List<Route> ordered = new ArrayList<>();
        private final List<Route> unique;

        private RouteSet(ICConnectionPoint source, int component, int version, List<Route> routes) {
            this.component = component;
            this.version = version;
            for (Route route : routes) {
                if (!route.sink.getPos().equals(source.getPos())) { ordered.add(route); }
            }
            ordered.sort(ROUTE_ORDER);
            Map<BlockPos, Route> bySink = new LinkedHashMap<>();
            for (Route route : ordered) { bySink.putIfAbsent(route.sink.getPos(), route); }
            this.unique = new ArrayList<>(bySink.values());
        }
    }

    private static final class Demand {
        private final Route route;
        private final ICEnergyNode sink;
        private final int cost;
        private int charged = 0;

        private Demand(Route route, ICEnergyNode sink, int cost) {
            this.route = route;
            this.sink = sink;
            this.cost = cost;
        }
    }
}
