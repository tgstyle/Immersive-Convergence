package com.immersiveconvergence.api.network;

import com.immersiveconvergence.core.lib.ICLib;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MessageTileSync(BlockPos pos, CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageTileSync> TYPE = new CustomPacketPayload.Type<>(ICLib.rl("tilesync"));

    public static final StreamCodec<ByteBuf, MessageTileSync> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            MessageTileSync::pos,
            ByteBufCodecs.COMPOUND_TAG,
            MessageTileSync::nbt,
            MessageTileSync::new
    );

    @Override public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageTileSync message, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        context.enqueueWork(() -> {
            Level level = player.level();
            BlockEntity tile = level.getBlockEntity(message.pos());
            if (tile instanceof ITileSyncReceiver receiver) { receiver.receiveMessageFromClient(message.nbt()); }
        });
    }
}
