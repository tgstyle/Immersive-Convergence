package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.api.block.ModBlockItem;
import com.immersiveconvergence.common.blocks.HeatCreativeBlock;
import com.immersiveconvergence.common.blocks.RotorCreativeBlock;
import com.immersiveconvergence.common.blocks.logic.HeatCreativeBlockEntity;
import com.immersiveconvergence.common.blocks.logic.RotorCreativeBlockEntity;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class ICBlocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, ICLib.MODID);

    private static final Supplier<BlockBehaviour.Properties> METAL_PROPERTIES_NO_OCCLUSION = () -> BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(SoundType.METAL)
            .strength(3.0F, 15.0F)
            .noOcclusion();

    public static final BlockEntry<RotorCreativeBlock> ROTOR_CREATIVE = new BlockEntry<>(
            "rotor_creative",
            METAL_PROPERTIES_NO_OCCLUSION,
            p -> new RotorCreativeBlock(RotorCreativeBlockEntity::new, p)
    );

    public static final BlockEntry<HeatCreativeBlock> HEAT_CREATIVE = new BlockEntry<>(
            "heat_creative",
            METAL_PROPERTIES_NO_OCCLUSION,
            p -> new HeatCreativeBlock(HeatCreativeBlockEntity::new, p)
    );

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
        for (BlockEntry<?> entry : BlockEntry.ALL_ENTRIES) { ICItems.REGISTER.register(entry.getId().getPath(), () -> new ModBlockItem(entry.get())); }
    }

    public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
        public static final Collection<BlockEntry<?>> ALL_ENTRIES = new ArrayList<>();

        private final RegistryObject<T> regObject;
        private final Supplier<BlockBehaviour.Properties> properties;

        public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
            this.properties = properties;
            this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
            ALL_ENTRIES.add(this);
        }

        @Override public T get() { return regObject.get(); }

        public ResourceLocation getId() { return regObject.getId(); }

        public BlockBehaviour.Properties getProperties() { return properties.get(); }

        @Override public @Nonnull Item asItem() { return get().asItem(); }
    }
}
