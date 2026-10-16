package com.immersiveconvergence.api.crafting.conditions;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.core.lib.ICLib;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import javax.annotation.Nonnull;

public final class ICIEEnabledCondition implements ICondition {
    public static final ICIEEnabledCondition INSTANCE = new ICIEEnabledCondition();
    public static final MapCodec<ICIEEnabledCondition> CODEC = MapCodec.unit(INSTANCE);
    private static final DeferredRegister<MapCodec<? extends ICondition>> REGISTER = DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, ICLib.MODID);

    static { REGISTER.register("ie_enabled", () -> CODEC); }

    private ICIEEnabledCondition() {}

    public static void init(IEventBus bus) { REGISTER.register(bus); }

    @Override public boolean test(@Nonnull IContext context) { return ICMods.immersiveEngineering(); }

    @Override public @Nonnull MapCodec<? extends ICondition> codec() { return CODEC; }

    @Override public String toString() { return "ie_enabled"; }
}
