package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.common.energy.IEWireEvents;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ImmersiveNetHandler.class, remap = false, priority = 1100)
public abstract class MixinIENetHandlerEvents {
    @Redirect(method = "handleMapForDamage(Ljava/util/Set;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/energy/wires/IImmersiveConnectable;getDamageAmount(Lnet/minecraft/entity/Entity;Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler$Connection;)F"))
    private static float immersiveconvergence$shock(IImmersiveConnectable source, Entity e, Connection c) { return IEWireEvents.shock(source, e, c); }

    @Inject(method = "addAndGetConnection(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;ILblusunrize/immersiveengineering/api/energy/wires/WireType;)Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler$Connection;", at = @At("RETURN"))
    private void immersiveconvergence$added(World world, BlockPos node, BlockPos connection, int distance, WireType cableType, CallbackInfoReturnable<Connection> cir) { IEWireEvents.added(world, cir.getReturnValue()); }

    @Inject(method = "removeConnection(Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler$Connection;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"))
    private void immersiveconvergence$removing(World world, Connection con, Vec3d vecStart, Vec3d vecEnd, CallbackInfo ci) { IEWireEvents.removing(world, con); }

    @Inject(method = "removeConnection(Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler$Connection;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)V", at = @At("TAIL"))
    private void immersiveconvergence$removed(World world, Connection con, Vec3d vecStart, Vec3d vecEnd, CallbackInfo ci) { IEWireEvents.removed(world, con); }

    @Inject(method = "clearAllConnectionsFor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/energy/wires/IImmersiveConnectable;Z)V", at = @At("HEAD"))
    private void immersiveconvergence$blockBroken(BlockPos node, World world, IImmersiveConnectable iic, boolean doDrops, CallbackInfo ci) {
        if (!world.isRemote) { IEWireEvents.enter(ICWireCause.BLOCK_BROKEN); }
    }

    @Inject(method = "clearAllConnectionsFor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/energy/wires/IImmersiveConnectable;Z)V", at = @At("RETURN"))
    private void immersiveconvergence$blockBrokenDone(BlockPos node, World world, IImmersiveConnectable iic, boolean doDrops, CallbackInfo ci) {
        if (!world.isRemote) { IEWireEvents.exit(); }
    }

    @Inject(method = "clearAllConnectionsFor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/TargetingInfo;)Z", at = @At("HEAD"))
    private void immersiveconvergence$cutNode(BlockPos node, World world, TargetingInfo target, CallbackInfoReturnable<Boolean> cir) {
        if (!world.isRemote) { IEWireEvents.enter(ICWireCause.PLAYER_CUT); }
    }

    @Inject(method = "clearAllConnectionsFor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/TargetingInfo;)Z", at = @At("RETURN"))
    private void immersiveconvergence$cutNodeDone(BlockPos node, World world, TargetingInfo target, CallbackInfoReturnable<Boolean> cir) {
        if (!world.isRemote) { IEWireEvents.exit(); }
    }

    @Inject(method = "removeConnectionAndDrop", at = @At("HEAD"))
    private void immersiveconvergence$cutWire(Connection conn, World world, BlockPos dropPos, CallbackInfo ci) {
        if (!world.isRemote) { IEWireEvents.enter(ICWireCause.PLAYER_CUT); }
    }

    @Inject(method = "removeConnectionAndDrop", at = @At("RETURN"))
    private void immersiveconvergence$cutWireDone(Connection conn, World world, BlockPos dropPos, CallbackInfo ci) {
        if (!world.isRemote) { IEWireEvents.exit(); }
    }
}
