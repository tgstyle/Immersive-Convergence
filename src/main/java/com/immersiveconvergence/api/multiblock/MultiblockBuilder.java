package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistrationBuilder;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.RedstoneControl;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockLogic;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockPartBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiblockBuilder<S extends IMultiblockState> extends MultiblockRegistrationBuilder<S, MultiblockBuilder<S>> {
    public MultiblockBuilder(IMultiblockLogic<S> logic, ResourceLocation name) { super(logic, name); }

    public MultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, BlockPos... positions) { redstoneAware(); return selfWrappingComponent(new RedstoneControl<>(getState, positions)); }

    public MultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, Supplier<BlockPos> position) {
        BlockPos[] cell = {position.get()};
        MultiblockDataLoader.onReload(() -> cell[0] = position.get());
        return redstone(getState, cell);
    }

    public MultiblockBuilder<S> customBEs(DeferredRegister<BlockEntityType<?>> register) { return customBEs(RegistrationMethod.fromDeferred(register), MachineBlockEntityMaster::new, MachineBlockEntityDummy::new); }

    @Override public MultiblockBuilder<S> customBlock(DeferredRegister<Block> register, DeferredRegister<Item> blockItemRegister, Function<MultiblockRegistration<S>, ? extends MultiblockPartBlock<S>> make, Function<Block, Item> makeItem) { super.customBlock(register, blockItemRegister, make, makeItem); return this; }

    @Override protected MultiblockBuilder<S> self() { return this; }

    @Override public MultiblockRegistration<S> build(Consumer<Consumer<net.neoforged.bus.api.IEventBus>> finisher) {
        MultiblockRegistration<S> reg = super.build(finisher);
        QueueProcessor.MANAGED.add(reg.id());
        return reg;
    }

    public static net.neoforged.bus.api.IEventBus modBus = null;

    public MultiblockRegistration<S> build() {
        return build(registerToModBus -> {
            if (modBus == null) { throw new IllegalStateException("MultiblockBuilder.modBus was not set before a multiblock was built - capabilities will not be registered."); }
            registerToModBus.accept(modBus);
        });
    }
}
