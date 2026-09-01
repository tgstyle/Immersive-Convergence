package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.ComparatorManager;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.Function;
import java.util.function.Supplier;
import com.immersiveconvergence.mixin.MultiblockRegistrationBuilderAccessor;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockBuilder<S extends IMultiblockState> extends MultiblockRegistrationBuilder<S, MultiblockBuilder<S>> {
    private Supplier<MultiblockRegistration<S>> regSupplier = () -> { throw new IllegalStateException("Accessed multiblock registration too early"); };

    public MultiblockBuilder(IMultiblockLogic<S> logic, ResourceLocation name) { super(logic, name); }


    public MultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, BlockPos... positions) { redstoneAware(); return selfWrappingComponent(new RedstoneControl<>(getState, positions)); }

    @SuppressWarnings("ConstantConditions") public MultiblockBuilder<S> customBEs(DeferredRegister<BlockEntityType<?>> register) {
        MultiblockRegistrationBuilderAccessor accessor = (MultiblockRegistrationBuilderAccessor) this;
        ResourceLocation rl = accessor.ic$getName();
        Supplier<? extends Block> blockSup = accessor.ic$getBlock();

        Supplier<BlockEntityType<?>> masterSup = register.register(rl.getPath() + "_master", () -> {
            Mutable<BlockEntityType<?>> resultBox = new MutableObject<>();
            resultBox.setValue(BlockEntityType.Builder.of((pos, state) -> new MachineBlockEntityMaster<>(resultBox.getValue(), pos, state, regSupplier.get()), blockSup.get()).build(null));
            return resultBox.getValue();
        });
        Supplier<BlockEntityType<?>> dummySup = register.register(rl.getPath() + "_dummy", () -> {
            Mutable<BlockEntityType<?>> resultBox = new MutableObject<>();
            resultBox.setValue(BlockEntityType.Builder.of((pos, state) -> new MachineBlockEntityDummy<>(resultBox.getValue(), pos, state, regSupplier.get()), blockSup.get()).build(null));
            return resultBox.getValue();
        });

        accessor.ic$setMasterBE(masterSup);
        accessor.ic$setDummyBE(dummySup);
        return this;
    }

    @Override public MultiblockBuilder<S> customBlock(DeferredRegister<Block> register, DeferredRegister<Item> blockItemRegister, Function<MultiblockRegistration<S>, ? extends MultiblockPartBlock<S>> make, Function<Block, Item> makeItem) { super.customBlock(register, blockItemRegister, make, makeItem); return this; }

    @Override public MultiblockBuilder<S> defaultBlock(DeferredRegister<Block> register, DeferredRegister<Item> blockItemRegister, BlockBehaviour.Properties properties) { super.defaultBlock(register, blockItemRegister, properties); return this; }

    @Override public <CS, C extends IMultiblockComponent<CS> & IMultiblockComponent.StateWrapper<S, CS>> MultiblockBuilder<S> selfWrappingComponent(C extraComponent) { Preconditions.checkArgument(!(extraComponent instanceof ComparatorManager<?>)); return super.selfWrappingComponent(extraComponent); }

    @Override protected MultiblockBuilder<S> self() { return this; }

    @Override public MultiblockRegistration<S> build() { MultiblockRegistration<S> reg = super.build(); regSupplier = () -> reg; return reg; }
}
