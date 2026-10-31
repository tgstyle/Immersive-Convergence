package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.common.ie.IEWireEvents;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IWireSyncManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = GlobalWireNetwork.class, remap = false) public abstract class GlobalWireNetworkMixin {
    @Shadow @Final private IWireSyncManager syncManager;

    @Unique @Nullable private Level immersiveconvergence$level() { return syncManager instanceof WireSyncManagerAccessor accessor ? accessor.immersiveconvergence$level() : null; }

    @SuppressWarnings("ConstantConditions") @Unique private GlobalWireNetwork immersiveconvergence$self() { return (GlobalWireNetwork) (Object) this; }

    @Inject(method = "addConnection", at = @At("HEAD"))
    private void immersiveconvergence$adding(Connection conn, CallbackInfo ci) { IEWireEvents.adding(immersiveconvergence$self(), immersiveconvergence$level(), conn); }

    @Inject(method = "addConnection", at = @At("TAIL"))
    private void immersiveconvergence$added(Connection conn, CallbackInfo ci) { IEWireEvents.added(immersiveconvergence$self(), immersiveconvergence$level(), conn); }

    @Inject(method = "removeConnection", at = @At("HEAD"))
    private void immersiveconvergence$removing(Connection c, CallbackInfo ci) { IEWireEvents.removing(immersiveconvergence$self(), immersiveconvergence$level(), c); }

    @Inject(method = "removeConnection", at = @At("TAIL"))
    private void immersiveconvergence$removed(Connection c, CallbackInfo ci) { IEWireEvents.removed(immersiveconvergence$self(), immersiveconvergence$level(), c); }

    @Inject(method = "removeAllConnectionsAt(Lblusunrize/immersiveengineering/api/wires/IImmersiveConnectable;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private void immersiveconvergence$cutNode(CallbackInfo ci) { IEWireEvents.enter(ICWireCause.PLAYER_CUT); }

    @Inject(method = "removeAllConnectionsAt(Lblusunrize/immersiveengineering/api/wires/IImmersiveConnectable;Ljava/util/function/Consumer;)V", at = @At("RETURN"))
    private void immersiveconvergence$cutNodeDone(CallbackInfo ci) { IEWireEvents.exit(); }

    @Inject(method = "removeInsertAndDropConnection", at = @At("HEAD"))
    private void immersiveconvergence$cutWire(CallbackInfo ci) { IEWireEvents.enter(ICWireCause.PLAYER_CUT); }

    @Inject(method = "removeInsertAndDropConnection", at = @At("RETURN"))
    private void immersiveconvergence$cutWireDone(CallbackInfo ci) { IEWireEvents.exit(); }
}
