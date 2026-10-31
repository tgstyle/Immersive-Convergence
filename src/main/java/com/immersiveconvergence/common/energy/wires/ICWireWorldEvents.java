package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireDamage;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.common.ie.IEWireEvents;
import com.immersiveconvergence.common.util.ICWireCollision;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ICLib.MODID)
public final class ICWireWorldEvents {
    private static final int SHOCK_INTERVAL = 10;
    private static final int RECONCILE_INTERVAL = 20;

    private ICWireWorldEvents() {}

    @SubscribeEvent public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || ICMods.immersiveEngineering()) { return; }
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multi) {
            multi.getReplacedBlockSnapshots().forEach(snapshot -> cutThrough(level, snapshot.getPos()));
        }
        else { cutThrough(level, event.getPos()); }
    }

    @SubscribeEvent public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) { return; }
        if (ICMods.immersiveEngineering()) {
            IEWireEvents.reset();
            if (level.getGameTime() % RECONCILE_INTERVAL == 0) { ICWireSavedData.reconcile(level); }
            return;
        }
        if (level.getGameTime() % SHOCK_INTERVAL != 0) { return; }
        shockAll(level);
    }

    private static void shockAll(ServerLevel level) {
        ICWireNetwork network = ICWireSavedData.get(level);
        for (Entity entity : entitiesOf(level)) {
            if (entity instanceof LivingEntity living && living.isAlive()) { shock(level, living, network); }
        }
    }

    private static List<Entity> entitiesOf(ServerLevel level) {
        List<Entity> entities = new ArrayList<>();
        level.getAllEntities().forEach(entities::add);
        return entities;
    }

    private static void cutThrough(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        VoxelShape shape = state.getCollisionShape(level, pos);
        if (shape.isEmpty()) { return; }
        List<AABB> boxes = shape.toAabbs().stream().map(box -> box.move(pos)).toList();
        List<ICWire> hit = new ArrayList<>();
        for (ICWire wire : ICWireCollision.wiresNear(ICWireSavedData.get(level), shape.bounds().move(pos))) {
            if (!wire.getA().position().equals(pos) && !wire.getB().position().equals(pos) && boxes.stream().anyMatch(box -> ICWireCollision.hits(wire, box))) { hit.add(wire); }
        }
        if (!hit.isEmpty()) { ICWireRemoval.breakWires(level, hit, Vec3.atCenterOf(pos), ICWireCause.OBSTRUCTED); }
    }

    private static void shock(ServerLevel level, LivingEntity entity, ICWireNetwork network) {
        AABB box = entity.getBoundingBox();
        ICWire strongest = null;
        float damage = 0;
        for (ICWire wire : ICWireCollision.wiresNear(network, box)) {
            float strength = wire.getType().getShockDamage();
            if (strength > damage && ICEnergyTransfer.energized(level, wire) && ICWireCollision.hits(wire, box)) {
                strongest = wire;
                damage = strength;
            }
        }
        if (strongest == null) { return; }
        ICWireEvent.Shock shock = new ICWireEvent.Shock(level, entity, strongest, damage);
        if (!ICWireEventBus.canceled(shock) && shock.getDamage() > 0) { entity.hurt(ICWireDamage.source(level), shock.getDamage()); }
    }
}
