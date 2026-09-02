package com.immersiveconvergence.api.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ArgContainer<T, C extends AbstractContainerMenu> {
    private final Supplier<MenuType<C>> type;
    private final IArgContainerConstructor<T, C> factory;

    protected ArgContainer(Supplier<MenuType<C>> type, IArgContainerConstructor<T, C> factory) {
        this.type = type;
        this.factory = factory;
    }

    public static <T, C extends AbstractContainerMenu> ArgContainer<T, C> register(DeferredRegister<MenuType<?>> register, String name, IArgContainerConstructor<T, C> container, IClientContainerConstructor<C> client) { return new ArgContainer<>(registerType(register, name, client), container); }

    public static <C extends AbstractContainerMenu> Supplier<MenuType<C>> registerType(DeferredRegister<MenuType<?>> register, String name, IClientContainerConstructor<C> client) {
        return register.register(name, () -> {
            Mutable<MenuType<C>> typeBox = new MutableObject<>();
            MenuType<C> type = IMenuTypeExtension.create((id, inv, buffer) -> client.construct(typeBox.getValue(), id, inv, buffer));
            typeBox.setValue(type);
            return type;
        });
    }

    public C create(int windowId, Inventory playerInv, T tile) { return this.factory.construct(this.getType(), windowId, playerInv, tile); }

    public MenuProvider provide(final T arg) {
        return new MenuProvider() {
            @Nonnull public Component getDisplayName() { return Component.empty(); }

            @Nullable public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player) { return ArgContainer.this.create(containerId, inventory, arg); }
        };
    }

    public MenuType<C> getType() { return this.type.get(); }

    @FunctionalInterface public interface IArgContainerConstructor<T, C extends AbstractContainerMenu> { C construct(MenuType<C> type, int windowId, Inventory invPlayer, T arg); }

    @FunctionalInterface public interface IClientContainerConstructor<C extends AbstractContainerMenu> { C construct(MenuType<C> type, int windowId, Inventory invPlayer, FriendlyByteBuf buffer); }
}
