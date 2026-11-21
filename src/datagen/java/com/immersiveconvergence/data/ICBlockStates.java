package com.immersiveconvergence.data;

import com.immersiveconvergence.api.block.Enums;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.common.blocks.CreativeEnergySourceBlock;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlocks;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ICBlockStates extends BlockStateProvider {
    public ICBlockStates(PackOutput output, ExistingFileHelper helper) { super(output, ICLib.MODID, helper); }

    @Override protected void registerStatesAndModels() {
        simpleBlock(ICBlocks.HEAT_CREATIVE.get(), models().cubeAll("block/metal/heat_creative", modLoc("block/metal/heat_creative")));

        models().cubeAll("block/creative_energy_source", sideTexture(Enums.IOSideConfig.OUTPUT));
        if (ICBlocks.CREATIVE_ENERGY_SOURCE != null) { creativeEnergySource(ICBlocks.CREATIVE_ENERGY_SOURCE.get()); }

        VariantBlockStateBuilder rotorBuilder = getVariantBuilder(ICBlocks.ROTOR_CREATIVE.get());
        ModelFile rotorNorthSouth = new ModelFile.UncheckedModelFile(modLoc("dynamic/rotor_creative"));
        ModelFile rotorEastWest = new ModelFile.UncheckedModelFile(modLoc("dynamic/rotor_creative_east_west"));
        rotorBuilder.forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            ModelFile modelFile = (facing == Direction.NORTH || facing == Direction.SOUTH) ? rotorNorthSouth : rotorEastWest;
            int yRot = (facing == Direction.SOUTH || facing == Direction.WEST) ? 180 : 0;
            return ConfiguredModel.builder().modelFile(modelFile).rotationY(yRot).build();
        });
    }

    private void creativeEnergySource(Block block) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        for (Direction side : DirectionUtils.VALUES) {
            for (Enums.IOSideConfig mode : Enums.IOSideConfig.VALUES) {
                builder.part().modelFile(sideModel(side, mode)).addModel().condition(CreativeEnergySourceBlock.sideProperty(side), mode).end();
            }
        }
    }

    private ModelFile sideModel(Direction side, Enums.IOSideConfig mode) {
        BlockModelBuilder model = models().getBuilder("block/creative_energy_source/" + side.getName() + "_" + mode.getSerializedName())
                .texture("particle", sideTexture(Enums.IOSideConfig.NONE))
                .texture("face", sideTexture(mode));
        model.element().from(0, 0, 0).to(16, 16, 16).face(side).texture("#face").cullface(side).end().end();
        return model;
    }

    private ResourceLocation sideTexture(Enums.IOSideConfig mode) { return modLoc("block/wooden/creative_energy_source_" + mode.getSerializedName()); }
}
