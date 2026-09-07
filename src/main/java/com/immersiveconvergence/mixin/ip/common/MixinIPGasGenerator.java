package com.immersiveconvergence.mixin.ip.common;

import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityGasGenerator;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityGasGenerator.class)
public abstract class MixinIPGasGenerator {
    @Unique private long immersiveconvergence$lastSync = Long.MIN_VALUE;
    @Unique private boolean immersiveconvergence$hadFuel;

    @Redirect(method = "update", at = @At(value = "INVOKE", ordinal = 0, target = "Lflaxbeard/immersivepetroleum/common/blocks/metal/TileEntityGasGenerator;markContainingBlockForUpdate(Lnet/minecraft/block/state/IBlockState;)V", remap = false))
    private void redirectThrottleFuelSync(TileEntityGasGenerator generator, IBlockState state) {
        boolean hasFuel = generator.tank.getFluid() != null;
        long now = generator.getWorld().getTotalWorldTime();
        if (hasFuel == this.immersiveconvergence$hadFuel && now - this.immersiveconvergence$lastSync < 10L) { return; }
        this.immersiveconvergence$hadFuel = hasFuel;
        this.immersiveconvergence$lastSync = now;
        generator.markContainingBlockForUpdate(state);
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/energy/immersiveflux/FluxStorage;receiveEnergy(IZ)I", remap = false))
    private int redirectBurnOnlyForFullCredit(FluxStorage storage, int energy, boolean simulate) {
        if (storage.receiveEnergy(energy, true) < energy) { return 0; }
        return storage.receiveEnergy(energy, simulate);
    }
}
