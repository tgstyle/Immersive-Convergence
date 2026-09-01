package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.lib.ICLib;

import com.mojang.authlib.GameProfile;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = ICLib.MODID)
public class QueueProcessor {
    public static final int DISASSEMBLE_QUEUE_SIZE = 8;
    public static final List<QueueProcessor> pendingQueues = new ArrayList<>();
    public static final Set<BlockPos> activeDisassemblies = new HashSet<>();

    private static final Comparator<BlockPos> Y_DESC_COMPARATOR = Comparator.comparingInt(pos -> -pos.getY());
    private static final GameProfile FALLBACK_PROFILE = new GameProfile(UUID.fromString("256cb34d-064f-3b7b-be9f-aa63f5ff7d65"), "[IT-Disassembler]");

    private final ServerLevel level;
    private final Deque<BlockPos> queue = new ArrayDeque<>();
    private final @Nullable ServerPlayer owner;
    private final boolean dropItems;
    private final BlockPos dropAt;
    private final List<ItemStack> allDrops;
    private final BlockPos masterPos;
    private FakePlayer fakePlayer;

    private final Set<ChunkPos> affectedChunks = new HashSet<>();
    private boolean chunksMarked = false;

    public QueueProcessor(ServerLevel level, List<BlockPos> toBreak, @Nullable ServerPlayer owner, boolean dropItems, BlockPos dropAt, List<ItemStack> allDrops, @Nullable BlockPos masterPos) {
        this.level = level;
        List<BlockPos> sorted = new ArrayList<>(toBreak);
        sorted.sort(Y_DESC_COMPARATOR);
        if (masterPos != null && sorted.remove(masterPos)) { sorted.addFirst(masterPos); }
        this.masterPos = masterPos;
        this.queue.addAll(sorted);
        this.owner = owner;
        this.dropItems = dropItems;
        this.dropAt = dropAt;
        this.allDrops = allDrops;

        for (BlockPos pos : toBreak) {
            ChunkPos cp = new ChunkPos(pos);
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) { affectedChunks.add(new ChunkPos(cp.x + dx, cp.z + dz)); }
            }
        }
    }

    @SubscribeEvent public static void onServerTick(ServerTickEvent.Post event) {
        List<QueueProcessor> copy = new ArrayList<>(pendingQueues);
        copy.forEach(QueueProcessor::tick);
        pendingQueues.removeIf(QueueProcessor::isEmpty);
    }

    public void tick() {
        if (!chunksMarked) {
            markChunksForLightUpdate();
            chunksMarked = true;
        }

        if (queue.isEmpty()) {
            if (dropItems && !allDrops.isEmpty()) { for (ItemStack s : allDrops) { Containers.dropItemStack(level, dropAt.getX(), dropAt.getY(), dropAt.getZ(), s); } }
            allDrops.clear();

            doFinalLightingRefresh();
            if (masterPos != null) { activeDisassemblies.remove(masterPos); }
            return;
        }

        if (fakePlayer == null) { fakePlayer = getFakePlayer(level, owner); }

        BlockPos batchPos = queue.peek();
        if (batchPos != null) {
            fakePlayer.setPos(batchPos.getX() + 0.5, batchPos.getY() + 1, batchPos.getZ() + 0.5);
            fakePlayer.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(batchPos));
        }

        for (int i = 0; i < DISASSEMBLE_QUEUE_SIZE && !queue.isEmpty(); ++i) {
            BlockPos pos = queue.poll();
            fakePlayer.gameMode.destroyBlock(pos);
        }
    }

    private static FakePlayer getFakePlayer(ServerLevel level, @Nullable ServerPlayer owner) {
        GameProfile profile = owner != null ? owner.getGameProfile() : FALLBACK_PROFILE;
        FakePlayer fake = new FakePlayer(level, profile);
        fake.gameMode.changeGameModeForPlayer(GameType.CREATIVE);
        return fake;
    }

    private void markChunksForLightUpdate() {
        for (ChunkPos chunk : affectedChunks) {
            LevelChunk levelChunk = level.getChunk(chunk.x, chunk.z);
            levelChunk.setLightCorrect(false);
            levelChunk.setUnsaved(true);
        }
    }

    private void doFinalLightingRefresh() {
        ChunkMap chunkMap = level.getChunkSource().chunkMap;
        ThreadedLevelLightEngine lightEngine = level.getChunkSource().getLightEngine();

        for (ChunkPos chunk : affectedChunks) {
            LevelChunk levelChunk = level.getChunk(chunk.x, chunk.z);
            levelChunk.setLightCorrect(false);
            levelChunk.setUnsaved(true);

            List<ServerPlayer> players = chunkMap.getPlayers(chunk, false);
            players.forEach(p -> p.connection.send(new ClientboundForgetLevelChunkPacket(chunk)));

            ClientboundLevelChunkWithLightPacket packet = new ClientboundLevelChunkWithLightPacket(levelChunk, lightEngine, null, null);
            players.forEach(p -> p.connection.send(packet));

            ChunkHolder holder = chunkMap.getVisibleChunkIfPresent(chunk.toLong());
            if (holder != null) { holder.broadcastChanges(levelChunk); }
        }

        affectedChunks.clear();
        fakePlayer = null;
    }

    public boolean isEmpty() { return queue.isEmpty() && allDrops.isEmpty(); }
}
