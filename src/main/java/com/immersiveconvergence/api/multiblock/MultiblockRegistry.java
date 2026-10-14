package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;
import com.immersiveconvergence.api.multiblock.blockimpl.MultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blockimpl.MultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.util.SetRestrictedField;
import com.immersiveconvergence.common.ie.IEMultiblockBridge;

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
import javax.annotation.Nullable;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class MultiblockRegistry {
    private static boolean ownTrigger;

    public static void init(IEventBus modEventBus) {
        SetRestrictedField.startInitializing();
        IMultiblockBEHelperMaster.MAKE_HELPER.setValue(MultiblockBEHelperMaster::new);
        IMultiblockBEHelperDummy.MAKE_HELPER.setValue(MultiblockBEHelperDummy::new);
        SetRestrictedField.lock();
        BlockMatchers.init();
        ownTrigger = !ModList.get().isLoaded(ICMods.IMMERSIVE_ENGINEERING);
        if (ownTrigger) { MultiblockAdvancementTrigger.REGISTER.register(modEventBus); }
    }

    public static <T extends IMultiblock> T register(T multiblock) {
        MultiblockHandler.registerMultiblock(multiblock);
        if (ICMods.immersiveEngineering()) { IEMultiblockBridge.register(multiblock); }
        return multiblock;
    }

    @Nullable public static Component displayName(ResourceLocation name) {
        if (ICMods.immersiveEngineering()) { return IEMultiblockBridge.displayName(name); }
        IMultiblock multiblock = MultiblockHandler.getByUniqueName(name);
        return multiblock == null ? null : multiblock.getDisplayName();
    }

    public static boolean formFirstMatching(Level level, BlockPos pos, Direction side, @Nullable Player player, ItemStack tool, Predicate<ResourceLocation> allowed) {
        if (ICMods.immersiveEngineering()) { return IEMultiblockBridge.formFirstMatching(level, pos, side, player, tool, allowed); }
        BlockState state = level.getBlockState(pos);
        for (IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (!multiblock.isBlockTrigger(state, side, level) || !allowed.test(multiblock.getUniqueName())) { continue; }
            if (MultiblockHandler.postMultiblockFormationEvent(player, multiblock, pos, tool).isCanceled()) { continue; }
            if (multiblock.createStructure(level, pos, side, player)) {
                if (ownTrigger && player instanceof ServerPlayer serverPlayer) { MultiblockAdvancementTrigger.INSTANCE.get().trigger(serverPlayer, multiblock, tool); }
                return true;
            }
        }
        return false;
    }
}
