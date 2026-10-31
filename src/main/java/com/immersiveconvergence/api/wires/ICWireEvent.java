package com.immersiveconvergence.api.wires;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class ICWireEvent extends Event {
    private final Level world;

    protected ICWireEvent(@Nonnull Level world) { this.world = world; }

    @Nonnull public Level getWorld() { return world; }

    public static class Placed extends ICWireEvent implements ICancellableEvent {
        private final ICWire wire;
        private final Player player;
        private final boolean cancelable;

        public Placed(@Nonnull Level world, @Nonnull ICWire wire, @Nullable Player player, boolean cancelable) {
            super(world);
            this.wire = wire;
            this.player = player;
            this.cancelable = cancelable;
        }

        @Nonnull public ICWire getWire() { return wire; }

        @Nullable public Player getPlayer() { return player; }

        public boolean isCancelable() { return cancelable; }

        @Override public void setCanceled(boolean canceled) {
            if (!cancelable) { throw new UnsupportedOperationException("This Placed event is not cancelable"); }
            ICancellableEvent.super.setCanceled(canceled);
        }
    }

    public static class Removed extends ICWireEvent {
        private final ICWire wire;
        private final ICWireCause cause;

        public Removed(@Nonnull Level world, @Nonnull ICWire wire, @Nonnull ICWireCause cause) {
            super(world);
            this.wire = wire;
            this.cause = cause;
        }

        @Nonnull public ICWire getWire() { return wire; }

        @Nonnull public ICWireCause getCause() { return cause; }
    }

    public static class ComponentChanged extends ICWireEvent {
        private final ICConnectionPoint first;
        private final ICConnectionPoint second;
        private final boolean joined;

        public ComponentChanged(@Nonnull Level world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, boolean joined) {
            super(world);
            this.first = first;
            this.second = second;
            this.joined = joined;
        }

        @Nonnull public ICConnectionPoint getFirst() { return first; }

        @Nonnull public ICConnectionPoint getSecond() { return second; }

        public boolean isJoined() { return joined; }
    }

    public static class Shock extends ICWireEvent implements ICancellableEvent {
        private final LivingEntity entity;
        private final ICWire wire;
        private float damage;

        public Shock(@Nonnull Level world, @Nonnull LivingEntity entity, @Nonnull ICWire wire, float damage) {
            super(world);
            this.entity = entity;
            this.wire = wire;
            this.damage = damage;
        }

        @Nonnull public LivingEntity getEntity() { return entity; }

        @Nonnull public ICWire getWire() { return wire; }

        public float getDamage() { return damage; }

        public void setDamage(float damage) { this.damage = Math.max(0F, damage); }
    }
}
