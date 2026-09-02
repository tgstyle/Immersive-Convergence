package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.network.MessageMenuSync;
import com.immersiveconvergence.api.network.MessageMenuUpdate;
import com.immersiveconvergence.api.network.MessageTileSync;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
        registrar.playToServer(MessageMenuUpdate.TYPE, MessageMenuUpdate.STREAM_CODEC, MessageMenuUpdate::handle);
        registrar.playToClient(MessageMenuSync.TYPE, MessageMenuSync.STREAM_CODEC, MessageMenuSync::handle);
    }

    public static void sendToServer(CustomPacketPayload message) { if (message != null) { PacketDistributor.sendToServer(message); } }

    public static void sendToPlayer(Player player, CustomPacketPayload message) { if (player instanceof ServerPlayer serverPlayer && message != null) { PacketDistributor.sendToPlayer(serverPlayer, message); } }
}
