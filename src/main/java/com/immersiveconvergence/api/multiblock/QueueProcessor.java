package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.ImmersiveConvergence;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID)
public class QueueProcessor {
    public static final int DISASSEMBLE_QUEUE_SIZE = 8;
    public static java.util.function.BooleanSupplier queueEnabled = () -> true;
    public static final List<QueueProcessor> pendingQueues = new ArrayList<>();
    public static final Set<BlockPos> activeDisassemblies = new HashSet<>();
    private static final Comparator<BlockPos> Y_DESC_COMPARATOR = Comparator.comparingInt(pos -> -pos.getY());
    private static final GameProfile FALLBACK_PROFILE = new GameProfile(UUID.fromString("256cb34d-064f-3b7b-be9f-aa63f5ff7d65"), "[IC-Disassembler]");

    private final WorldServer world;
    private final Deque<BlockPos> queue = new ArrayDeque<>();
    @Nullable private final EntityPlayerMP owner;
    private final boolean dropItems;
    private final BlockPos dropAt;
    private final List<ItemStack> allDrops;
    @Nullable private final BlockPos masterPos;
    private FakePlayer fakePlayer;

    public QueueProcessor(WorldServer world, List<BlockPos> toBreak, @Nullable EntityPlayerMP owner, boolean dropItems, BlockPos dropAt, List<ItemStack> allDrops, @Nullable BlockPos masterPos) {
        this.world = world;
        List<BlockPos> sorted = new ArrayList<>(toBreak);
        sorted.sort(Y_DESC_COMPARATOR);
        if (masterPos != null && sorted.remove(masterPos)) { sorted.add(0, masterPos); }
        this.masterPos = masterPos;
        this.queue.addAll(sorted);
        this.owner = owner;
        this.dropItems = dropItems;
        this.dropAt = dropAt;
        this.allDrops = allDrops;
    }

    public void tick() {
        if (queue.isEmpty()) {
            if (dropItems && !allDrops.isEmpty()) { for (ItemStack stack : allDrops) { world.spawnEntity(new EntityItem(world, dropAt.getX() + 0.5, dropAt.getY() + 0.5, dropAt.getZ() + 0.5, stack)); } }
            allDrops.clear();
            if (masterPos != null) { activeDisassemblies.remove(masterPos); }
            return;
        }
        if (fakePlayer == null) {
            fakePlayer = new FakePlayer(world, owner != null ? owner.getGameProfile() : FALLBACK_PROFILE);
            fakePlayer.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer);
            fakePlayer.interactionManager.setGameType(GameType.CREATIVE);
        }
        BlockPos batchPos = queue.peek();
        if (batchPos != null) { fakePlayer.setPosition(batchPos.getX() + 0.5, batchPos.getY() + 1, batchPos.getZ() + 0.5); }
        for (int i = 0; i < DISASSEMBLE_QUEUE_SIZE && !queue.isEmpty(); ++i) {
            fakePlayer.interactionManager.tryHarvestBlock(queue.poll());
        }
    }

    public boolean isEmpty() { return queue.isEmpty() && allDrops.isEmpty(); }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        List<QueueProcessor> copy = new ArrayList<>(pendingQueues);
        for (QueueProcessor processor : copy) { processor.tick(); }
        pendingQueues.removeIf(QueueProcessor::isEmpty);
    }
}
