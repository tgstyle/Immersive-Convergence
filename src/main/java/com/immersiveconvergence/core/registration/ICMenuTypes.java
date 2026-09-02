package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.api.gui.ArgContainer;
import com.immersiveconvergence.common.blocks.gui.RotorCreativeMenu;
import com.immersiveconvergence.common.blocks.logic.RotorCreativeBlockEntity;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ICMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, ICLib.MODID);
    public static final ArgContainer<RotorCreativeBlockEntity, RotorCreativeMenu> ROTOR_CREATIVE = ArgContainer.register(REGISTER, "rotor_creative", RotorCreativeMenu::makeServer, RotorCreativeMenu::makeClient);

    public static void init(IEventBus bus) { REGISTER.register(bus); }
}
