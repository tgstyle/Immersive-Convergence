package com.immersiveconvergence.api.wires;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public final class ICWireDamage {
    public static final ResourceKey<DamageType> SHOCK = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("immersiveconvergence", "wire_shock"));
    private static final ResourceKey<DamageType> IE_SHOCK = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("immersiveengineering", "wire_shock"));

    private ICWireDamage() {}

    @Nonnull public static DamageSource source(@Nonnull Level level) { return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SHOCK)); }

    public static boolean isShock(@Nonnull DamageSource source) { return source.is(SHOCK) || source.is(IE_SHOCK); }
}
