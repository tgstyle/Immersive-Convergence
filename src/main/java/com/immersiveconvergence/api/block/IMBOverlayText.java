package com.immersiveconvergence.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused") public interface IMBOverlayText<State> {
    @Nullable List<Component> getOverlayText(State state, BlockPos posInMultiblock, BlockHitResult absoluteHit, Player player, boolean hammer);
}
