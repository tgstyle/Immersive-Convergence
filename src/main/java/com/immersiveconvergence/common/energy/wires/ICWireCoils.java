package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectResult;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireLink;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.api.wires.IICWireCoil;
import com.immersiveconvergence.common.ie.IEWireBridge;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public final class ICWireCoils {
    private ICWireCoils() {}

    @Nonnull public static ItemStack coilFor(@Nonnull ICWire wire) { return coilFor(wire.getType(), (int)Math.ceil(wire.getLength())); }

    @Nonnull public static ItemStack coilFor(@Nonnull ICWireType type, int length) {
        ItemStack coil = type.getWireCoil().copy();
        if (!coil.isEmpty()) { coil.set(ICDataComponents.COIL_LENGTH, length); }
        return coil;
    }

    @Nonnull public static InteractionResult use(@Nonnull Player player, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull InteractionHand hand, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof IICWireCoil coil)) { return InteractionResult.PASS; }
        Wiring wiring = wiring();
        if (wiring.missing(level, pos)) { return InteractionResult.PASS; }
        if (level instanceof ServerLevel server && player instanceof ServerPlayer serverPlayer) { place(serverPlayer, server, stack, wiring, coil, pos, new ICTargetingInfo(hitX, hitY, hitZ)); }
        return InteractionResult.SUCCESS;
    }

    @Nonnull private static Wiring wiring() { return ICMods.immersiveEngineering() ? IEWireBridge.coilWiring() : ICWiring.INSTANCE; }

    private static void place(ServerPlayer player, ServerLevel level, ItemStack stack, Wiring wiring, IICWireCoil coil, BlockPos pos, ICTargetingInfo target) {
        ICWireType type = coil.getWireType(stack);
        ICConnectionPoint point = wiring.point(level, pos, type, target);
        boolean allowed = point != null && coilAllows(coil, stack, level, point, target);
        ICWireLink link = coil.link(stack);
        if (link == null) {
            if (allowed && !wiring.refuses(level, point, type)) { new ICWireLink(dimension(level), point).write(stack); }
            else { refuse(player, "wrongCable"); }
            return;
        }
        ICWireLink.clear(stack);
        String refusal = allowed ? connect(player, level, stack, wiring, coil, type, link, point) : "wrongCable";
        if (refusal != null) { refuse(player, refusal); }
    }

    private static boolean coilAllows(IICWireCoil coil, ItemStack stack, ServerLevel level, ICConnectionPoint point, ICTargetingInfo target) {
        IICConnector connector = ICWireRemoval.connectable(level, point.position());
        if (connector == null) { return true; }
        ICConnectionPoint targeted = connector.pointAt(target, Vec3i.ZERO);
        return targeted == null || coil.canConnect(stack, connector, targeted);
    }

    @Nullable private static String connect(ServerPlayer player, ServerLevel level, ItemStack stack, Wiring wiring, IICWireCoil coil, ICWireType type, ICWireLink link, ICConnectionPoint point) {
        if (!link.getDimension().equals(dimension(level))) { return "wrongDimension"; }
        boolean creative = player.getAbilities().instabuild;
        int[] needed = new int[1];
        ICConnectResult result = ICWireWriter.attempt(level, wiring, point, link.getPoint(), type, player, length -> {
            needed[0] = length;
            return length <= coil.maxLength(stack) && (creative || coil.remaining(stack) >= length);
        });
        if (result == null) { return "coilTooShort"; }
        if (result == ICConnectResult.OK && !creative) { giveBack(player, coil.consume(stack, needed[0])); }
        return result.key();
    }

    public interface Wiring {
        boolean missing(@Nonnull Level level, @Nonnull BlockPos pos);

        @Nullable ICConnectionPoint point(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target);

        boolean refuses(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint point, @Nonnull ICWireType type);

        boolean wired(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second);

        @Nullable ICWire wireBetween(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, @Nonnull ICWireType type);

        @Nonnull Set<BlockPos> ignored(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second);

        boolean place(@Nonnull ServerLevel level, @Nonnull ICWire wire);
    }

    private static String dimension(ServerLevel level) { return level.dimension().location().toString(); }

    private static void giveBack(ServerPlayer player, ItemStack rest) {
        if (!rest.isEmpty() && !player.getInventory().add(rest)) { player.drop(rest, false); }
    }

    private static void refuse(ServerPlayer player, String key) { player.displayClientMessage(Component.translatable("chat." + ICLib.MODID + ".info.wire." + key), true); }
}
