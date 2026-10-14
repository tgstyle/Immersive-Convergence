package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.ClientMultiblocks.MultiblockManualData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockHandler {
    static List<IMultiblock> multiblocks = new ArrayList<>();
    static Map<ResourceLocation, IMultiblock> byUniqueName = new HashMap<>();

    public static synchronized void registerMultiblock(IMultiblock multiblock) {
        multiblocks.add(multiblock);
        byUniqueName.put(multiblock.getUniqueName(), multiblock);
    }

    public static List<IMultiblock> getMultiblocks() { return multiblocks; }

    @Nullable public static IMultiblock getByUniqueName(ResourceLocation name) { return byUniqueName.get(name); }

    public interface IMultiblock {
        ResourceLocation getUniqueName();

        boolean isBlockTrigger(BlockState state, Direction side, @Nonnull Level world);

        boolean createStructure(Level world, BlockPos pos, Direction side, Player player);

        List<StructureBlockInfo> getStructure(@Nonnull Level world);

        float getManualScale();

        Vec3i getSize(@Nonnull Level world);

        void disassemble(Level world, BlockPos startPos, boolean mirrored, Direction clickDirectionAtCreation);

        BlockPos getTriggerOffset();

        void initializeClient(Consumer<MultiblockManualData> consumer);

        Component getDisplayName();

        default Block getBlock() {
            return Blocks.AIR;
        }
    }

    public static MultiblockFormEvent postMultiblockFormationEvent(Player player, IMultiblock multiblock, BlockPos clickedBlock, ItemStack hammer) {
        MultiblockFormEvent event = new MultiblockFormEvent(player, multiblock, clickedBlock, hammer);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static class MultiblockFormEvent extends PlayerEvent implements ICancellableEvent {
        private final IMultiblock multiblock;
        private final BlockPos clickedBlock;
        private final ItemStack hammer;

        public MultiblockFormEvent(Player player, IMultiblock multiblock, BlockPos clickedBlock, ItemStack hammer) {
            super(player);
            this.multiblock = multiblock;
            this.clickedBlock = clickedBlock;
            this.hammer = hammer;
        }

        public IMultiblock getMultiblock() { return multiblock; }

        public BlockPos getClickedBlock() { return clickedBlock; }

        public ItemStack getHammer() { return hammer; }
    }
}
