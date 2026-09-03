package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ClearTankRegistry {
    public static final String KEY_TANK_CLEARED = "gui.immersiveconvergence.input_tank_cleared";
    public static final String KEY_TANKS_CLEARED = "gui.immersiveconvergence.input_tanks_cleared";
    private static final Map<ResourceLocation, Entry> ENTRIES = new HashMap<>();

    public record Entry(Set<BlockPos> inputPositions, ToIntFunction<Object> clear) {}

    public static void register(ResourceLocation multiblock, Collection<BlockPos> inputPositions, ToIntFunction<Object> clear) { ENTRIES.put(multiblock, new Entry(Set.copyOf(inputPositions), clear)); }

    @Nullable public static InteractionResult handle(ResourceLocation multiblock, BlockPos posInMultiblock, IMultiblockContext<?> context, Player player, InteractionHand hand) {
        Entry entry = ENTRIES.get(multiblock);
        if (entry == null || !entry.inputPositions().contains(posInMultiblock) || !player.isShiftKeyDown()) { return null; }
        ItemStack held = player.getItemInHand(hand);
        if (!Utils.isHammer(held) && !ClearTank.additionalTool.test(held)) { return null; }
        boolean isClient = player.level().isClientSide;
        if (!isClient) {
            int cleared = entry.clear().applyAsInt(context.getState());
            context.markMasterDirty();
            context.requestMasterBESync();
            player.displayClientMessage(Component.translatable(cleared > 1 ? KEY_TANKS_CLEARED : KEY_TANK_CLEARED), true);
        }
        return InteractionResult.sidedSuccess(isClient);
    }
}
