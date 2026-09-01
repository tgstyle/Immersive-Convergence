package com.immersiveconvergence.api.network;

import net.minecraft.nbt.CompoundTag;

public interface ITileSyncReceiver {
    void receiveMessageFromClient(CompoundTag message);
}
