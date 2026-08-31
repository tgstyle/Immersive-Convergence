package com.immersiveconvergence.client;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.network.BinaryMessageTileSync;
import com.immersiveconvergence.api.network.MessageStopSound;
import com.immersiveconvergence.api.network.MessageTileSync;
import com.immersiveconvergence.common.CommonProxy;

import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override public void preInit() { super.preInit(); }

    @Override public void init() {
        ImmersiveConvergence.packetHandler.registerMessage(MessageTileSync.HandlerClient.class, MessageTileSync.class, 0, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(MessageTileSync.HandlerServer.class, MessageTileSync.class, 0, Side.SERVER);
        ImmersiveConvergence.packetHandler.registerMessage(MessageStopSound.HandlerClient.class, MessageStopSound.class, 1, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryMessageTileSync.HandlerClient.class, BinaryMessageTileSync.class, 3, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryMessageTileSync.HandlerServer.class, BinaryMessageTileSync.class, 3, Side.SERVER);
    }

    @Override public void postInit() { super.postInit(); }
}
