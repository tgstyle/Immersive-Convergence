package com.immersiveconvergence.core.proxy;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.client.IICOBJModelCallback;
import com.immersiveconvergence.api.network.BinaryTileSyncMessage;
import com.immersiveconvergence.api.network.TileSyncMessage;
import com.immersiveconvergence.common.blocks.IEOBJCallbackAdapter;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityRotorCreative;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class CommonProxy {

    public void preInit() { }

    public void init() {
        ImmersiveConvergence.packetHandler.registerMessage(TileSyncMessage.HandlerServer.class, TileSyncMessage.class, 0, Side.SERVER);
        ImmersiveConvergence.packetHandler.registerMessage(BinaryTileSyncMessage.HandlerServer.class, BinaryTileSyncMessage.class, 3, Side.SERVER);
    }

    public void postInit() { }

    public void loadComplete() {}

    public boolean isMultiplayerClient() { return false; }

    public void scheduleClient(Runnable task) {}

    @Nullable public World clientWorld() { return null; }

    public void openRotorCreativeGui(TileEntityRotorCreative tile) {}

    public <T> Object objCallbackAdapter(IICOBJModelCallback<T> callback) { return new IEOBJCallbackAdapter<>(callback); }
}
