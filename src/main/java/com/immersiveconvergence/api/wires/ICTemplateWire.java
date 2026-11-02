package com.immersiveconvergence.api.wires;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused") public final class ICTemplateWire {
    private static final String FIRST = "first";
    private static final String SECOND = "second";
    private static final String POS = "pos";
    private static final String TARGET = "target";
    private static final String TYPE = "type";
    private final BlockPos first;
    private final ICTargetingInfo firstTarget;
    private final BlockPos second;
    private final ICTargetingInfo secondTarget;
    private final String type;

    public ICTemplateWire(@Nonnull BlockPos first, @Nonnull ICTargetingInfo firstTarget, @Nonnull BlockPos second, @Nonnull ICTargetingInfo secondTarget, @Nonnull String type) {
        this.first = first.toImmutable();
        this.firstTarget = firstTarget;
        this.second = second.toImmutable();
        this.secondTarget = secondTarget;
        this.type = type;
    }

    @Nonnull public static ICTemplateWire readFromNBT(@Nonnull NBTTagCompound tag) {
        NBTTagCompound firstEnd = tag.getCompoundTag(FIRST);
        NBTTagCompound secondEnd = tag.getCompoundTag(SECOND);
        return new ICTemplateWire(readPos(firstEnd), ICTargetingInfo.readFromNBT(firstEnd.getCompoundTag(TARGET)), readPos(secondEnd), ICTargetingInfo.readFromNBT(secondEnd.getCompoundTag(TARGET)), tag.getString(TYPE));
    }

    private static BlockPos readPos(NBTTagCompound end) {
        NBTTagList pos = end.getTagList(POS, Constants.NBT.TAG_INT);
        return new BlockPos(pos.getIntAt(0), pos.getIntAt(1), pos.getIntAt(2));
    }

    public void writeToNBT(@Nonnull NBTTagCompound tag) {
        tag.setTag(FIRST, end(first, firstTarget));
        tag.setTag(SECOND, end(second, secondTarget));
        tag.setString(TYPE, type);
    }

    private static NBTTagCompound end(BlockPos pos, ICTargetingInfo target) {
        NBTTagList position = new NBTTagList();
        position.appendTag(new NBTTagInt(pos.getX()));
        position.appendTag(new NBTTagInt(pos.getY()));
        position.appendTag(new NBTTagInt(pos.getZ()));
        NBTTagCompound targetTag = new NBTTagCompound();
        target.writeToNBT(targetTag);
        NBTTagCompound end = new NBTTagCompound();
        end.setTag(POS, position);
        end.setTag(TARGET, targetTag);
        return end;
    }

    @Nonnull public ICConnectResult place(@Nonnull World world, @Nonnull UnaryOperator<BlockPos> toWorld, @Nonnull UnaryOperator<ICTargetingInfo> targetToWorld) {
        ICWireType wire = ICWireType.byName(type);
        if (wire == null) { return ICConnectResult.WRONG_TYPE; }
        IICWireNetwork network = ICWires.network(world);
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
