package com.immersiveconvergence.client;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.network.BinaryTileSyncMessage;
import com.immersiveconvergence.api.network.MessageStopSound;
import com.immersiveconvergence.api.network.TileSyncMessage;
import com.immersiveconvergence.common.CommonProxy;

import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override public void preInit() { super.preInit(); }

    @Override public void init() {
        ImmersiveConvergence.packetHandler.registerMessage(TileSyncMessage.HandlerClient.class, TileSyncMessage.class, 0, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(TileSyncMessage.HandlerServer.class, TileSyncMessage.class, 0, Side.SERVER);
        ImmersiveConvergence.packetHandler.registerMessage(MessageStopSound.HandlerClient.class, MessageStopSound.class, 1, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryTileSyncMessage.HandlerClient.class, BinaryTileSyncMessage.class, 3, Side.CLIENT);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryTileSyncMessage.HandlerServer.class, BinaryTileSyncMessage.class, 3, Side.SERVER);
    }

    @Override public void postInit() { super.postInit(); }
}
