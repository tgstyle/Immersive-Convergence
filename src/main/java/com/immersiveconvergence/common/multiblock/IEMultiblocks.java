package com.immersiveconvergence.common.multiblock;

import com.immersiveconvergence.api.multiblock.BlockMatcher;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockIEBase;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBucketWheel;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCrusher;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityExcavator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityLightningrod;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySqueezer;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityAlloySmelter;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnace;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityCokeOven;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.MultiblockBucketWheel;
import blusunrize.immersiveengineering.common.blocks.stone.BlockTypes_StoneDevices;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import java.util.function.Supplier;

public final class IEMultiblocks {
    private IEMultiblocks() {}

    private static <E extends Enum<E> & BlockIEBase.IBlockEnum> Supplier<IBlockState> state(BlockIEBase<E> block, E type) { return () -> block.getDefaultState().withProperty(block.property, type); }

    public static void init() {
        BlockMatcher.addGenericOreNames("blockGlass", "blockSheetmetalIron", "blockSheetmetalSteel", "blockSteel", "fenceSteel", "fenceTreatedWood", "scaffoldingSteel", "slabSheetmetalIron", "slabSheetmetalSteel", "slabTreatedWood");
        BlockMatcher.addWildcardBlocks(Blocks.HOPPER, Blocks.PISTON, Blocks.CAULDRON);

        IEMultiblockRegistry.register("IE:CokeOven", "coke_oven", state(IEContent.blockStoneDevice, BlockTypes_StoneDevices.COKE_OVEN), IEMultiblock.Anchor.YAW_OPPOSITE, false);
        IEMultiblockRegistry.register("IE:BlastFurnace", "blast_furnace", state(IEContent.blockStoneDevice, BlockTypes_StoneDevices.BLAST_FURNACE), IEMultiblock.Anchor.YAW_OPPOSITE, false);
        IEMultiblockRegistry.register("IE:BlastFurnaceAdvanced", "blast_furnace_advanced", state(IEContent.blockStoneDevice, BlockTypes_StoneDevices.BLAST_FURNACE_ADVANCED), IEMultiblock.Anchor.YAW_OPPOSITE, false);
        IEMultiblockRegistry.register("IE:AlloySmelter", "alloy_smelter", state(IEContent.blockStoneDevice, BlockTypes_StoneDevices.ALLOY_SMELTER), IEMultiblock.Anchor.YAW_OPPOSITE, false);

        IEMultiblockRegistry.register("IE:Crusher", "crusher", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.CRUSHER), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:Squeezer", "squeezer", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.SQUEEZER), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:Fermenter", "fermenter", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.FERMENTER), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:Mixer", "mixer", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.MIXER), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:Refinery", "refinery", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.REFINERY), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:DieselGenerator", "diesel_generator", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.DIESEL_GENERATOR), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:Lightningrod", "lightning_rod", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.LIGHTNINGROD), IEMultiblock.Anchor.SIDE, false);
        IEMultiblockRegistry.register("IE:SheetmetalTank", "sheetmetal_tank", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.TANK), IEMultiblock.Anchor.YAW_OPPOSITE, false);
        IEMultiblockRegistry.register("IE:Silo", "silo", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.SILO), IEMultiblock.Anchor.YAW_OPPOSITE, false);
        IEMultiblockRegistry.register("IE:ArcFurnace", "arc_furnace", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.ARC_FURNACE), IEMultiblock.Anchor.SIDE, true);

        IEMultiblockRegistry.register("IE:Excavator", "excavator", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.EXCAVATOR), IEMultiblock.Anchor.SIDE, true, (world, clicked, facing, player) -> {
            BlockPos wheel = clicked.offset(facing, 4);
            if (MultiblockBucketWheel.instance.isBlockTrigger(world.getBlockState(wheel))) { MultiblockBucketWheel.instance.createStructure(world, wheel, facing.rotateYCCW(), player); }
        });
        IEMultiblockRegistry.register("IE:BucketWheel", "bucket_wheel", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.BUCKET_WHEEL), IEMultiblock.Anchor.SIDE_RAW, false);
        IEMultiblockRegistry.register("IE:Assembler", "assembler", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.ASSEMBLER), IEMultiblock.Anchor.SIDE, false);
        IEMultiblockRegistry.register("IE:AutoWorkbench", "auto_workbench", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.AUTO_WORKBENCH), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:BottlingMachine", "bottling_machine", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.BOTTLING_MACHINE), IEMultiblock.Anchor.SIDE, true);
        IEMultiblockRegistry.register("IE:MetalPress", "metal_press", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.METAL_PRESS), IEMultiblock.Anchor.CONVEYOR_ROW, false);
        IEMultiblockRegistry.register("IE:ExcavatorDemo", "excavator_demo", state(IEContent.blockMetalMultiblock, BlockTypes_MetalMultiblock.EXCAVATOR), IEMultiblock.Anchor.MANUAL_ONLY, false);

        IEMultiblockRegistry.registerTile(TileEntityCokeOven.class, "IE:CokeOven");
        IEMultiblockRegistry.registerTile(TileEntityBlastFurnace.class, "IE:BlastFurnace");
        IEMultiblockRegistry.registerTile(TileEntityBlastFurnaceAdvanced.class, "IE:BlastFurnaceAdvanced");
        IEMultiblockRegistry.registerTile(TileEntityAlloySmelter.class, "IE:AlloySmelter");
        IEMultiblockRegistry.registerTile(TileEntityCrusher.class, "IE:Crusher");
        IEMultiblockRegistry.registerTile(TileEntitySqueezer.class, "IE:Squeezer");
        IEMultiblockRegistry.registerTile(TileEntityFermenter.class, "IE:Fermenter");
        IEMultiblockRegistry.registerTile(TileEntityMixer.class, "IE:Mixer");
        IEMultiblockRegistry.registerTile(TileEntityRefinery.class, "IE:Refinery");
        IEMultiblockRegistry.registerTile(TileEntityDieselGenerator.class, "IE:DieselGenerator");
        IEMultiblockRegistry.registerTile(TileEntityLightningrod.class, "IE:Lightningrod");
        IEMultiblockRegistry.registerTile(TileEntitySheetmetalTank.class, "IE:SheetmetalTank");
        IEMultiblockRegistry.registerTile(TileEntitySilo.class, "IE:Silo");
        IEMultiblockRegistry.registerTile(TileEntityArcFurnace.class, "IE:ArcFurnace");
        IEMultiblockRegistry.registerTile(TileEntityAssembler.class, "IE:Assembler");
        IEMultiblockRegistry.registerTile(TileEntityAutoWorkbench.class, "IE:AutoWorkbench");
        IEMultiblockRegistry.registerTile(TileEntityBottlingMachine.class, "IE:BottlingMachine");
        IEMultiblockRegistry.registerTile(TileEntityMetalPress.class, "IE:MetalPress");
        IEMultiblockRegistry.registerTile(TileEntityBucketWheel.class, "IE:BucketWheel");
        IEMultiblockRegistry.registerTile(TileEntityExcavator.class, "IE:Excavator");

        if (Loader.isModLoaded("immersivepetroleum")) { IPMultiblocks.init(); }
    }
}
