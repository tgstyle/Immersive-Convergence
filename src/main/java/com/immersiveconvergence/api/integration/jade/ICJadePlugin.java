package com.immersiveconvergence.api.integration.jade;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@SuppressWarnings("unused")
@WailaPlugin
public class ICJadePlugin implements IWailaPlugin {
    @Override public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(JadeStatusProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(JadeLinesProvider.INSTANCE, Block.class);
        registration.registerEnergyStorageClient(JadeEnergyProvider.INSTANCE);
        registration.registerFluidStorageClient(JadeFluidProvider.INSTANCE);
    }

    @Override public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(JadeStatusProvider.INSTANCE, MultiblockBlockEntityMaster.class);
        registration.registerBlockDataProvider(JadeStatusProvider.INSTANCE, MultiblockBlockEntityDummy.class);
        registration.registerBlockDataProvider(JadeLinesProvider.INSTANCE, BlockEntity.class);
        registration.registerEnergyStorage(JadeEnergyProvider.INSTANCE, BlockEntity.class);
        registration.registerFluidStorage(JadeFluidProvider.INSTANCE, BlockEntity.class);
    }
}
