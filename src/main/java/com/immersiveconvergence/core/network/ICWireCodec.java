package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireType;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ICWireCodec {
    private ICWireCodec() {}

    public static void write(@Nonnull FriendlyByteBuf buf, @Nonnull Collection<ICWire> wires) {
        buf.writeInt(wires.size());
        for (ICWire wire : wires) {
            writeEnd(buf, wire.getA(), wire.getOffsetA());
            writeEnd(buf, wire.getB(), wire.getOffsetB());
            buf.writeUtf(wire.getType().getUniqueName());
        }
    }

    @Nonnull public static List<ICWire> read(@Nonnull FriendlyByteBuf buf) {
        int count = buf.readInt();
        if (count == 0) { return Collections.emptyList(); }
        List<ICWire> wires = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ICConnectionPoint a = readPoint(buf);
            Vec3 offsetA = readOffset(buf);
            ICConnectionPoint b = readPoint(buf);
            Vec3 offsetB = readOffset(buf);
            ICWireType type = ICWireType.byName(buf.readUtf());
            if (type != null) { wires.add(new ICWire(a, offsetA, b, offsetB, type)); }
        }
        return wires;
    }

    private static void writeEnd(FriendlyByteBuf buf, ICConnectionPoint point, Vec3 offset) {
        buf.writeLong(point.position().asLong());
        buf.writeInt(point.index());
        buf.writeDouble(offset.x);
        buf.writeDouble(offset.y);
        buf.writeDouble(offset.z);
    }

    private static ICConnectionPoint readPoint(FriendlyByteBuf buf) { return new ICConnectionPoint(BlockPos.of(buf.readLong()), buf.readInt()); }

    private static Vec3 readOffset(FriendlyByteBuf buf) { return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()); }
}
