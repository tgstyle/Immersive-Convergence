package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICIntegrationBridge;
import com.immersiveconvergence.api.manual.ICManualPage;
import com.immersiveconvergence.common.IEIntegrationBridge;
import com.immersiveconvergence.common.manual.IEManualBridge;
import com.immersiveconvergence.common.util.IEChatBridge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICIntegrationBridge implements IICIntegrationBridge {
    @Override public void addRenderCacheClearer(@Nonnull Runnable clearer) { IEIntegrationBridge.addRenderCacheClearer(clearer); }

    @Override public void clearRenderCaches() { IEIntegrationBridge.clearRenderCaches(); }

    @Override public void putManualInt(@Nonnull String key, int value) { IEIntegrationBridge.putManualInt(key, value); }

    @Override public void refreshConfig(@Nonnull FMLPreInitializationEvent event) { IEIntegrationBridge.refreshConfig(event); }

    @Override @Nullable public Fluid creosote() { return IEIntegrationBridge.creosote(); }

    @Override @Nonnull public ItemStack cokeOven() { return IEIntegrationBridge.cokeOven(); }

    @Override public boolean isWireCoil(@Nonnull ItemStack stack) { return IEIntegrationBridge.isWireCoil(stack); }

    @Override public void sendServerNoSpamMessages(@Nonnull EntityPlayer player, @Nonnull ITextComponent... messages) { IEChatBridge.sendServerNoSpamMessages(player, messages); }

    @Override public void addManualEntry(@Nonnull String name, @Nonnull String category, @Nonnull ICManualPage... pages) { IEManualBridge.addEntry(name, category, pages); }
}
