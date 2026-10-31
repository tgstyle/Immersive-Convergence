package com.immersiveconvergence.api.bridge;

import com.immersiveconvergence.api.manual.ICManualPage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICIntegrationBridge {
    void addRenderCacheClearer(@Nonnull Runnable clearer);

    void clearRenderCaches();

    void putManualInt(@Nonnull String key, int value);

    void refreshConfig(@Nonnull FMLPreInitializationEvent event);

    @Nullable Fluid creosote();

    @Nonnull ItemStack cokeOven();

    boolean isWireCoil(@Nonnull ItemStack stack);

    void sendServerNoSpamMessages(@Nonnull EntityPlayer player, @Nonnull ITextComponent... messages);

    void addManualEntry(@Nonnull String name, @Nonnull String category, @Nonnull ICManualPage... pages);
}
