package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.api.wires.ICWireRole;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.common.energy.wires.ICWireCoils;

import blusunrize.immersiveengineering.api.tool.IElectricEquipment.ElectricSource;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.IEnergyWire;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler.IShockingWire;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class ICIEWireType extends WireType {
    private final ICWireType type;

    private ICIEWireType(@Nonnull ICWireType type) { this.type = type; }

    @Nonnull public static ICIEWireType of(@Nonnull ICWireType type) {
        if (type.getCategory() == null || type.getRole() != ICWireRole.ENERGY) { return new ICIEWireType(type); }
        return type.getShockDamage() > 0 ? new Shocking(type) : new Energy(type);
    }

    @Nonnull public ICWireType icType() { return type; }

    @Override public String getUniqueName() { return type.getUniqueName(); }

    @Override public int getColour(Connection connection) { return connection == null ? type.getColor() : type.colorFor(IEWireBridge.wire(connection)); }

    @Override public double getSlack() { return type.getSlack(); }

    @Override public int getMaxLength() { return type.getMaxLength(); }

    @Override public ItemStack getWireCoil(Connection con) { return ICWireCoils.coilFor(type, (int)con.getLength()); }

    @Override public double getRenderDiameter() { return type.getRenderDiameter(); }

    @Nonnull @Override public String getCategory() {
        ICWireCategory category = type.getCategory();
        return category == null ? type.getUniqueName() : category.getName();
    }

    private static class Energy extends ICIEWireType implements IEnergyWire {
        private Energy(@Nonnull ICWireType type) { super(type); }

        private double lossRatio() { return icType().getLossPerBlock() * getMaxLength(); }

        @Override public int getTransferRate() { return icType().getTransferRate(); }

        @Override public double getBasicLossRate(Connection c) { return getMaxLength() > 0 ? lossRatio() * c.getLength() / getMaxLength() : 0; }

        @Override public double getLossRate(Connection c, int transferred) { return 0; }
    }

    private static final class Shocking extends Energy implements IShockingWire {
        private final IEShock shock;

        private Shocking(@Nonnull ICWireType type) {
            super(type);
            this.shock = IEShock.of(type.getCategory());
        }

        @Override public double getDamageRadius() { return shock.radius; }

        @Override public ElectricSource getElectricSource() { return shock.source; }

        @Override public float getDamageAmount(Entity e, Connection c, int energy) { return shock.factor * energy / getTransferRate() * 8; }

        @Override public Collection<ResourceLocation> getRequestedHandlers() { return List.of(WireDamageHandler.ID); }
    }

    private enum IEShock {
        COPPER(.05, .5F, 2F),
        ELECTRUM(.1, 1F, 5F),
        STEEL(.3, 1.5F, 15F);
        private final double radius;
        private final ElectricSource source;
        private final float factor;

        IEShock(double radius, float level, float factor) {
            this.radius = radius;
            this.source = new ElectricSource(level);
            this.factor = factor;
        }

        @Nonnull private static IEShock of(@Nullable ICWireCategory category) {
            if (category == ICWireCategory.MV) { return ELECTRUM; }
            return category == ICWireCategory.HV ? STEEL : COPPER;
        }
    }
}
