package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.network.MessageTileSync;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = ICLib.MODID)
public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(MessageTileSync.TYPE, MessageTileSync.STREAM_CODEC, MessageTileSync::handle);
    }

    public static void sendToServer(CustomPacketPayload message) { if (message != null) { PacketDistributor.sendToServer(message); } }
}
