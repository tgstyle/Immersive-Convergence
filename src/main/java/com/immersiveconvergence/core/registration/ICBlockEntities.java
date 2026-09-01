package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.common.blocks.logic.HeatCreativeBlockEntity;
import com.immersiveconvergence.common.blocks.logic.RotorCreativeBlockEntity;
import com.immersiveconvergence.api.capability.MechanicalCapabilities;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("DataFlowIssue")
public class ICBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ICLib.MODID);

    public static final Supplier<BlockEntityType<RotorCreativeBlockEntity>> ROTOR_CREATIVE = REGISTER.register(
            "rotor_creative",
            () -> BlockEntityType.Builder.of(RotorCreativeBlockEntity::new, ICBlocks.ROTOR_CREATIVE.get()).build(null)
    );

    public static final Supplier<BlockEntityType<HeatCreativeBlockEntity>> HEAT_CREATIVE = REGISTER.register(
            "heat_creative",
            () -> BlockEntityType.Builder.of(HeatCreativeBlockEntity::new, ICBlocks.HEAT_CREATIVE.get()).build(null)
    );

    public static void init(IEventBus bus) { REGISTER.register(bus); }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(MechanicalCapabilities.MECHANICAL_PROVIDER, ROTOR_CREATIVE.get(), RotorCreativeBlockEntity::getMechanicalProvider);
    }
}
