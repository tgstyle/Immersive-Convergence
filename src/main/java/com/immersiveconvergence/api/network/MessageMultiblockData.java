package com.immersiveconvergence.api.network;

import com.immersiveconvergence.api.multiblock.MultiblockDataLoader;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public final class MessageMultiblockData implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageMultiblockData> TYPE = new CustomPacketPayload.Type<>(ICLib.rl("multiblockdata"));
    public static final StreamCodec<FriendlyByteBuf, MessageMultiblockData> STREAM_CODEC = StreamCodec.of((buf, message) -> buf.writeByteArray(message.payload), buf -> new MessageMultiblockData(buf.readByteArray()));
    private final byte[] payload;

    public MessageMultiblockData(byte[] payload) { this.payload = payload; }

    @Override @Nonnull public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageMultiblockData message, IPayloadContext context) { context.enqueueWork(() -> MultiblockDataLoader.applyEncoded(message.payload)); }
}
