package com.immersiveconvergence.common;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.network.BinaryMessageTileSync;
import com.immersiveconvergence.api.network.MessageTileSync;

import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

    public void preInit() { }

    public void init() {
        ImmersiveConvergence.packetHandler.registerMessage(MessageTileSync.HandlerServer.class, MessageTileSync.class, 0, Side.SERVER);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryMessageTileSync.HandlerServer.class, BinaryMessageTileSync.class, 3, Side.SERVER);
    }

    public void postInit() { }
}
