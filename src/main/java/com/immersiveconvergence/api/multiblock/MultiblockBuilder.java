package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistrationBuilder;
import com.immersiveconvergence.api.multiblock.blocks.component.ComparatorManager;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.RedstoneControl;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockLogic;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockPartBlock;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockBuilder<S extends IMultiblockState> extends MultiblockRegistrationBuilder<S, MultiblockBuilder<S>> {
    public MultiblockBuilder(IMultiblockLogic<S> logic, ResourceLocation name) { super(logic, name); }


    public MultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, BlockPos... positions) { redstoneAware(); return selfWrappingComponent(new RedstoneControl<>(getState, positions)); }

    public MultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, Supplier<BlockPos> position) {
        BlockPos[] cell = {position.get()};
        MultiblockDataLoader.onReload(() -> cell[0] = position.get());
        return redstone(getState, cell);
    }

    public MultiblockBuilder<S> customBEs(DeferredRegister<BlockEntityType<?>> register) { return customBEs(RegistrationMethod.fromDeferred(register), MachineBlockEntityMaster::new, MachineBlockEntityDummy::new); }

    @Override public MultiblockBuilder<S> customBlock(DeferredRegister<Block> register, DeferredRegister<Item> blockItemRegister, Function<MultiblockRegistration<S>, ? extends MultiblockPartBlock<S>> make, Function<Block, Item> makeItem) { super.customBlock(register, blockItemRegister, make, makeItem); return this; }

    @Override public MultiblockBuilder<S> defaultBlock(DeferredRegister<Block> register, DeferredRegister<Item> blockItemRegister, BlockBehaviour.Properties properties) { super.defaultBlock(register, blockItemRegister, properties); return this; }

    @Override public <CS, C extends IMultiblockComponent<CS> & IMultiblockComponent.StateWrapper<S, CS>> MultiblockBuilder<S> selfWrappingComponent(C extraComponent) { Preconditions.checkArgument(!(extraComponent instanceof ComparatorManager<?>)); return super.selfWrappingComponent(extraComponent); }

    @Override protected MultiblockBuilder<S> self() { return this; }

    @Override public MultiblockRegistration<S> build() {
        MultiblockRegistration<S> reg = super.build();
        QueueProcessor.MANAGED.add(reg.id());
        return reg;
    }
}
