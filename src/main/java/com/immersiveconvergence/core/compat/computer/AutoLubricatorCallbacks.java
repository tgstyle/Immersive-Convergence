package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.TankInfoCallbacks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import flaxbeard.immersivepetroleum.api.crafting.LubricantHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.ILubricationHandler;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings("unused") public class AutoLubricatorCallbacks extends Callback<AutoLubricatorTileEntity> {
    public AutoLubricatorCallbacks() { addAdditional(new TankInfoCallbacks<>(lubricator -> lubricator.tank, "")); }

    @SuppressWarnings({"rawtypes", "unchecked"}) @ComputerCallable public boolean isActive(CallbackEnvironment<AutoLubricatorTileEntity> env) {
        AutoLubricatorTileEntity lubricator = env.object();
        Level level = lubricator.getLevel();
        FluidStack fluid = lubricator.tank.getFluid();
        if (level == null || fluid.isEmpty() || !LubricantHandler.isValidLube(fluid) || fluid.getAmount() < LubricantHandler.getLubeAmount(fluid)) { return false; }
        if (!(level.getBlockEntity(lubricator.getBlockPos().relative(lubricator.facing)) instanceof IMultiblockBE<?> multiblock)) { return false; }
        ILubricationHandler handler = LubricatedHandler.getHandlerForTile(multiblock.getHelper());
        if (handler == null || !handler.isPlacedCorrectly(level, lubricator.getBlockPos(), lubricator.facing)) { return false; }
        IMultiblockBEHelperMaster<?> master = Utils.getMultiblockMasterHelper(level, multiblock.getHelper());
        return master != null && handler.isMachineEnabled(level, master);
    }
}
