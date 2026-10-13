package com.immersiveconvergence.api.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused") public interface IMBOverlayText<State> {
    @Nullable List<Component> getOverlayText(State state, Player player, boolean hammer);
}
