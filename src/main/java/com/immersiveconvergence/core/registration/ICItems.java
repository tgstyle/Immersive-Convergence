package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ICItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, ICLib.MODID);

    public static void init(IEventBus bus) { REGISTER.register(bus); }
}
