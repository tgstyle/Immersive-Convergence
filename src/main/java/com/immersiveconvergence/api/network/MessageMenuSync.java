package com.immersiveconvergence.api.network;

import com.immersiveconvergence.api.gui.BaseContainerMenu;
import com.immersiveconvergence.api.gui.MenuSyncSerializers;
import com.immersiveconvergence.api.gui.MenuSyncSerializers.DataPair;
import com.immersiveconvergence.core.lib.ICLib;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MessageMenuSync(List<Pair<Integer, DataPair<?>>> synced) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageMenuSync> TYPE = new CustomPacketPayload.Type<>(ICLib.rl("menusync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageMenuSync> STREAM_CODEC = StreamCodec.of((buf, data) -> data.toBytes(buf), MessageMenuSync::new);

    public MessageMenuSync(RegistryFriendlyByteBuf buf) { this(readSynced(buf)); }

    private static List<Pair<Integer, DataPair<?>>> readSynced(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        List<Pair<Integer, DataPair<?>>> synced = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int index = buf.readVarInt();
            DataPair<?> dataPair = MenuSyncSerializers.read(buf);
            synced.add(Pair.of(index, dataPair));
        }
        return synced;
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(synced.size());
        for (Pair<Integer, DataPair<?>> pair : synced) {
            buf.writeVarInt(pair.getFirst());
            pair.getSecond().write(buf);
        }
    }

    @Override @Nonnull public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageMenuSync message, IPayloadContext context) {
        context.enqueueWork(() -> {
            assert Minecraft.getInstance().player != null;
            AbstractContainerMenu currentContainer = Minecraft.getInstance().player.containerMenu;
            if (currentContainer instanceof BaseContainerMenu container) { container.receiveSync(message.synced()); }
        });
    }
}
