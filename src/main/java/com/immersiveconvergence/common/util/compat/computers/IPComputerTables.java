package com.immersiveconvergence.common.util.compat.computers;

import com.immersiveconvergence.api.compat.ICComputerTable;
import com.immersiveconvergence.api.petroleum.ICPumpjackHandler;

import flaxbeard.immersivepetroleum.api.crafting.LubricantHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityAutoLubricator;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityDistillationTower;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityGasGenerator;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public final class IPComputerTables {
    public static final ICComputerTable<TileEntityDistillationTower> DISTILLATION_TOWER = IEComputerLocators.metal("ip_distillation_tower", TileEntityDistillationTower.class, true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isActive", TileEntityDistillationTower::getIsActive)
            .add("getInputTankInfo", te -> te.tanks[0].getInfo())
            .add("getOutputTanks", te -> te.tanks[1].fluids.toArray(new FluidStack[0]))
            .add("getEmptyCannisters", te -> IEComputerTables.named("input", te.inventory.get(1), "output", te.inventory.get(2)))
            .add("getFilledCannisters", te -> IEComputerTables.named("input", te.inventory.get(0), "output", te.inventory.get(3)));
    public static final ICComputerTable<TileEntityPumpjack> PUMPJACK = IEComputerLocators.metal("ip_pumpjack", TileEntityPumpjack.class, true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isActive", te -> te.wasActive)
            .add("getReservoirFluid", IPComputerTables::reservoirFluid)
            .add("getReservoirAmount", TileEntityPumpjack::availableOil)
            .add("getReservoirReplenish", IPComputerTables::replenish);
    public static final ICComputerTable<TileEntityGasGenerator> GAS_GENERATOR = IEComputerLocators.only("ip_gas_generator", TileEntityGasGenerator.class, te -> true)
            .add("getEnergyStored", te -> te.getFluxStorage().getEnergyStored())
            .add("getMaxEnergyStored", te -> te.getFluxStorage().getMaxEnergyStored())
            .add("isActive", te -> te.active)
            .add("getTankInfo", te -> te.tank.getInfo());
    public static final ICComputerTable<TileEntityAutoLubricator> AUTO_LUBRICATOR = IEComputerLocators.block("ip_auto_lubricator", TileEntityAutoLubricator.class, te -> te.dummy)
            .add("isActive", IPComputerTables::lubricating)
            .add("getTankInfo", te -> te.tank.getInfo());

    private IPComputerTables() {}

    private static boolean lubricating(TileEntityAutoLubricator te) {
        FluidStack fluid = te.tank.getFluid();
        if (fluid == null || fluid.getFluid() == null || !LubricantHandler.isValidLube(fluid.getFluid()) || fluid.amount < LubricantHandler.getLubeAmount(fluid.getFluid())) { return false; }
        LubricatedHandler.ILubricationHandler<?> handler = LubricatedHandler.getHandlerForTile(te.getWorld().getTileEntity(te.getPos().offset(te.getFacing())));
        if (handler == null) { return false; }
        TileEntity master = handler.isPlacedCorrectly(te.getWorld(), te, te.getFacing());
        return master != null && enabled(handler, te.getWorld(), master);
    }

    @SuppressWarnings("unchecked") private static <E extends TileEntity> boolean enabled(LubricatedHandler.ILubricationHandler<E> handler, World world, TileEntity master) { return handler.isMachineEnabled(world, (E)master); }

    @Nullable private static String reservoirFluid(TileEntityPumpjack te) {
        Fluid fluid = te.availableFluid();
        return fluid == null ? null : fluid.getName();
    }

    private static int replenish(TileEntityPumpjack te) {
        PumpjackHandler.ReservoirType reservoir = ICPumpjackHandler.reservoirUnder(te.getWorld(), te.getPos());
        return reservoir == null ? 0 : reservoir.replenishRate;
    }
}
