package com.immersiveconvergence.common;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.network.BinaryTileSyncMessage;
import com.immersiveconvergence.api.network.TileSyncMessage;

import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

    public void preInit() { }

    public void init() {
        ImmersiveConvergence.packetHandler.registerMessage(TileSyncMessage.HandlerServer.class, TileSyncMessage.class, 0, Side.SERVER);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryTileSyncMessage.HandlerServer.class, BinaryTileSyncMessage.class, 3, Side.SERVER);
    }

    public void postInit() { }
}
