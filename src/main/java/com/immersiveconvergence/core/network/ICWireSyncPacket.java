package com.immersiveconvergence.core.network;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.client.ICClientWires;
import com.immersiveconvergence.common.energy.wires.ICWireSavedData;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID)
public class ICWireSyncPacket implements IMessage {
    private int dimension;
    private List<ICWire> wires;

    public ICWireSyncPacket() {}

    private ICWireSyncPacket(int dimension, Collection<ICWire> wires) {
        this.dimension = dimension;
        this.wires = new ArrayList<>(wires);
    }

    @SubscribeEvent public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.getChunkInstance();
        if (ICMods.immersiveEngineering() || chunk == null) { return; }
        World world = chunk.getWorld();
        Collection<ICWire> watched = ICWireSavedData.get(world).wiresIn(chunk.getPos());
        if (!watched.isEmpty()) { ImmersiveConvergence.packetHandler.sendTo(new ICWireSyncPacket(world.provider.getDimension(), watched), event.getPlayer()); }
    }

    @Override public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt();
        wires = readWires(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension);
        writeWires(buf, wires);
    }

    static void writeWires(@Nonnull ByteBuf buf, @Nonnull Collection<ICWire> written) {
        buf.writeInt(written.size());
        for (ICWire wire : written) {
            writeEnd(buf, wire.getA(), wire.getOffsetA());
            writeEnd(buf, wire.getB(), wire.getOffsetB());
            ByteBufUtils.writeUTF8String(buf, wire.getType().getUniqueName());
        }
    }

    @Nonnull static List<ICWire> readWires(@Nonnull ByteBuf buf) {
        int count = buf.readInt();
        if (count == 0) { return Collections.emptyList(); }
        List<ICWire> read = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ICConnectionPoint a = readPoint(buf);
            Vec3d offsetA = readOffset(buf);
            ICConnectionPoint b = readPoint(buf);
            Vec3d offsetB = readOffset(buf);
            ICWireType type = ICWireType.byName(ByteBufUtils.readUTF8String(buf));
            if (type != null) { read.add(new ICWire(a, offsetA, b, offsetB, type)); }
        }
        return read;
    }

    private static void writeEnd(ByteBuf buf, ICConnectionPoint point, Vec3d offset) {
        buf.writeLong(point.getPos().toLong());
        buf.writeInt(point.getIndex());
        buf.writeDouble(offset.x);
        buf.writeDouble(offset.y);
        buf.writeDouble(offset.z);
    }

    private static ICConnectionPoint readPoint(ByteBuf buf) { return new ICConnectionPoint(BlockPos.fromLong(buf.readLong()), buf.readInt()); }

    private static Vec3d readOffset(ByteBuf buf) { return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()); }

    public static class Handler implements IMessageHandler<ICWireSyncPacket, IMessage> {
        @Override public IMessage onMessage(ICWireSyncPacket message, MessageContext ctx) {
            ImmersiveConvergence.proxy.scheduleClient(() -> ICClientWires.apply(message.dimension, message.wires, Collections.emptyList()));
            return null;
        }
    }
}
