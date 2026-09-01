package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.common.blocks.logic.HeatCreativeBlockEntity;
import com.immersiveconvergence.common.blocks.logic.RotorCreativeBlockEntity;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ConstantConditions")
public class ICBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ICLib.MODID);

    public static final RegistryObject<BlockEntityType<RotorCreativeBlockEntity>> ROTOR_CREATIVE = REGISTER.register(
            "rotor_creative",
            () -> BlockEntityType.Builder.of(RotorCreativeBlockEntity::new, ICBlocks.ROTOR_CREATIVE.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<HeatCreativeBlockEntity>> HEAT_CREATIVE = REGISTER.register(
            "heat_creative",
            () -> BlockEntityType.Builder.of(HeatCreativeBlockEntity::new, ICBlocks.HEAT_CREATIVE.get()).build(null)
    );

    public static void init(IEventBus bus) { REGISTER.register(bus); }
}
