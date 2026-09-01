package com.immersiveconvergence.data;

import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlocks;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ICBlockStates extends BlockStateProvider {
    public ICBlockStates(PackOutput output, ExistingFileHelper helper) { super(output, ICLib.MODID, helper); }

    @Override protected void registerStatesAndModels() {
        simpleBlock(ICBlocks.HEAT_CREATIVE.get(), models().cubeAll("block/metal/heat_creative", modLoc("block/metal/heat_creative")));

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
}
