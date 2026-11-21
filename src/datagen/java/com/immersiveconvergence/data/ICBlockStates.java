package com.immersiveconvergence.data;

import com.immersiveconvergence.api.block.Enums;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.common.blocks.CreativeEnergySourceBlock;
import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlocks;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ICBlockStates extends BlockStateProvider {
    public ICBlockStates(PackOutput output, ExistingFileHelper helper) { super(output, ICLib.MODID, helper); }

    private void creativeEnergySource(CreativeEnergySourceBlock block) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        for (Enums.IOSideConfig mode : Enums.IOSideConfig.VALUES) {
            String name = "creative_energy_source_" + mode.getSerializedName();
            ModelFile face = models().getBuilder("block/" + name)
                    .texture("face", modLoc("block/wooden/" + name))
                    .texture("particle", modLoc("block/wooden/" + name))
                    .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#face").cullface(Direction.NORTH).end().end();
            for (Direction side : DirectionUtils.VALUES) {
                builder.part().modelFile(face)
                        .rotationX(side == Direction.DOWN ? 90 : side == Direction.UP ? 270 : 0)
                        .rotationY(side.getAxis().isHorizontal() ? (int) side.getOpposite().toYRot() : 0)
                        .addModel().condition(CreativeEnergySourceBlock.SIDES.get(side), mode).end();
            }
        }
        models().cubeAll("block/creative_energy_source_item", modLoc("block/wooden/creative_energy_source_output"));
    }

    @Override protected void registerStatesAndModels() {
        simpleBlock(ICBlocks.HEAT_CREATIVE.get(), models().cubeAll("block/metal/heat_creative", modLoc("block/metal/heat_creative")));

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
}
