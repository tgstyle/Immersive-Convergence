package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireDamage;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.common.energy.IEWireEvents;
import com.immersiveconvergence.common.util.ICWireCollision;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID)
public final class ICWireWorldEvents {
    private static final int SHOCK_INTERVAL = 10;
    private static final int RECONCILE_INTERVAL = 20;

    private ICWireWorldEvents() {}

    @SubscribeEvent public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        World world = event.getWorld();
        if (ICMods.immersiveEngineering() || world.isRemote) { return; }
        if (event instanceof BlockEvent.MultiPlaceEvent) {
            for (BlockSnapshot snapshot : ((BlockEvent.MultiPlaceEvent)event).getReplacedBlockSnapshots()) { cutThrough(world, snapshot.getPos()); }
        }
        else { cutThrough(world, event.getPos()); }
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (event.phase != TickEvent.Phase.END || world.isRemote) { return; }
        if (ICMods.immersiveEngineering()) {
            IEWireEvents.reset();
            if (world.getTotalWorldTime() % RECONCILE_INTERVAL == 0) { ICWireSavedData.reconcile(world); }
            return;
        }
        if (world.getTotalWorldTime() % SHOCK_INTERVAL != 0) { return; }
        ICWireNetwork network = ICWireSavedData.get(world);
        for (Entity entity : new ArrayList<>(world.loadedEntityList)) {
            if (entity instanceof EntityLivingBase && !entity.isDead) { shock((EntityLivingBase)entity, network); }
        }
    }

    private static void cutThrough(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        AxisAlignedBB local = state.getCollisionBoundingBox(world, pos);
        if (local == null) { return; }
        AxisAlignedBB box = local.offset(pos);
        List<ICWire> hit = new ArrayList<>();
        for (ICWire wire : ICWireCollision.wiresNear(ICWireSavedData.get(world), box)) {
            if (!wire.getA().getPos().equals(pos) && !wire.getB().getPos().equals(pos) && ICWireCollision.hits(wire, box)) { hit.add(wire); }
        }
        if (!hit.isEmpty()) { ICWireRemoval.breakWires(world, hit, new Vec3d(pos).add(0.5, 0.5, 0.5), ICWireCause.OBSTRUCTED); }
    }

    private static void shock(EntityLivingBase entity, ICWireNetwork network) {
        AxisAlignedBB box = entity.getEntityBoundingBox();
        ICWire strongest = null;
        float damage = 0;
        for (ICWire wire : ICWireCollision.wiresNear(network, box)) {
            float strength = wire.getType().getShockDamage();
            if (strength > damage && ICEnergyTransfer.energized(entity.world, wire) && ICWireCollision.hits(wire, box)) {
                strongest = wire;
                damage = strength;
            }
        }
        if (strongest == null) { return; }
        ICWireEvent.Shock shock = new ICWireEvent.Shock(entity.world, entity, strongest, damage);
        if (!MinecraftForge.EVENT_BUS.post(shock) && shock.getDamage() > 0) { entity.attackEntityFrom(ICWireDamage.SHOCK, shock.getDamage()); }
    }
}
