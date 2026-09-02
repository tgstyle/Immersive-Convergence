package com.immersiveconvergence.api.network;

import com.immersiveconvergence.api.gui.BaseContainerMenu;
import com.immersiveconvergence.api.gui.MenuSyncSerializers;
import com.immersiveconvergence.api.gui.MenuSyncSerializers.DataPair;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MessageMenuSync(List<Pair<Integer, DataPair<?>>> synced) implements INetworkMessage {
    public MessageMenuSync(FriendlyByteBuf buf) { this(readSynced(buf)); }

    private static List<Pair<Integer, DataPair<?>>> readSynced(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<Pair<Integer, DataPair<?>>> synced = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int index = buf.readVarInt();
            DataPair<?> dataPair = MenuSyncSerializers.read(buf);
            synced.add(Pair.of(index, dataPair));
        }
        return synced;
    }

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(synced.size());
        for (Pair<Integer, DataPair<?>> pair : synced) {
            buf.writeVarInt(pair.getFirst());
            pair.getSecond().write(buf);
        }
    }

    @Override public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            assert Minecraft.getInstance().player != null;
            AbstractContainerMenu currentContainer = Minecraft.getInstance().player.containerMenu;
            if (currentContainer instanceof BaseContainerMenu container) { container.receiveSync(synced); }
        });
    }
}
