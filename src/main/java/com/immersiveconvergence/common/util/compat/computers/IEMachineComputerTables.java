package com.immersiveconvergence.common.util.compat.computers;

import com.immersiveconvergence.api.compat.ICComputerArgs;
import com.immersiveconvergence.api.compat.ICComputerTable;

import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityLightningrod;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityAlloySmelter;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnace;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityCokeOven;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public final class IEMachineComputerTables {
    public static final ICComputerTable<TileEntityMetalPress> METAL_PRESS = IEComputerLocators.metal("ie_metal_press", TileEntityMetalPress.class, true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isActive", TileEntityMultiblockMetal::shouldRenderAsActive)
            .add("getMold", te -> te.mold)
            .add("getInputQueue", te -> IEComputerTables.inputQueue(te.processQueue, recipe -> recipe.output));
    public static final ICComputerTable<TileEntityAutoWorkbench> AUTO_WORKBENCH = IEComputerLocators.metal("ie_auto_workbench", TileEntityAutoWorkbench.class, true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isRunning", TileEntityMultiblockMetal::shouldRenderAsActive)
            .call("selectRecipe", (te, args) -> {
                int selected = ICComputerArgs.checkInteger(args, 0) - 1;
                int available = recipes(te).length;
                if (selected < 0 || selected >= available) { throw new IllegalArgumentException("Only " + available + " recipes are available"); }
                te.selectedRecipe = selected;
                return null;
            })
            .call("unselectRecipe", (te, args) -> {
                te.selectedRecipe = -1;
                return null;
            })
            .add("getAvailableRecipes", IEMachineComputerTables::recipeOutputs)
            .add("getSelectedRecipe", te -> te.selectedRecipe + 1);
    public static final ICComputerTable<TileEntityCokeOven> COKE_OVEN = IEComputerLocators.part("ie_coke_oven", TileEntityCokeOven.class)
            .add("isActive", te -> te.active)
            .add("getProcess", te -> te.process)
            .add("getMaxProcess", te -> te.processMax)
            .add("getInputStack", te -> te.getInventory().get(0))
            .add("getOutputStack", te -> te.getInventory().get(1))
            .add("getTankInfo", te -> te.tank.getInfo())
            .add("getEmptyCannisters", te -> te.getInventory().get(2))
            .add("getFilledCannisters", te -> te.getInventory().get(3));
    public static final ICComputerTable<TileEntityBlastFurnace> BLAST_FURNACE = blastFurnace(new ICComputerTable<>("ie_blast_furnace", TileEntityBlastFurnace.class, tile -> tile instanceof TileEntityBlastFurnaceAdvanced ? null : IEComputerLocators.master(tile, TileEntityBlastFurnace.class)));
    public static final ICComputerTable<TileEntityBlastFurnaceAdvanced> BLAST_FURNACE_ADVANCED = blastFurnace(IEComputerLocators.part("ie_blast_furnace_advanced", TileEntityBlastFurnaceAdvanced.class));
    public static final ICComputerTable<TileEntityAlloySmelter> ALLOY_SMELTER = IEComputerLocators.part("ie_alloy_smelter", TileEntityAlloySmelter.class)
            .add("isActive", te -> te.active)
            .add("getProcess", te -> te.process)
            .add("getMaxProcess", te -> te.processMax)
            .add("getBurnTime", te -> te.burnTime)
            .call("getInputStack", (te, args) -> new Object[] {te.getInventory().get(ICComputerArgs.checkRange(args, 0, 2, "Input slots are 1-2"))})
            .add("getFuelStack", te -> te.getInventory().get(2))
            .add("getOutputStack", te -> te.getInventory().get(3));
    public static final ICComputerTable<TileEntityLightningrod> LIGHTNING_ROD = IEComputerLocators.part("ie_lightning_rod", TileEntityLightningrod.class)
            .add("getEnergyStored", te -> te.getFluxStorage().getEnergyStored())
            .add("getMaxEnergyStored", te -> te.getFluxStorage().getMaxEnergyStored());
    public static final ICComputerTable<TileEntitySheetmetalTank> SHEETMETAL_TANK = IEComputerLocators.part("ie_sheetmetal_tank", TileEntitySheetmetalTank.class)
            .add("getTankInfo", te -> te.tank.getInfo());
    public static final ICComputerTable<TileEntitySilo> SILO = IEComputerLocators.part("ie_silo", TileEntitySilo.class)
            .add("getContents", te -> ItemHandlerHelper.copyStackWithSize(te.identStack, te.storageAmount));

    private IEMachineComputerTables() {}

    private static <T extends TileEntityBlastFurnace> ICComputerTable<T> blastFurnace(ICComputerTable<T> table) {
        return table.add("isActive", te -> te.active)
                .add("getProcess", te -> te.process)
                .add("getMaxProcess", te -> te.processMax)
                .add("getBurnTime", te -> te.burnTime)
                .add("getInputStack", te -> te.getInventory().get(0))
                .add("getFuelStack", te -> te.getInventory().get(1))
                .add("getOutputStack", te -> te.getInventory().get(2))
                .add("getSlagStack", te -> te.getInventory().get(3));
    }

    private static BlueprintCraftingRecipe[] recipes(TileEntityAutoWorkbench te) { return BlueprintCraftingRecipe.findRecipes(ItemNBTHelper.getString(te.inventory.get(0), "blueprint")); }

    private static ItemStack[] recipeOutputs(TileEntityAutoWorkbench te) {
        BlueprintCraftingRecipe[] recipes = recipes(te);
        ItemStack[] outputs = new ItemStack[recipes.length];
        for (int i = 0; i < recipes.length; i++) { outputs[i] = recipes[i].output; }
        return outputs;
    }
}
