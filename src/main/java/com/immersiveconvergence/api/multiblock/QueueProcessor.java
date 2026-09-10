package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.ImmersiveConvergence;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.ITileDrop;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.common.multiblock.IEMultiblockPartBridge;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID)
public class QueueProcessor {
    public static final int DISASSEMBLE_QUEUE_SIZE = 8;
    public static BooleanSupplier queueEnabled = () -> true;
    public static final List<QueueProcessor> pendingQueues = new ArrayList<>();
    private static final Map<Integer, Set<BlockPos>> activeDisassemblies = new HashMap<>();
    public static BlockPos currentlyBreakingPos = null;
    public static boolean sneakBreaking = false;
    private static final Comparator<BlockPos> Y_DESC_COMPARATOR = Comparator.comparingInt(pos -> -pos.getY());
    private static final GameProfile FALLBACK_PROFILE = new GameProfile(UUID.fromString("256cb34d-064f-3b7b-be9f-aa63f5ff7d65"), "[IC-Disassembler]");

    public static boolean isDisassembling(World world, BlockPos masterPos) {
        Set<BlockPos> active = activeDisassemblies.get(world.provider.getDimension());
        return active != null && active.contains(masterPos);
    }

    private final WorldServer world;
    private final Deque<BlockPos> queue = new ArrayDeque<>();
    @Nullable private final EntityPlayerMP owner;
    private final boolean dropItems;
    private final BlockPos dropAt;
    private final List<ItemStack> allDrops;
    @Nullable private final BlockPos masterPos;
    private FakePlayer fakePlayer;
    private boolean finished = false;

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
            if (masterPos != null) { endDisassembly(world, masterPos); }
            finished = true;
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

    public boolean isEmpty() { return finished; }

    public enum Result { QUEUED, CLEARED, FALLBACK }

    @SubscribeEvent public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote || event.getPlayer() instanceof FakePlayer) { return; }
        boolean multiblock = ICMultiblockPart.of(event.getWorld().getTileEntity(event.getPos())) != null;
        currentlyBreakingPos = multiblock ? event.getPos().toImmutable() : null;
        sneakBreaking = multiblock && event.getPlayer().isSneaking();
    }

    private static void readOnPlacement(@Nullable TileEntity placed, ItemStack stack) {
        if (placed instanceof ITileDrop) { ((ITileDrop)placed).readOnPlacement(null, stack); }
        else if (placed != null && ICMods.immersiveEngineering()) { IEMultiblockPartBridge.readOnPlacement(placed, stack); }
    }

    public static Result handleDisassembly(ICMultiblockPart broken, int[] structureDimensions, boolean dropOriginal) {
        EnumFacing facing = broken.getPartFacing();
        boolean mirrored = broken.isPartMirrored();
        BlockPos startPos = broken.getPartOrigin();
        List<BlockPos> positions = new ArrayList<>(structureDimensions[0] * structureDimensions[1] * structureDimensions[2]);
        for (int h = 0; h < structureDimensions[0]; h++) {
            for (int l = 0; l < structureDimensions[1]; l++) {
                for (int w = 0; w < structureDimensions[2]; w++) {
                    int ww = mirrored ? -w : w;
                    positions.add(startPos.offset(facing, l).offset(facing.rotateY(), ww).add(0, h, 0));
                }
            }
        }
        return handleDisassembly(broken, positions, dropOriginal);
    }

    public static Result handleDisassembly(ICMultiblockPart broken, List<BlockPos> positions, boolean dropOriginal) {
        World world = broken.getWorld();
        if (world.isRemote || broken.isPartUnformed()) { return Result.FALLBACK; }
        BlockPos brokenPos = broken.getPos();
        int[] brokenOffset = broken.getPartOffset();
        BlockPos masterPos = brokenPos.add(-brokenOffset[0], -brokenOffset[1], -brokenOffset[2]);
        if (isDisassembling(world, masterPos)) { return Result.QUEUED; }
        EntityPlayer breakingPlayer = world.getClosestPlayer(masterPos.getX() + 0.5, masterPos.getY() + 0.5, masterPos.getZ() + 0.5, -1, false);
        boolean creative = breakingPlayer != null && breakingPlayer.isCreative();
        boolean templateMode = !queueEnabled.getAsBoolean() || (sneakBreaking && brokenPos.equals(currentlyBreakingPos));
        long time = world.getTotalWorldTime();
        if (templateMode) {
            if (!creative) { return Result.FALLBACK; }
            for (BlockPos pos2 : positions) {
                ICMultiblockPart part = ICMultiblockPart.of(world.getTileEntity(pos2));
                if (part == null) { continue; }
                int[] partOffset = part.getPartOffset();
                Vec3i diff = pos2.subtract(masterPos);
                if (partOffset[0] != diff.getX() || partOffset[1] != diff.getY() || partOffset[2] != diff.getZ()) { continue; }
                if (time == part.getPartDisassemblyTime()) { continue; }
                ItemStack s = part.getPartOriginalBlock();
                part.unformPart();
                if (pos2.equals(brokenPos)) { continue; }
                IBlockState state = ICUtils.getStateFromItemStack(s);
                if (state != null) {
                    world.setBlockState(pos2, state);
                    readOnPlacement(world.getTileEntity(pos2), s);
                }
            }
            return Result.CLEARED;
        }
        List<BlockPos> toBreak = new ArrayList<>();
        List<ItemStack> allDrops = new ArrayList<>();
        for (BlockPos pos2 : positions) {
            ItemStack s = ItemStack.EMPTY;
            boolean breakable = false;
            ICMultiblockPart part = ICMultiblockPart.of(world.getTileEntity(pos2));
            if (part != null) {
                int[] partOffset = part.getPartOffset();
                Vec3i diff = pos2.subtract(masterPos);
                if (partOffset[0] != diff.getX() || partOffset[1] != diff.getY() || partOffset[2] != diff.getZ()) { continue; }
                if (time != part.getPartDisassemblyTime()) {
                    s = part.getPartOriginalBlock();
                    part.unformPart();
                    breakable = true;
                }
            }
            if (pos2.equals(brokenPos)) { s = broken.getPartOriginalBlock(); }
            if (!s.isEmpty()) { allDrops.add(s.copy()); }
            if (breakable && !pos2.equals(brokenPos)) { toBreak.add(pos2); }
        }
        activeDisassemblies.computeIfAbsent(world.provider.getDimension(), dim -> new HashSet<>()).add(masterPos);
        boolean dropItems = !creative && dropOriginal && world.getGameRules().getBoolean("doTileDrops");
        pendingQueues.add(new QueueProcessor((WorldServer)world, toBreak, breakingPlayer instanceof EntityPlayerMP ? (EntityPlayerMP)breakingPlayer : null, dropItems, brokenPos, allDrops, masterPos));
        return Result.QUEUED;
    }

    private static void endDisassembly(World world, BlockPos masterPos) {
        int dim = world.provider.getDimension();
        Set<BlockPos> active = activeDisassemblies.get(dim);
        if (active != null && active.remove(masterPos) && active.isEmpty()) { activeDisassemblies.remove(dim); }
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        if (world.isRemote) { return; }
        activeDisassemblies.remove(world.provider.getDimension());
        pendingQueues.removeIf(processor -> processor.world == world);
    }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || pendingQueues.isEmpty()) { return; }
        List<QueueProcessor> copy = new ArrayList<>(pendingQueues);
        for (QueueProcessor processor : copy) { processor.tick(); }
        pendingQueues.removeIf(QueueProcessor::isEmpty);
    }
}
