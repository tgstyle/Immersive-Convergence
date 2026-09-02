package com.immersiveconvergence.api.network;

import com.immersiveconvergence.api.gui.BaseContainerMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MessageMenuUpdate implements INetworkMessage {
    private final int windowId;
    private final CompoundTag nbt;

    public MessageMenuUpdate(FriendlyByteBuf buf) {
        this.windowId = buf.readByte();
        this.nbt = buf.readNbt();
    }

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeNbt(this.nbt);
    }

    @Override public void process(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ServerPlayer player = ctx.getSender();
        if (player != null) {
            ctx.enqueueWork(() -> {
                player.resetLastActionTime();
                if (player.containerMenu.containerId == this.windowId) {
                    AbstractContainerMenu menu = player.containerMenu;
                    if (menu instanceof BaseContainerMenu container) { container.receiveMessageFromScreen(this.nbt); }
                }
            });
        }
    }
}
