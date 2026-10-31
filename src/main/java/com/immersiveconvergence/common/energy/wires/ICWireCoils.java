package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireLink;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.ICConnectResult;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.api.wires.IICWireCoil;
import com.immersiveconvergence.common.energy.IEWireBridge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public final class ICWireCoils {
    private ICWireCoils() {}

    @Nonnull public static ItemStack coilFor(@Nonnull ICWire wire) { return coilFor(wire.getType(), (int)Math.ceil(wire.getLength())); }

    @Nonnull public static ItemStack coilFor(@Nonnull ICWireType type, int length) {
        ItemStack coil = type.getWireCoil().copy();
        if (!coil.isEmpty()) { coil.setTagInfo(IICWireCoil.LENGTH, new NBTTagInt(length)); }
        return coil;
    }

    @Nonnull public static EnumActionResult use(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!(stack.getItem() instanceof IICWireCoil)) { return EnumActionResult.PASS; }
        Wiring wiring = wiring();
        if (wiring.missing(world, pos)) { return EnumActionResult.PASS; }
        if (world.isRemote) { return EnumActionResult.SUCCESS; }
        IICWireCoil coil = (IICWireCoil)stack.getItem();
        ICWireType type = coil.getWireType(stack);
        ICTargetingInfo target = new ICTargetingInfo(side, hitX, hitY, hitZ);
        ICConnectionPoint point = wiring.point(world, pos, type, target);
        ICWireLink link = coil.link(stack);
        if (link == null) {
            if (point != null && coilAllows(coil, stack, world, point, target) && wiring.accepts(world, point, type, target)) { new ICWireLink(world.provider.getDimension(), point, target).write(stack); }
            else { refuse(player, "wrongCable"); }
            return EnumActionResult.SUCCESS;
        }
        ICWireLink.clear(stack);
        String refusal = point != null && coilAllows(coil, stack, world, point, target) ? connect(player, world, stack, wiring, coil, type, link, point, target) : "wrongCable";
        if (refusal != null) { refuse(player, refusal); }
        return EnumActionResult.SUCCESS;
    }

    @Nonnull private static Wiring wiring() { return ICMods.immersiveEngineering() ? IEWireBridge.coilWiring() : ICWiring.INSTANCE; }

    private static boolean coilAllows(IICWireCoil coil, ItemStack stack, World world, ICConnectionPoint point, ICTargetingInfo target) {
        IICConnector connector = ICWireRemoval.connectable(world, point.getPos());
        if (connector == null) { return true; }
        ICConnectionPoint targeted = connector.pointAt(target, Vec3i.NULL_VECTOR);
        return targeted == null || coil.canConnect(stack, connector, targeted);
    }

    @Nullable private static String connect(EntityPlayer player, World world, ItemStack stack, Wiring wiring, IICWireCoil coil, ICWireType type, ICWireLink link, ICConnectionPoint point, ICTargetingInfo target) {
        if (link.getDimension() != world.provider.getDimension()) { return "wrongDimension"; }
        boolean creative = player.capabilities.isCreativeMode;
        int[] needed = new int[1];
        ICConnectResult result = ICWireWriter.attempt(world, wiring, point, target, link.getPoint(), link.getTarget(), type, player, length -> {
            needed[0] = length;
            return length <= coil.maxLength(stack) && (creative || coil.remaining(stack) >= length);
        });
        if (result == null) { return "coilTooShort"; }
        if (result == ICConnectResult.OK && !creative) { giveBack(player, coil.consume(stack, needed[0])); }
        return result.key();
    }

    public interface Wiring {
        boolean missing(@Nonnull World world, @Nonnull BlockPos pos);

        @Nullable ICConnectionPoint point(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target);

        boolean accepts(@Nonnull World world, @Nonnull ICConnectionPoint point, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target);

        boolean wired(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second);

        @Nullable ICWire wireBetween(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type);

        @Nonnull Set<BlockPos> ignored(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second);

        boolean place(@Nonnull World world, @Nonnull ICWire wire, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget);
    }

    private static void giveBack(EntityPlayer player, ItemStack rest) {
        if (!rest.isEmpty() && !player.inventory.addItemStackToInventory(rest)) { player.dropItem(rest, false); }
    }

    private static void refuse(EntityPlayer player, String key) { player.sendStatusMessage(new TextComponentTranslation(ICLib.CHAT_INFO + "wire." + key), true); }
}
