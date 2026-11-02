package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;
import com.immersiveconvergence.api.multiblock.blockimpl.MultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blockimpl.MultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.shape.BlockMatchers;
import com.immersiveconvergence.api.multiblock.util.MultiblockAdvancementTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

public class MultiblockRegistry {
    private static final List<IMultiblock> PENDING_IE = new ArrayList<>();
    private static boolean ownTrigger;

    public static void init(IEventBus modEventBus) {
        IMultiblockBEHelperMaster.MAKE_HELPER.setValue(MultiblockBEHelperMaster::new);
        IMultiblockBEHelperDummy.MAKE_HELPER.setValue(MultiblockBEHelperDummy::new);
        BlockMatchers.init();
        ownTrigger = !ModList.get().isLoaded(ICMods.IMMERSIVE_ENGINEERING);
        if (ownTrigger) { MultiblockAdvancementTrigger.REGISTER.register(modEventBus); }
        registerPendingWithIE();
    }

    public static <T extends IMultiblock> T register(T multiblock) {
        MultiblockHandler.registerMultiblock(multiblock);
        if (ICMods.immersiveEngineering()) { registerWithIE(multiblock); }
        return multiblock;
    }

    private static synchronized void registerWithIE(IMultiblock multiblock) {
        if (ICBridges.installed()) { ICBridges.multiblock().register(multiblock); }
        else { PENDING_IE.add(multiblock); }
    }

    private static synchronized void registerPendingWithIE() {
        for (IMultiblock multiblock : PENDING_IE) { ICBridges.multiblock().register(multiblock); }
        PENDING_IE.clear();
    }

    @Nullable public static Component displayName(ResourceLocation name) {
        if (ICMods.immersiveEngineering()) { return ICBridges.multiblock().displayName(name); }
        IMultiblock multiblock = MultiblockHandler.getByUniqueName(name);
        return multiblock == null ? null : multiblock.getDisplayName();
    }

    public static boolean formFirstMatching(Level level, BlockPos pos, Direction side, @Nullable Player player, ItemStack tool, Predicate<ResourceLocation> allowed) {
        if (ICMods.immersiveEngineering()) { return ICBridges.multiblock().formFirstMatching(level, pos, side, player, tool, allowed); }
        BlockState state = level.getBlockState(pos);
        for (IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (!multiblock.isBlockTrigger(state, side, level) || !allowed.test(multiblock.getUniqueName())) { continue; }
            if (multiblock.createStructure(level, pos, side, player)) {
                if (ownTrigger && player instanceof ServerPlayer serverPlayer) { MultiblockAdvancementTrigger.INSTANCE.get().trigger(serverPlayer, multiblock, tool); }
                return true;
            }
        }
        return false;
    }
}
