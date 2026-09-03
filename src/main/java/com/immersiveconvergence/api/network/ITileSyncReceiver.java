package com.immersiveconvergence.api.network;

import net.minecraft.nbt.NBTTagCompound;

public interface ITileSyncReceiver {
    void receiveMessageFromServer(NBTTagCompound message);

    void receiveMessageFromClient(NBTTagCompound message);
}
