package com.immersiveconvergence.api.wires;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class ICWireEvent extends Event {
    private final World world;

    protected ICWireEvent(@Nonnull World world) { this.world = world; }

    @Nonnull public World getWorld() { return world; }

    public static class Placed extends ICWireEvent {
        private final ICWire wire;
        private final EntityPlayer player;
        private final boolean cancelable;

        public Placed(@Nonnull World world, @Nonnull ICWire wire, @Nullable EntityPlayer player, boolean cancelable) {
            super(world);
            this.wire = wire;
            this.player = player;
            this.cancelable = cancelable;
        }

        @Nonnull public ICWire getWire() { return wire; }

        @Nullable public EntityPlayer getPlayer() { return player; }

        @Override public boolean isCancelable() { return cancelable; }
    }

    public static class Removed extends ICWireEvent {
        private final ICWire wire;
        private final ICWireCause cause;

        public Removed(@Nonnull World world, @Nonnull ICWire wire, @Nonnull ICWireCause cause) {
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

        public ComponentChanged(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, boolean joined) {
            super(world);
            this.first = first;
            this.second = second;
            this.joined = joined;
        }

        @Nonnull public ICConnectionPoint getFirst() { return first; }

        @Nonnull public ICConnectionPoint getSecond() { return second; }

        public boolean isJoined() { return joined; }
    }

    @Cancelable public static class Shock extends ICWireEvent {
        private final EntityLivingBase entity;
        private final ICWire wire;
        private float damage;

        public Shock(@Nonnull World world, @Nonnull EntityLivingBase entity, @Nonnull ICWire wire, float damage) {
            super(world);
            this.entity = entity;
            this.wire = wire;
            this.damage = damage;
        }

        @Nonnull public EntityLivingBase getEntity() { return entity; }

        @Nonnull public ICWire getWire() { return wire; }

        public float getDamage() { return damage; }

        public void setDamage(float damage) { this.damage = Math.max(0F, damage); }
    }
}
