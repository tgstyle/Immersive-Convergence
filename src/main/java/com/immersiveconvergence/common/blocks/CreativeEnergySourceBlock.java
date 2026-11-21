package com.immersiveconvergence.common.blocks;

import com.immersiveconvergence.api.block.Enums;
import com.immersiveconvergence.api.block.HammerUse;
import com.immersiveconvergence.api.block.ModEntityBlock;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.common.blocks.logic.CreativeEnergySourceBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class CreativeEnergySourceBlock extends ModEntityBlock<CreativeEnergySourceBlockEntity> {
    private static final Map<Direction, EnumProperty<Enums.IOSideConfig>> SIDES = createSideProperties();

    public CreativeEnergySourceBlock(BiFunction<BlockPos, BlockState, CreativeEnergySourceBlockEntity> makeEntity, Properties blockProps) { super(makeEntity, blockProps); }

    private static Map<Direction, EnumProperty<Enums.IOSideConfig>> createSideProperties() {
        Map<Direction, EnumProperty<Enums.IOSideConfig>> sides = new EnumMap<>(Direction.class);
        for (Direction side : DirectionUtils.VALUES) { sides.put(side, EnumProperty.create(side.getName(), Enums.IOSideConfig.class)); }
        return sides;
    }

    public static EnumProperty<Enums.IOSideConfig> sideProperty(Direction side) { return SIDES.get(side); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        for (EnumProperty<Enums.IOSideConfig> property : SIDES.values()) { builder.add(property); }
    }

    @Override protected BlockState getInitDefaultState() {
        BlockState state = super.getInitDefaultState();
        for (EnumProperty<Enums.IOSideConfig> property : SIDES.values()) { state = state.setValue(property, Enums.IOSideConfig.OUTPUT); }
        return state;
    }

    @SuppressWarnings("deprecation") @Override @Nonnull public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootParams.Builder builder) { Float r = builder.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS); return r != null && builder.getLevel().getRandom().nextFloat() > 1.0F / r ? List.of() : List.of(new ItemStack(this)); }

    @Override @Nonnull public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (ICUtils.isToggleTool(player.getItemInHand(hand))) { return world.isClientSide ? InteractionResult.SUCCESS : HammerUse.onBlock(world, pos, hit.getDirection(), player, hand, hit.getLocation()); }
        return super.use(state, world, pos, player, hand, hit);
    }
}
