package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.common.blocks.gui.RotorCreativeMenu;
import com.immersiveconvergence.common.blocks.logic.RotorCreativeBlockEntity;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;


public class ICMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ICLib.MODID);

    public static final ArgContainer<RotorCreativeBlockEntity, RotorCreativeMenu> ROTOR_CREATIVE = registerArg("rotor_creative", RotorCreativeMenu::makeServer, RotorCreativeMenu::makeClient);

    public static void init(IEventBus bus) { REGISTER.register(bus); }

    public static <T, C extends AbstractContainerMenu> ArgContainer<T, C> registerArg(String name, IArgContainerConstructor<T, C> container, IClientContainerConstructor<C> client) { return new ArgContainer<>(registerType(name, client), container); }

    private static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> registerType(String name, IClientContainerConstructor<C> client) {
        return REGISTER.register(name, () -> {
            Mutable<MenuType<C>> typeBox = new MutableObject<>();
            MenuType<C> type = IForgeMenuType.create((id, inv, buffer) -> client.construct(typeBox.getValue(), id, inv, buffer));
            typeBox.setValue(type);
            return type;
        });
    }

    public static class ArgContainer<T, C extends AbstractContainerMenu> {
        private final RegistryObject<MenuType<C>> type;
        private final IArgContainerConstructor<T, C> factory;

        private ArgContainer(RegistryObject<MenuType<C>> type, IArgContainerConstructor<T, C> factory) {
            this.type = type;
            this.factory = factory;
        }

        public C create(int windowId, Inventory playerInv, T tile) { return this.factory.construct(this.getType(), windowId, playerInv, tile); }

        public MenuType<C> getType() { return this.type.get(); }
    }

    @FunctionalInterface public interface IArgContainerConstructor<T, C extends AbstractContainerMenu> { C construct(MenuType<C> type, int windowId, Inventory invPlayer, T arg); }

    @FunctionalInterface public interface IClientContainerConstructor<C extends AbstractContainerMenu> { C construct(MenuType<C> type, int windowId, Inventory invPlayer, FriendlyByteBuf buffer); }
}
