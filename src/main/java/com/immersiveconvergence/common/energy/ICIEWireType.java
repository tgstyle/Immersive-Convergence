package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.api.wires.ICWireRole;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.common.energy.wires.ICWireCoils;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.tool.IElectricEquipment.ElectricSource;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICIEWireType extends WireType {
    private static final ElectricSource NO_SHOCK = new ElectricSource(-1);
    private final ICWireType type;
    @Nullable private final IEShock shock;

    public ICIEWireType(@Nonnull ICWireType type) {
        this.type = type;
        this.shock = isEnergyWire() && type.getShockDamage() > 0 ? IEShock.of(type.getCategory()) : null;
    }

    @Nonnull public ICWireType icType() { return type; }

    @Override public String getUniqueName() { return type.getUniqueName(); }

    @Override public double getLossRatio() { return type.getLossPerBlock() * type.getMaxLength(); }

    @Override public int getTransferRate() { return type.getTransferRate(); }

    @Override public int getColour(Connection connection) {
        World world = ImmersiveConvergence.proxy.clientWorld();
        ICWire wire = connection == null || world == null ? null : IEWireView.wireOf(world, connection);
        return wire == null ? type.getColor() : type.colorFor(wire);
    }

    @Override public double getSlack() { return type.getSlack(); }

    @Override @SideOnly(Side.CLIENT) public TextureAtlasSprite getIcon(Connection connection) { return iconDefaultWire; }

    @Override public int getMaxLength() { return type.getMaxLength(); }

    @Override public ItemStack getWireCoil() { return type.getWireCoil(); }

    @Override public ItemStack getWireCoil(Connection con) { return ICWireCoils.coilFor(type, con.length); }

    @Override public double getRenderDiameter() { return type.getRenderDiameter(); }

    @Override public boolean isEnergyWire() { return type.getCategory() != null && type.getRole() == ICWireRole.ENERGY; }

    @Nullable @Override public String getCategory() { return type.getCategory() == null ? null : type.getCategory().getName(); }

    @Override public boolean canCauseDamage() { return shock != null; }

    @Override public double getDamageRadius() { return shock == null ? 0 : shock.radius; }

    @Override public ElectricSource getElectricSource() { return shock == null ? NO_SHOCK : shock.source; }

    public float getDamageFactor() { return shock == null ? 0 : shock.factor; }

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
