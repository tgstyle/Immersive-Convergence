package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public record ICTemplateWire(@Nonnull BlockPos first, @Nonnull ICTargetingInfo firstTarget, @Nonnull BlockPos second, @Nonnull ICTargetingInfo secondTarget, @Nonnull String type) {
    private static final String FIRST = "first";
    private static final String SECOND = "second";
    private static final String POS = "pos";
    private static final String TARGET = "target";
    private static final String TYPE = "type";
    public ICTemplateWire { first = first.immutable(); second = second.immutable(); }

    @Nonnull public static ICTemplateWire readFromNBT(@Nonnull CompoundTag tag) {
        CompoundTag firstEnd = tag.getCompound(FIRST);
        CompoundTag secondEnd = tag.getCompound(SECOND);
        return new ICTemplateWire(readPos(firstEnd), ICTargetingInfo.readFromNBT(firstEnd.getCompound(TARGET)), readPos(secondEnd), ICTargetingInfo.readFromNBT(secondEnd.getCompound(TARGET)), tag.getString(TYPE));
    }

    private static BlockPos readPos(CompoundTag end) {
        ListTag pos = end.getList(POS, Tag.TAG_INT);
        return new BlockPos(pos.getInt(0), pos.getInt(1), pos.getInt(2));
    }

    public void writeToNBT(@Nonnull CompoundTag tag) {
        tag.put(FIRST, end(first, firstTarget));
        tag.put(SECOND, end(second, secondTarget));
        tag.putString(TYPE, type);
    }

    private static CompoundTag end(BlockPos pos, ICTargetingInfo target) {
        ListTag position = new ListTag();
        position.add(IntTag.valueOf(pos.getX()));
        position.add(IntTag.valueOf(pos.getY()));
        position.add(IntTag.valueOf(pos.getZ()));
        CompoundTag targetTag = new CompoundTag();
        target.writeToNBT(targetTag);
        CompoundTag end = new CompoundTag();
        end.put(POS, position);
        end.put(TARGET, targetTag);
        return end;
    }

    @Nonnull public ICConnectResult place(@Nonnull Level level, @Nonnull UnaryOperator<BlockPos> toWorld, @Nonnull UnaryOperator<ICTargetingInfo> targetToWorld) {
        ICWireType wire = ICWireType.byName(type);
        if (wire == null) { return ICConnectResult.WRONG_TYPE; }
        IICWireNetwork network = ICWires.network(level);
        ICTargetingInfo worldFirstTarget = targetToWorld.apply(firstTarget);
        ICTargetingInfo worldSecondTarget = targetToWorld.apply(secondTarget);
        ICConnectionPoint firstPoint = network.pointFor(toWorld.apply(first), wire, worldFirstTarget);
        ICConnectionPoint secondPoint = network.pointFor(toWorld.apply(second), wire, worldSecondTarget);
        if (firstPoint == null || secondPoint == null) { return ICConnectResult.NO_CONNECTOR; }
        return network.connect(firstPoint, worldFirstTarget, secondPoint, worldSecondTarget, wire);
    }

    @Nonnull public BlockPos getFirst() { return first; }

    @Nonnull public ICTargetingInfo getFirstTarget() { return firstTarget; }

    @Nonnull public BlockPos getSecond() { return second; }

    @Nonnull public ICTargetingInfo getSecondTarget() { return secondTarget; }

    @Nonnull public String getType() { return type; }
}
