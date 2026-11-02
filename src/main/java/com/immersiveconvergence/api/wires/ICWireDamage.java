package com.immersiveconvergence.api.wires;

import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public final class ICWireDamage {
    public static final DamageSource SHOCK = new DamageSource("immersiveconvergence.wire_shock").setDamageBypassesArmor();
    private static final String IE_SHOCK = "ieWireShock";

    private ICWireDamage() {}

    public static boolean isShock(@Nonnull DamageSource source) { return SHOCK.damageType.equals(source.damageType) || IE_SHOCK.equals(source.damageType); }
}
