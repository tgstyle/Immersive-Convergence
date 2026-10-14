package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.multiblock.ClientMultiblocks;
import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;
import com.immersiveconvergence.api.multiblock.MultiblockOverride;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockAdvancementTrigger;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.CapabilityGetter;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.CapabilityRegistrar;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.lib.manual.ManualInstance;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.neoforge.capabilities.BlockCapability;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class IEMultiblockBridge {
    private IEMultiblockBridge() {}

    public static void register(IMultiblock multiblock) { MultiblockHandler.registerMultiblock(new Adapter(multiblock)); }

    public static boolean isHammer(ItemStack stack) { return stack.is(IEItems.Tools.HAMMER.asItem()); }

    public static ItemStack pickBlock(BlockState state) { return Utils.getPickBlock(state); }

    public static boolean isPartBlock(Block block) { return block instanceof MultiblockPartBlock<?>; }

    public static boolean isStrayDummy(@Nullable BlockEntity part, BlockPos cell) { return part instanceof IMultiblockBE<?> be && !(be.getHelper() instanceof IMultiblockBEHelperMaster) && cell.equals(be.getHelper().getPositionInMB()); }

    public static void markDisassembling(@Nullable BlockEntity be) {
        if (be instanceof IMultiblockBE<?> multiblockBE) { multiblockBE.getHelper().markDisassembling(); }
    }

    public static <State> CapabilityRegistrar<State> wrapOverride(ResourceLocation id, CapabilityRegistrar<State> inner) {
        return new CapabilityRegistrar<>() {
            @Override public <T> void register(BlockCapability<T, Direction> capability, CapabilityGetter<T, State> getter) {
                inner.register(capability, (state, position) -> {
                    MultiblockOverride override = MultiblockOverride.get(id);
                    if (override == null || override.ignores(capability)) { return getter.getCapability(state, position); }
                    com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition mapped = override.map(capability, new com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition(position.posInMultiblock(), position.side() == null ? null : com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace.valueOf(position.side().name())));
                    return mapped == null ? null : getter.getCapability(state, new CapabilityPosition(mapped.posInMultiblock(), mapped.side() == null ? null : RelativeBlockFace.valueOf(mapped.side().name())));
                });
            }
        };
    }

    public static void resetManual() {
        ManualInstance manual = ManualHelper.getManual();
        if (manual != null) { manual.reset(); }
    }

    @Nullable public static Component displayName(ResourceLocation name) {
        MultiblockHandler.IMultiblock multiblock = MultiblockHandler.getByUniqueName(name);
        return multiblock == null ? null : multiblock.getDisplayName();
    }

    public static boolean formFirstMatching(Level level, BlockPos pos, Direction side, @Nullable Player player, ItemStack tool, Predicate<ResourceLocation> allowed) {
        BlockState state = level.getBlockState(pos);
        for (MultiblockHandler.IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (!multiblock.isBlockTrigger(state, side, level) || !allowed.test(multiblock.getUniqueName())) { continue; }
            if (MultiblockHandler.postMultiblockFormationEvent(player, multiblock, pos, tool).isCanceled()) { continue; }
            if (multiblock.createStructure(level, pos, side, player)) {
                if (player instanceof ServerPlayer serverPlayer) { MultiblockAdvancementTrigger.INSTANCE.get().trigger(serverPlayer, multiblock, tool); }
                return true;
            }
        }
        return false;
    }

    private record Adapter(IMultiblock multiblock) implements MultiblockHandler.IMultiblock {
        @Override public ResourceLocation getUniqueName() { return multiblock.getUniqueName(); }

        @Override public boolean isBlockTrigger(BlockState state, Direction side, @Nonnull Level world) { return multiblock.isBlockTrigger(state, side, world); }

        @Override public boolean createStructure(Level world, BlockPos pos, Direction side, Player player) { return multiblock.createStructure(world, pos, side, player); }

        @Override public List<StructureBlockInfo> getStructure(@Nonnull Level world) { return multiblock.getStructure(world); }

        @Override public float getManualScale() { return multiblock.getManualScale(); }

        @Override public Vec3i getSize(@Nonnull Level world) { return multiblock.getSize(world); }

        @Override public void disassemble(Level world, BlockPos startPos, boolean mirrored, Direction clickDirectionAtCreation) { multiblock.disassemble(world, startPos, mirrored, clickDirectionAtCreation); }

        @Override public BlockPos getTriggerOffset() { return multiblock.getTriggerOffset(); }

        @Override public void initializeClient(Consumer<MultiblockManualData> consumer) { consumer.accept(new ManualData(ClientMultiblocks.get(multiblock))); }

        @Override public Component getDisplayName() { return multiblock.getDisplayName(); }

        @Override public Block getBlock() { return multiblock.getBlock(); }
    }

    private record ManualData(ClientMultiblocks.MultiblockManualData data) implements MultiblockManualData {
        @Override public NonNullList<ItemStack> getTotalMaterials() { return data.getTotalMaterials(); }

        @Override public boolean canRenderFormedStructure() { return data.canRenderFormedStructure(); }

        @Override public void renderFormedStructure(PoseStack transform, MultiBufferSource buffer) { data.renderFormedStructure(transform, buffer); }
    }
}
