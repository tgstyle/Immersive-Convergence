package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.network.INetworkMessage;
import com.immersiveconvergence.api.network.MessageMenuSync;
import com.immersiveconvergence.api.network.MessageMenuUpdate;
import com.immersiveconvergence.api.network.MessageTileSync;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.function.Function;

@SuppressWarnings("unused")
public class PacketHandler {
    public static final String NET_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ICLib.rl("main"))
            .networkProtocolVersion(() -> NET_VERSION)
            .serverAcceptedVersions(NET_VERSION::equals)
            .clientAcceptedVersions(NET_VERSION::equals)
            .simpleChannel();

    private static int id = 0;

    public static void initialize() {
        registerMessage(MessageTileSync.class, MessageTileSync::new);
        registerMessage(MessageMenuUpdate.class, MessageMenuUpdate::new);
        registerMessage(MessageMenuSync.class, MessageMenuSync::new);
    }

    public static <T extends INetworkMessage> void registerMessage(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        INSTANCE.registerMessage(id++, type, INetworkMessage::toBytes, decoder, (t, ctx) -> {
            t.process(ctx);
            ctx.get().setPacketHandled(true);
        });
    }

    public static <MSG> void sendToPlayer(Player player, @Nonnull MSG message) { if (player instanceof ServerPlayer serverPlayer) { INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message); } }

    public static <MSG> void sendToServer(MSG message) { if (message != null) { INSTANCE.send(PacketDistributor.SERVER.noArg(), message); } }
}
