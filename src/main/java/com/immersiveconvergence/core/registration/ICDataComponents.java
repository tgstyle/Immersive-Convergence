package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ICDataComponents {
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ICLib.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COIL_LENGTH = REGISTER.registerComponentType("coil_length", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> COIL_LINK = REGISTER.registerComponentType("coil_link", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG));

    public static void init(IEventBus bus) { REGISTER.register(bus); }
}
