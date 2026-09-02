package com.immersiveconvergence.api.network;

import com.immersiveconvergence.api.gui.BaseContainerMenu;
import com.immersiveconvergence.core.lib.ICLib;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MessageMenuUpdate(int windowId, CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageMenuUpdate> TYPE = new CustomPacketPayload.Type<>(ICLib.rl("menuupdate"));
    public static final StreamCodec<ByteBuf, MessageMenuUpdate> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, MessageMenuUpdate::windowId, ByteBufCodecs.COMPOUND_TAG, MessageMenuUpdate::nbt, MessageMenuUpdate::new);

    @Override @Nonnull public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageMenuUpdate message, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        context.enqueueWork(() -> {
            player.resetLastActionTime();
            if (player.containerMenu.containerId == message.windowId()) {
                AbstractContainerMenu menu = player.containerMenu;
                if (menu instanceof BaseContainerMenu container) { container.receiveMessageFromScreen(message.nbt()); }
            }
        });
    }
}
