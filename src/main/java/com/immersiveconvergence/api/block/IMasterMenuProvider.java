package com.immersiveconvergence.api.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IMasterMenuProvider<T extends BlockEntity & IMasterMenuProvider<T>> extends MenuProvider {
    @Nullable T getGuiMaster();

    boolean canUseGui(Player player);

    default boolean isValid() { return getGuiMaster() != null; }

    @Nonnull default Component getDisplayName() { return Component.literal(""); }
}
