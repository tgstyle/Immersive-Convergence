package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.core.ICCommonConfig;
import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.BlockEvent;
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
    public static final Set<ResourceLocation> MANAGED = new HashSet<>();
    public static BlockPos currentlyBreakingPos = null;
    public static boolean sneakBreaking = false;

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
    private boolean finished = false;

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

    @SubscribeEvent public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide() || event.getPlayer() instanceof FakePlayer) { return; }
        if (!(event.getState().getBlock() instanceof MultiblockPartBlock)) { return; }
        currentlyBreakingPos = event.getPos().immutable();
        sneakBreaking = event.getPlayer().isShiftKeyDown();
    }

    @SuppressWarnings("deprecation") public static boolean disassemble(ServerLevel serverLevel, List<StructureBlockInfo> structure, BlockPos origin, Mirror mirror, Rotation rot, BlockPos masterPos, boolean handleTemplateMode) {
        BlockPos initiatedAt = currentlyBreakingPos;
        boolean templateMode = sneakBreaking || ICCommonConfig.disassemblyMode == DisassemblyMode.TEMPLATE_BLOCKS;
        if (templateMode && !handleTemplateMode) { return false; }
        boolean doTileDrops = serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
        ServerPlayer breakingPlayer = (ServerPlayer) serverLevel.getNearestPlayer(masterPos.getX() + 0.5, masterPos.getY() + 0.5, masterPos.getZ() + 0.5, -1.0, e -> true);
        boolean dropItems = doTileDrops;
        if (breakingPlayer != null && breakingPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE) { dropItems = false; }
        ItemStack tool = breakingPlayer != null ? breakingPlayer.getMainHandItem() : ItemStack.EMPTY;
        ItemStack effectiveTool = tool.isEmpty() ? new ItemStack(Items.DIAMOND_PICKAXE) : tool;
        if (!activeDisassemblies.add(masterPos.immutable())) { return true; }
        BlockEntity masterBE = serverLevel.getBlockEntity(masterPos);
        if (masterBE instanceof IMultiblockBE<?> mbBE) { markDisassembling(mbBE.getHelper()); }
        for (StructureBlockInfo info : structure) { prepareBlockForDisassembly(serverLevel, TemplateMultiblock.withSettingsAndOffset(origin, info.pos(), mirror, rot)); }
        BlockPos brokenPos = initiatedAt != null ? initiatedAt : masterPos;
        if (initiatedAt == null && breakingPlayer != null) {
            Vec3 eyePos = breakingPlayer.getEyePosition();
            Vec3 look = breakingPlayer.getViewVector(1.0F);
            double reach = breakingPlayer.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
            Vec3 end = eyePos.add(look.scale(reach + 2));
            ClipContext ctx = new ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, breakingPlayer);
            BlockHitResult hit = serverLevel.clip(ctx);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = hit.getBlockPos();
                for (StructureBlockInfo info : structure) {
                    BlockPos actual = TemplateMultiblock.withSettingsAndOffset(origin, info.pos(), mirror, rot);
                    if (actual.equals(hitPos)) {
                        brokenPos = actual;
                        break;
                    }
                }
            }
            if (brokenPos.equals(masterPos)) {
                double minDist = Double.MAX_VALUE;
                BlockPos closest = null;
                for (StructureBlockInfo info : structure) {
                    BlockPos actual = TemplateMultiblock.withSettingsAndOffset(origin, info.pos(), mirror, rot);
                    double dist = Vec3.atCenterOf(actual).distanceToSqr(eyePos);
                    if (dist < minDist) {
                        minDist = dist;
                        closest = actual;
                    }
                }
                if (closest != null) { brokenPos = closest; }
            }
        }
        List<ItemStack> allDrops = new ArrayList<>();
        List<BlockPos> toBreak = new ArrayList<>();
        LootParams.Builder baseLootBuilder = dropItems ? new LootParams.Builder(serverLevel).withParameter(LootContextParams.TOOL, effectiveTool).withOptionalParameter(LootContextParams.THIS_ENTITY, breakingPlayer) : null;
        if (templateMode) {
            BlockState brokenTemplate = null;
            for (StructureBlockInfo info : structure) {
                BlockPos actualPos = TemplateMultiblock.withSettingsAndOffset(origin, info.pos(), mirror, rot);
                BlockState template = info.state().mirror(mirror).rotate(rot);
                if (actualPos.equals(brokenPos)) { brokenTemplate = template; }
                serverLevel.setBlockAndUpdate(actualPos, template);
            }
            if (initiatedAt != null && brokenTemplate != null && !brokenTemplate.isAir()) {
                if (dropItems) {
                    BlockEntity brokenBE = serverLevel.getBlockEntity(brokenPos);
                    LootParams.Builder params = baseLootBuilder.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(brokenPos)).withOptionalParameter(LootContextParams.BLOCK_ENTITY, brokenBE);
                    try { for (ItemStack s : brokenTemplate.getDrops(params)) { Containers.dropItemStack(serverLevel, brokenPos.getX(), brokenPos.getY(), brokenPos.getZ(), s); } }
                    catch (Exception e) { Containers.dropItemStack(serverLevel, brokenPos.getX(), brokenPos.getY(), brokenPos.getZ(), new ItemStack(brokenTemplate.getBlock())); }
                }
                serverLevel.removeBlock(brokenPos, false);
            }
        }
        else {
            for (StructureBlockInfo info : structure) {
                BlockPos actualPos = TemplateMultiblock.withSettingsAndOffset(origin, info.pos(), mirror, rot);
                BlockState template = info.state().mirror(mirror).rotate(rot);
                toBreak.add(actualPos);
                if (dropItems && !template.isAir()) {
                    BlockEntity templateBE = null;
                    if (template.hasBlockEntity() && template.getBlock() instanceof EntityBlock entityBlock) {
                        try { templateBE = entityBlock.newBlockEntity(actualPos, template); }
                        catch (Exception ignored) { }
                    }
                    LootParams.Builder params = baseLootBuilder.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(actualPos)).withOptionalParameter(LootContextParams.BLOCK_ENTITY, templateBE);
                    try { allDrops.addAll(template.getDrops(params)); }
                    catch (Exception e) { allDrops.add(new ItemStack(template.getBlock())); }
                }
            }
        }
        if (templateMode || toBreak.isEmpty()) { activeDisassemblies.remove(masterPos); }
        else { pendingQueues.add(new QueueProcessor(serverLevel, toBreak, breakingPlayer, dropItems, brokenPos, allDrops, masterPos)); }
        return true;
    }

    public static void prepareBlockForDisassembly(Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) { return; }
        markDisassembling(be);
        if (be instanceof IMultiblockBE<?> multiblockBE) { markDisassembling(multiblockBE.getHelper()); }
    }

    public static boolean markDisassembling(Object target) {
        if (target instanceof IMultiblockBEHelper<?> helper) {
            helper.markDisassembling();
            return true;
        }
        if (target instanceof MachineBlockEntityMaster<?> master) {
            master.markDisassembling();
            return true;
        }
        if (target instanceof MachineBlockEntityDummy<?> dummy) {
            dummy.markDisassembling();
            return true;
        }
        return false;
    }

    @SubscribeEvent public static void onServerTick(ServerTickEvent.Post event) {
        if (pendingQueues.isEmpty()) { return; }
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
            finished = true;
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

    public boolean isEmpty() { return finished; }
}
