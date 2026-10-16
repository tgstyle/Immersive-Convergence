package com.immersiveconvergence.core.compat.computercraft;

import com.immersiveconvergence.api.compat.ICComputerTable;
import com.immersiveconvergence.api.compat.ICComputerTables;
import com.immersiveconvergence.api.compat.computercraft.ICPeripheral;
import com.immersiveconvergence.core.lib.ICLib;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ICComputerCraft {
    private static final Capability<IPeripheral> PERIPHERAL = CapabilityManager.get(new CapabilityToken<>() {});
    private static final ResourceLocation NAME = ICLib.rl("cc_peripheral");
    @Nullable private static Map<BlockEntityType<?>, ICComputerTable<?>> tables;

    private ICComputerCraft() {}

    public static void init() { MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, ICComputerCraft::attach); }

    private static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        ICComputerTable<?> table = tables().get(event.getObject().getType());
        if (table != null) { event.addCapability(NAME, provider(event.getObject(), table)); }
    }

    private static synchronized Map<BlockEntityType<?>, ICComputerTable<?>> tables() {
        if (tables == null) {
            Map<BlockEntityType<?>, ICComputerTable<?>> built = new HashMap<>();
            for (ICComputerTable<?> table : ICComputerTables.all()) {
                for (Supplier<? extends BlockEntityType<?>> type : table.blockEntities()) { built.putIfAbsent(type.get(), table); }
            }
            tables = built;
        }
        return tables;
    }

    private static <T> ICapabilityProvider provider(BlockEntity blockEntity, ICComputerTable<T> table) {
        return new ICapabilityProvider() {
            @Nullable private LazyOptional<IPeripheral> peripheral;

            @Nonnull @Override public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction side) {
                if (capability != PERIPHERAL) { return LazyOptional.empty(); }
                if (peripheral == null) { peripheral = table.attachesTo(blockEntity) ? LazyOptional.of(() -> new ICPeripheral<>(blockEntity, table)) : LazyOptional.empty(); }
                return peripheral.cast();
            }
        };
    }
}
