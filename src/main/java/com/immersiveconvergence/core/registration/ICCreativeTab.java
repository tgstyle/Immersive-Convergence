package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ICCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ICLib.MODID);

    static { REGISTER.register("main", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(ICBlocks.ROTOR_CREATIVE.get()))
            .title(Component.translatable("itemGroup." + ICLib.MODID))
            .displayItems((params, output) -> {
                for (ICBlocks.BlockEntry<?> entry : ICBlocks.BlockEntry.ALL_ENTRIES) { output.accept(new ItemStack(entry.get())); }
            })
            .build()); }

    public static void init(IEventBus bus) { REGISTER.register(bus); }
}
