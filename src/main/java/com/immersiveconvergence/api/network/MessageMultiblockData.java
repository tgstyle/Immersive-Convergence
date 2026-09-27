package com.immersiveconvergence.api.network;

import com.immersiveconvergence.api.multiblock.MultiblockDataLoader;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class MessageMultiblockData implements INetworkMessage {
    private final byte[] payload;

    public MessageMultiblockData(byte[] payload) { this.payload = payload; }

    public MessageMultiblockData(FriendlyByteBuf buf) { this(buf.readByteArray()); }

    @Override public void toBytes(FriendlyByteBuf buf) { buf.writeByteArray(payload); }

    @Override public void process(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isServer()) { return; }
        context.get().enqueueWork(() -> MultiblockDataLoader.applyEncoded(payload));
    }
}
