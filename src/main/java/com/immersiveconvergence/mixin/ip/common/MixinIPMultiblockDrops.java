package com.immersiveconvergence.mixin.ip.common;

import com.immersiveconvergence.api.multiblock.MultiblockDrops;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPMultiblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockIPMultiblock.class)
public abstract class MixinIPMultiblockDrops {
    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void injectInventoryDrop(World world, BlockPos pos, IBlockState state, CallbackInfo ci) {
        if (!world.getGameRules().getBoolean("doTileDrops")) { return; }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityMultiblockPart) { MultiblockDrops.dropMasterInventory(world, pos, (TileEntityMultiblockPart<?>)te); }
    }
}
