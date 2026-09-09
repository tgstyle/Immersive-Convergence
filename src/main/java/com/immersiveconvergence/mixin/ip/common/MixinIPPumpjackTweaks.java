package com.immersiveconvergence.mixin.ip.common;

import com.immersiveconvergence.api.petroleum.ICPowerTier;
import com.immersiveconvergence.api.petroleum.ICPowerTiers;
import com.immersiveconvergence.api.petroleum.ICPumpjackHandler;
import com.immersiveconvergence.api.petroleum.ICReservoirContent;
import com.immersiveconvergence.api.petroleum.ICReservoirData;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityPumpjack.class, remap = false)
public abstract class MixinIPPumpjackTweaks {
    @Unique private long immersiveconvergence$resolvedAt = Long.MIN_VALUE;
    @Unique private ICPowerTier immersiveconvergence$tier = null;
    @Unique private int immersiveconvergence$pumpSpeed;
    @Unique private ICReservoirContent immersiveconvergence$content = ICReservoirContent.EMPTY;
    @Unique private boolean immersiveconvergence$sized;

    @Unique private TileEntityPumpjack immersiveconvergence$self() { return (TileEntityPumpjack)(Object)this; }

    @Unique private boolean immersiveconvergence$pumps() { return immersiveconvergence$content != ICReservoirContent.GAS; }

    @Unique private void immersiveconvergence$resolve(World world, BlockPos pos) {
        long now = world.getTotalWorldTime();
        if (now == immersiveconvergence$resolvedAt) { return; }
        immersiveconvergence$resolvedAt = now;
        ICReservoirData data = ICPumpjackHandler.dataUnder(world, pos);
        immersiveconvergence$content = data == null ? ICReservoirContent.EMPTY : data.content;
        immersiveconvergence$tier = data == null ? ICPowerTiers.fallback() : ICPowerTiers.get(data.powerTier);
        immersiveconvergence$pumpSpeed = ICPumpjackHandler.pumpSpeedOf(data);
    }

    @Inject(method = "update(Z)V", at = @At("HEAD"))
    private void immersiveconvergence$resolveReservoir(boolean consumePower, CallbackInfo ci) {
        TileEntityPumpjack self = immersiveconvergence$self();
        World world = self.getWorld();
        if (world.isRemote || self.isDummy()) { return; }
        immersiveconvergence$resolve(world, self.getPos());
        if (immersiveconvergence$sized) { return; }
        immersiveconvergence$sized = true;
        TileEntityMultiblockMetal<?, ?> machine = (TileEntityMultiblockMetal<?, ?>)(Object)this;
        machine.energyStorage.setCapacity(immersiveconvergence$tier.getCapacity());
        machine.energyStorage.setLimitTransfer(immersiveconvergence$tier.getUsage());
    }

    @Redirect(method = "update(Z)V", at = @At(value = "FIELD", target = "Lflaxbeard/immersivepetroleum/common/Config$IPConfig$Extraction;pumpjack_consumption:I", opcode = Opcodes.GETSTATIC))
    private int immersiveconvergence$consumption() { return immersiveconvergence$tier.getUsage(); }

    @Redirect(method = "update(Z)V", at = @At(value = "FIELD", target = "Lflaxbeard/immersivepetroleum/common/Config$IPConfig$Extraction;pumpjack_speed:I", opcode = Opcodes.GETSTATIC))
    private int immersiveconvergence$speed() { return immersiveconvergence$pumps() ? immersiveconvergence$pumpSpeed : 0; }

    @Redirect(method = "update(Z)V", at = @At(value = "INVOKE", target = "Lflaxbeard/immersivepetroleum/common/blocks/metal/TileEntityPumpjack;getResidualOil()I"))
    private int immersiveconvergence$residual(TileEntityPumpjack self) { return immersiveconvergence$pumps() ? ICPumpjackHandler.claimReplenish(self.getWorld(), self.getPos()) : 0; }

    @Redirect(method = "update(Z)V", at = @At(value = "INVOKE", target = "Lflaxbeard/immersivepetroleum/common/blocks/metal/TileEntityPumpjack;availableOil()I"))
    private int immersiveconvergence$available(TileEntityPumpjack self) { return immersiveconvergence$pumps() ? self.availableOil() : 0; }

    @Redirect(method = "update(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getX()I", ordinal = 1, remap = true), remap = false)
    private int immersiveconvergence$pipeCheckX(BlockPos pos) { return Math.abs(pos.getX()); }

    @Redirect(method = "update(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getZ()I", ordinal = 1, remap = true), remap = false)
    private int immersiveconvergence$pipeCheckZ(BlockPos pos) { return Math.abs(pos.getZ()); }
}
