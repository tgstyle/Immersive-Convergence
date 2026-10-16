package com.immersiveconvergence.api.multiblock.blocks;

import com.immersiveconvergence.api.multiblock.MultiblockOverride;
import com.immersiveconvergence.api.multiblock.TemplateMultiblock;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration.Disassembler;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration.ExtraComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent.CapabilityGetter;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent.CapabilityRegistrar;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent.StateWrapper;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockLogic;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityDummy;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockPartBlock;
import static com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistrationBuilder.RegistrationMethod.fromDeferred;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class MultiblockRegistrationBuilder<
        State extends IMultiblockState,
        Self extends MultiblockRegistrationBuilder<State, Self>
        > {
    public static final String DUMMY_BE_SUFFIX =  "_dummy";
    public static final String MASTER_BE_SUFFIX =  "_master";
    private final IMultiblockLogic<State> logic;
    private final ResourceLocation name;
    private final List<ExtraComponent<State, ?>> extraComponents = new ArrayList<>();
    private Supplier<BlockEntityType<? extends MultiblockBlockEntityMaster<State>>> masterBE;
    private Supplier<BlockEntityType<? extends MultiblockBlockEntityDummy<State>>> dummyBE;
    private Supplier<? extends MultiblockPartBlock<State>> block;
    private Supplier<? extends Item> item;
    private boolean mirrorable = true;
    private boolean hasComparatorOutput = false;
    private boolean redstoneInputAware = false;
    private Supplier<BlockPos> getMasterPosInMB;
    private Function<Level, Vec3i> getSize;
    private Disassembler disassemble;
    private Function<Level, List<StructureBlockInfo>> structure;
    private MultiblockRegistration<State> result;

    public MultiblockRegistrationBuilder(IMultiblockLogic<State> logic, ResourceLocation name) {
        this.logic = logic;
        this.name = name;
    }

    public Self notMirrored() {
        this.mirrorable = false;
        return self();
    }

    public Self withComparator() {
        this.hasComparatorOutput = true;
        return self();
    }

    public Self redstoneAware() {
        this.redstoneInputAware = true;
        return self();
    }

    public Self customBEs(RegistrationMethod<BlockEntityType<?>> register, BEConstructor<State, ? extends MultiblockBlockEntityMaster<State>> master, BEConstructor<State, ? extends MultiblockBlockEntityDummy<State>> dummy) {
        Preconditions.checkState(this.masterBE == null);
        Preconditions.checkState(this.dummyBE == null);
        this.masterBE = register.register(name.getPath() + MASTER_BE_SUFFIX, () -> makeBEType(master));
        this.dummyBE = register.register(name.getPath() + DUMMY_BE_SUFFIX, () -> makeBEType(dummy));
        return self();
    }

    public Self customBlock(DeferredRegister<Block> register, DeferredRegister<Item> blockItemRegister, Function<MultiblockRegistration<State>, ? extends MultiblockPartBlock<State>> make, Function<Block, Item> makeItem) { return customBlock(fromDeferred(register), fromDeferred(blockItemRegister), make, makeItem); }

    public Self customBlock(RegistrationMethod<Block> register, RegistrationMethod<Item> blockItemRegister, Function<MultiblockRegistration<State>, ? extends MultiblockPartBlock<State>> make, Function<Block, Item> makeItem) {
        Preconditions.checkState(this.block == null);
        this.block = register.register(name.getPath(), () -> make.apply(this.result));
        this.item = blockItemRegister.register(name.getPath(), () -> makeItem.apply(this.result.block().get()));
        return self();
    }

    public Self structure(Supplier<TemplateMultiblock> structure) {
        Preconditions.checkState(this.getMasterPosInMB == null);
        Preconditions.checkState(this.disassemble == null);
        this.getMasterPosInMB = () -> structure.get().getMasterFromOriginOffset();
        this.getSize = l -> structure.get().getSize(l);
        this.disassemble = (level, origin, orientation) -> structure.get().disassemble(
                level, origin, orientation.mirrored(), orientation.front()
        );
        this.structure = l -> structure.get().getStructure(l);
        return self();
    }

    public Self component(IMultiblockComponent<State> extraComponent) { return component(extraComponent, s -> s); }

    public <CS> Self component(IMultiblockComponent<CS> extraComponent, StateWrapper<State, CS> makeState) {
        extraComponents.add(new ExtraComponent<>(extraComponent, makeState));
        return self();
    }

    public <CS, C extends IMultiblockComponent<CS> & StateWrapper<State, CS>>
    Self selfWrappingComponent(C extraComponent) { return component(extraComponent, extraComponent); }

    public MultiblockRegistration<State> build(Consumer<Consumer<IEventBus>> registerToModBus) {
        Objects.requireNonNull(logic);
        Objects.requireNonNull(masterBE);
        Objects.requireNonNull(dummyBE);
        Objects.requireNonNull(block);
        Objects.requireNonNull(item);
        Objects.requireNonNull(getMasterPosInMB);
        Objects.requireNonNull(getSize);
        Objects.requireNonNull(disassemble);
        Objects.requireNonNull(structure);
        Preconditions.checkState(this.result == null);
        this.result = new MultiblockRegistration<>(
                logic, extraComponents, masterBE, dummyBE, block, item,
                mirrorable, hasComparatorOutput, redstoneInputAware,
                getMasterPosInMB, getSize, disassemble, structure, name
        );
        registerToModBus.accept(bus -> bus.addListener(
                RegisterCapabilitiesEvent.class, this::registerCapabilities
        ));
        return this.result;
    }

    private void registerCapabilities(RegisterCapabilitiesEvent ev) {
        logic.registerCapabilities(MultiblockOverride.wrap(name, new CapabilityRegistrar<>() {
            @Override public <T>
            void register(BlockCapability<T, @Nullable Direction> capability, CapabilityGetter<T, State> getter) {
                ICapabilityProvider<IMultiblockBE<State>, @Nullable Direction, T> provider = (be, side) -> {
                    IMultiblockBEHelper<State> helper = be.getHelper();
                    State state = helper.getState();
                    if (state != null) { return getter.getCapability(state, helper.getCapabilityPosition(side)); }
                    else { return null; }
                };
                ev.registerBlockEntity(capability, masterBE.get(), provider);
                ev.registerBlockEntity(capability, dummyBE.get(), provider);
            }
        }));
    }

    @SuppressWarnings("DataFlowIssue") private <BE extends BlockEntity> BlockEntityType<? extends BE> makeBEType(BEConstructor<State, BE> construct) {
        Mutable<BlockEntityType<? extends BE>> resultBox = new MutableObject<>();
        resultBox.setValue(new BlockEntityType<>((pos, state) -> construct.make(resultBox.getValue(), pos, state, result), Set.of(block.get()), null));
        return resultBox.getValue();
    }

    protected abstract Self self();

    public interface RegistrationMethod<Base> {
        <T extends Base>
        Supplier<T> register(String path, Supplier<T> makeInstance);

        static <B> RegistrationMethod<B> fromDeferred(DeferredRegister<B> register) {
            return new RegistrationMethod<>() {
                @Override public <T extends B> Supplier<T> register(String path, Supplier<T> makeInstance) { return register.register(path, makeInstance); }
            };
        }
    }

    public interface BEConstructor<State extends IMultiblockState, T extends BlockEntity> {
        T make(BlockEntityType<?> type, BlockPos pos, BlockState state, MultiblockRegistration<State> multiblock);
    }
}
