package com.immersiveconvergence.common.util.compat.computers;

import com.immersiveconvergence.api.compat.ICComputerArgs;
import com.immersiveconvergence.api.compat.ICComputerTable;

import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import blusunrize.immersiveengineering.api.crafting.IMultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import blusunrize.immersiveengineering.api.tool.AssemblerHandler;
import blusunrize.immersiveengineering.api.tool.AssemblerHandler.RecipeQuery;
import blusunrize.immersiveengineering.common.Config.IEConfig;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCapacitorCreative;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCapacitorHV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCapacitorLV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCapacitorMV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCrusher;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityEnergyMeter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityExcavator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFloodlight;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySampleDrill;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySqueezer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTeslaCoil;
import blusunrize.immersiveengineering.common.items.ItemGraphiteElectrode;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class IEComputerTables {
    public static final ICComputerTable<TileEntityArcFurnace> ARC_FURNACE = IEComputerLocators.metal("ie_arc_furnace", TileEntityArcFurnace.class, true)
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("isActive", TileEntityMultiblockMetal::shouldRenderAsActive)
            .call("getInputStack", (te, args) -> new Object[] {inputWithProgress(te, te.inventory, ICComputerArgs.checkRange(args, 0, 12, "Input slots are 1-12"))})
            .call("getOutputStack", (te, args) -> new Object[] {te.inventory.get(ICComputerArgs.checkRange(args, 0, 6, "Output slots are 1-6") + 16)})
            .call("getAdditiveStack", (te, args) -> new Object[] {te.inventory.get(ICComputerArgs.checkRange(args, 0, 4, "Additive slots are 1-4") + 12)})
            .add("getSlagStack", te -> te.inventory.get(22))
            .add("hasElectrodes", TileEntityArcFurnace::hasElectrodes)
            .call("getElectrode", (te, args) -> new Object[] {electrode(te.inventory.get(ICComputerArgs.checkRange(args, 0, 3, "Electrode slots are 1-3") + 23))});
    public static final ICComputerTable<TileEntityAssembler> ASSEMBLER = IEComputerLocators.metal("ie_assembler", TileEntityAssembler.class, false)
            .call("hasIngredients", (te, args) -> new Object[] {hasIngredients(te, recipe(args))})
            .call("getRecipe", (te, args) -> new Object[] {assemblerRecipe(te, recipe(args))})
            .call("isValidRecipe", (te, args) -> new Object[] {!te.patterns[recipe(args)].inv.get(9).isEmpty()})
            .call("getTank", (te, args) -> new Object[] {te.tanks[ICComputerArgs.checkRange(args, 0, 3, "Only tanks 1-3 are available")].getInfo()})
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .call("getStackInSlot", (te, args) -> new Object[] {te.inventory.get(ICComputerArgs.checkRange(args, 0, 18, "Only slots 1-18 are available"))})
            .call("getBufferStack", (te, args) -> new Object[] {te.inventory.get(18 + recipe(args))})
            .call("enableComputerControl", (te, args) -> {
                te.isComputerControlled = ICComputerArgs.checkBoolean(args, 0);
                for (int i = 0; i < 3; i++) { te.computerOn[i] = true; }
                return null;
            })
            .call("setEnabled", (te, args) -> {
                boolean on = ICComputerArgs.checkBoolean(args, 1);
                te.computerOn[recipe(args)] = on;
                return null;
            });
    public static final ICComputerTable<TileEntityBottlingMachine> BOTTLING_MACHINE = IEComputerLocators.metal("ie_bottling_machine", TileEntityBottlingMachine.class, true)
            .add("isActive", TileEntityMultiblockMetal::shouldRenderAsActive)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getTank", te -> te.tanks[0].getInfo());
    public static final ICComputerTable<TileEntityCapacitorLV> CAPACITOR_LV = capacitor("ie_lv_capacitor", TileEntityCapacitorLV.class);
    public static final ICComputerTable<TileEntityCapacitorMV> CAPACITOR_MV = capacitor("ie_mv_capacitor", TileEntityCapacitorMV.class);
    public static final ICComputerTable<TileEntityCapacitorHV> CAPACITOR_HV = capacitor("ie_hv_capacitor", TileEntityCapacitorHV.class);
    public static final ICComputerTable<TileEntityCapacitorCreative> CAPACITOR_CREATIVE = capacitor("ie_creative_capacitor", TileEntityCapacitorCreative.class);
    public static final ICComputerTable<TileEntityCrusher> CRUSHER = IEComputerLocators.metal("ie_crusher", TileEntityCrusher.class, true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isActive", TileEntityMultiblockMetal::shouldRenderAsActive)
            .add("getInputQueue", te -> inputQueue(te.processQueue, recipe -> recipe.output));
    public static final ICComputerTable<TileEntityDieselGenerator> DIESEL_GENERATOR = IEComputerLocators.metal("ie_diesel_generator", TileEntityDieselGenerator.class, true)
            .add("isActive", te -> te.active)
            .add("getTankInfo", te -> te.tanks[0].getInfo());
    public static final ICComputerTable<TileEntityEnergyMeter> ENERGY_METER = IEComputerLocators.only("ie_current_transformer", TileEntityEnergyMeter.class, te -> te.lower)
            .add("getAvgEnergy", TileEntityEnergyMeter::getAveragePower);
    public static final ICComputerTable<TileEntityExcavator> EXCAVATOR = IEComputerLocators.metal("ie_excavator", TileEntityExcavator.class, true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("isActive", te -> te.active);
    public static final ICComputerTable<TileEntityFermenter> FERMENTER = IEComputerLocators.metal("ie_fermenter", TileEntityFermenter.class, true)
            .call("getRecipe", (te, args) -> fermenterRecipe(te.inventory.get(ICComputerArgs.checkRange(args, 0, 8, "Input slots are 1-8"))))
            .call("getInputStack", (te, args) -> new Object[] {te.inventory.get(ICComputerArgs.checkRange(args, 0, 8, "Input slots are 1-8"))})
            .add("getOutputStack", te -> te.inventory.get(8))
            .add("getFluid", te -> te.tanks[0].getInfo())
            .add("getEmptyCannisters", te -> te.inventory.get(9))
            .add("getFilledCannisters", te -> te.inventory.get(10))
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("isActive", TileEntityMultiblockMetal::shouldRenderAsActive);
    public static final ICComputerTable<TileEntityFloodlight> FLOODLIGHT = IEComputerLocators.only("ie_floodlight", TileEntityFloodlight.class, te -> true)
            .add("getMaxEnergyStored", te -> 80)
            .add("getEnergyStored", te -> te.energyStorage)
            .call("turnAroundXZ", (te, args) -> {
                te.turnX(ICComputerArgs.checkBoolean(args, 0), true);
                return null;
            })
            .call("turnAroundY", (te, args) -> {
                te.turnY(ICComputerArgs.checkBoolean(args, 0), true);
                return null;
            })
            .add("canTurn", TileEntityFloodlight::canComputerTurn)
            .call("setEnabled", (te, args) -> {
                te.computerOn = ICComputerArgs.checkBoolean(args, 0);
                return null;
            })
            .add("isActive", te -> te.active)
            .connection(te -> {
                te.controllingComputers++;
                te.computerOn = true;
            }, te -> te.controllingComputers--);
    public static final ICComputerTable<TileEntityMixer> MIXER = IEComputerLocators.metal("ie_mixer", TileEntityMixer.class, true)
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("isActive", TileEntityMultiblockMetal::shouldRenderAsActive)
            .call("getInputStack", (te, args) -> new Object[] {inputWithProgress(te, te.inventory, ICComputerArgs.checkRange(args, 0, 12, "Input slots are 1-12"))})
            .add("getTank", te -> te.tank.getInfo())
            .add("isValidRecipe", te -> te.processQueue.get(0).recipe != null);
    public static final ICComputerTable<TileEntityRefinery> REFINERY = IEComputerLocators.metal("ie_refinery", TileEntityRefinery.class, true)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getInputFluidTanks", te -> named("input1", te.tanks[0].getInfo(), "input2", te.tanks[1].getInfo()))
            .add("getOutputTank", te -> te.tanks[2].getInfo())
            .add("getRecipe", IEComputerTables::refineryRecipe)
            .add("isValidRecipe", te -> te.processQueue.get(0).recipe != null)
            .add("getEmptyCannisters", te -> named("input1", te.inventory.get(1), "input2", te.inventory.get(3), "output", te.inventory.get(4)))
            .add("getFullCannisters", te -> named("input1", te.inventory.get(0), "input2", te.inventory.get(2), "output", te.inventory.get(5)));
    public static final ICComputerTable<TileEntitySampleDrill> SAMPLE_DRILL = IEComputerLocators.only("ie_sample_drill", TileEntitySampleDrill.class, te -> te.dummy == 0)
            .add("getSampleProgress", TileEntitySampleDrill::getSampleProgress)
            .add("isSamplingFinished", TileEntitySampleDrill::isSamplingFinished)
            .call("getVeinUnlocalizedName", (te, args) -> te.isSamplingFinished() ? new Object[] {te.getVein()} : new Object[0])
            .call("getVeinLocalizedName", (te, args) -> te.isSamplingFinished() ? new Object[] {te.getVeinLocalizedName()} : new Object[0])
            .call("getVeinIntegrity", (te, args) -> te.isSamplingFinished() ? new Object[] {te.getVeinIntegrity()} : new Object[0])
            .add("getVeinExpectedYield", TileEntitySampleDrill::getExpectedVeinYield)
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .call("reset", (te, args) -> {
                te.process = 0;
                te.active = true;
                te.sample = ItemStack.EMPTY;
                return new Object[0];
            });
    public static final ICComputerTable<TileEntitySqueezer> SQUEEZER = IEComputerLocators.metal("ie_squeezer", TileEntitySqueezer.class, true)
            .call("getRecipe", (te, args) -> squeezerRecipe(te.inventory.get(ICComputerArgs.checkRange(args, 0, 8, "Input slots are 1-8"))))
            .call("getInputStack", (te, args) -> new Object[] {te.inventory.get(ICComputerArgs.checkRange(args, 0, 8, "Input slots are 1-8"))})
            .add("getOutputStack", te -> te.inventory.get(8))
            .add("getFluid", te -> te.tanks[0].getInfo())
            .add("getEmptyCannisters", te -> te.inventory.get(9))
            .add("getFilledCannisters", te -> te.inventory.get(10))
            .add("getMaxEnergyStored", te -> te.energyStorage.getMaxEnergyStored())
            .add("getEnergyStored", te -> te.energyStorage.getEnergyStored())
            .add("isActive", TileEntityMultiblockMetal::shouldRenderAsActive);
    public static final ICComputerTable<TileEntityTeslaCoil> TESLA_COIL = IEComputerLocators.only("ie_tesla_coil", TileEntityTeslaCoil.class, te -> !te.isDummy())
            .add("isActive", IEComputerTables::teslaCanRun)
            .call("setRSMode", (te, args) -> {
                te.redstoneControlInverted = ICComputerArgs.checkBoolean(args, 0);
                return null;
            })
            .call("setPowerMode", (te, args) -> {
                if (teslaCanRun(te)) { throw new IllegalArgumentException("Can't switch power mode on an active coil"); }
                te.lowPower = !ICComputerArgs.checkBoolean(args, 0);
                return null;
            });

    private IEComputerTables() {}

    private static <T extends TileEntityCapacitorLV> ICComputerTable<T> capacitor(String type, Class<T> tileClass) {
        return IEComputerLocators.exact(type, tileClass)
                .add("getMaxEnergyStored", te -> te.getMaxEnergyStored(EnumFacing.UP))
                .add("getEnergyStored", te -> te.getEnergyStored(EnumFacing.DOWN));
    }

    private static int recipe(Object[] args) { return ICComputerArgs.checkRange(args, 0, 3,"Only recipes 1-3 are available"); }

    private static Map<String, Object> inputWithProgress(TileEntityMultiblockMetal<?, ?> te, NonNullList<ItemStack> inventory, int slot) {
        Map<String, Object> stack = Utils.saveStack(inventory.get(slot));
        for (MultiblockProcess<?> process : te.processQueue) {
            for (int i : ((MultiblockProcessInMachine<?>)process).getInputSlots()) {
                if (i == slot) {
                    stack.put("progress", process.processTick);
                    stack.put("maxProgress", process.maxTicks);
                    return stack;
                }
            }
        }
        stack.put("progress", 0);
        stack.put("maxProgress", 0);
        return stack;
    }

    private static Map<String, Object> electrode(ItemStack stack) {
        Map<String, Object> map = Utils.saveStack(stack);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemGraphiteElectrode) { map.put("damage", ItemNBTHelper.getInt(stack, "graphDmg")); }
        return map;
    }

    private static boolean hasIngredients(TileEntityAssembler te, int recipe) {
        if (te.patterns[recipe].inv.get(9).isEmpty()) { throw new IllegalArgumentException("The requested recipe is invalid"); }
        TileEntityAssembler.CrafterPatternInventory pattern = te.patterns[recipe];
        @SuppressWarnings("unchecked") AssemblerHandler.IRecipeAdapter<IRecipe> adapter = AssemblerHandler.findAdapter(pattern.recipe);
        ArrayList<ItemStack> queryList = new ArrayList<>();
        for (ItemStack stack : te.inventory) {
            if (!stack.isEmpty()) { queryList.add(stack.copy()); }
        }
        RecipeQuery[] queries = adapter.getQueriedInputs(pattern.recipe, pattern.inv);
        if (queries == null) { throw new IllegalArgumentException("The Assembler cannot craft this recipe"); }
        return te.consumeIngredients(queries, queryList, false, null);
    }

    private static Map<String, Object> assemblerRecipe(TileEntityAssembler te, int recipe) {
        Map<String, Object> ret = new HashMap<>();
        for (int i = 0; i < 9; i++) { ret.put("in" + (i + 1), te.patterns[recipe].inv.get(i)); }
        ret.put("out", te.patterns[recipe].inv.get(9));
        return ret;
    }

    static <R extends IMultiblockRecipe> Map<Integer, Object> inputQueue(List<MultiblockProcess<R>> queue, Function<R, ItemStack> output) {
        Map<Integer, Object> ret = new HashMap<>();
        for (int i = 0; i < queue.size(); i++) {
            if (!(queue.get(i) instanceof MultiblockProcessInWorld)) { continue; }
            MultiblockProcessInWorld<R> process = (MultiblockProcessInWorld<R>)queue.get(i);
            Map<String, Object> recipe = new HashMap<>();
            recipe.put("progress", process.processTick);
            recipe.put("maxProgress", process.maxTicks);
            List<Map<String, Object>> input = new ArrayList<>(process.inputItems.size());
            for (ItemStack in : process.inputItems) { input.add(Utils.saveStack(in)); }
            recipe.put("input", input);
            recipe.put("output", Utils.saveStack(output.apply(process.recipe)));
            ret.put(i + 1, recipe);
        }
        return ret;
    }

    private static Object[] fermenterRecipe(ItemStack input) {
        FermenterRecipe recipe = FermenterRecipe.findRecipe(input);
        return recipe == null ? null : new Object[] {input, recipe.itemOutput, recipe.fluidOutput, recipe.getTotalProcessTime()};
    }

    private static Object[] squeezerRecipe(ItemStack input) {
        SqueezerRecipe recipe = SqueezerRecipe.findRecipe(input);
        return recipe == null ? null : new Object[] {input, recipe.itemOutput, recipe.fluidOutput, recipe.getTotalProcessTime()};
    }

    private static Map<String, Object> refineryRecipe(TileEntityRefinery te) {
        RefineryRecipe recipe = te.processQueue.get(0).recipe;
        if (recipe == null) { throw new IllegalArgumentException("The recipe of the refinery is invalid"); }
        return named("input1", recipe.input0, "input2", recipe.input1, "output", recipe.output);
    }

    private static boolean teslaCanRun(TileEntityTeslaCoil te) {
        int energyDrain = IEConfig.Machines.teslacoil_consumption;
        if (te.lowPower) { energyDrain /= 2; }
        return te.canRun(energyDrain);
    }

    static Map<String, Object> named(Object... pairs) {
        Map<String, Object> map = new HashMap<>(pairs.length / 2);
        for (int i = 0; i < pairs.length; i += 2) { map.put((String)pairs[i], pairs[i + 1]); }
        return map;
    }
}
