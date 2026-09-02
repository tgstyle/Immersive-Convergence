package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.api.block.ModBlockItem;
import com.immersiveconvergence.api.registration.BlockEntry;
import com.immersiveconvergence.common.blocks.HeatCreativeBlock;
import com.immersiveconvergence.common.blocks.RotorCreativeBlock;
import com.immersiveconvergence.common.blocks.logic.HeatCreativeBlockEntity;
import com.immersiveconvergence.common.blocks.logic.RotorCreativeBlockEntity;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class ICBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(ICLib.MODID);

    private static final Supplier<BlockBehaviour.Properties> METAL_PROPERTIES_NO_OCCLUSION = () -> BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(SoundType.METAL)
            .strength(3.0F, 15.0F)
            .noOcclusion();

    public static final ICBlockEntry<RotorCreativeBlock> ROTOR_CREATIVE = new ICBlockEntry<>(
            "rotor_creative",
            METAL_PROPERTIES_NO_OCCLUSION,
            p -> new RotorCreativeBlock(RotorCreativeBlockEntity::new, p)
    );

    public static final ICBlockEntry<HeatCreativeBlock> HEAT_CREATIVE = new ICBlockEntry<>(
            "heat_creative",
            METAL_PROPERTIES_NO_OCCLUSION,
            p -> new HeatCreativeBlock(HeatCreativeBlockEntity::new, p)
    );

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
        for (ICBlockEntry<?> entry : ICBlockEntry.ALL_ENTRIES) { ICItems.REGISTER.register(entry.getId().getPath(), () -> new ModBlockItem(entry.get())); }
    }

    public static final class ICBlockEntry<T extends Block> extends BlockEntry<T> {
        public static final Collection<ICBlockEntry<?>> ALL_ENTRIES = new ArrayList<>();

        public ICBlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
            super(REGISTER, name, properties, make);
            ALL_ENTRIES.add(this);
        }
    }
}
