package com.immersiveconvergence.common.blocks;

import com.immersiveconvergence.api.block.ICBlockTileProvider;
import com.immersiveconvergence.api.block.ICItemBlockBase;
import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IConfigurableSides;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.common.ICBlockContext;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityCreativeEnergySource;
import com.immersiveconvergence.common.blocks.types.ICBlockType_CreativeEnergySource;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ICBlockCreativeEnergySource extends ICBlockTileProvider<ICBlockType_CreativeEnergySource> {
    private static final PropertyEnum<ICSideConfig>[] SIDES = createSideProperties();

    public ICBlockCreativeEnergySource() {
        super(ICBlockContext.CONTEXT, "creative_energy_source", Material.IRON, PropertyEnum.create("type", ICBlockType_CreativeEnergySource.class), ICItemBlockBase.class, (Object)SIDES);
        this.setHardness(3.0F);
        this.setResistance(15.0F);
    }

    @SuppressWarnings("unchecked") private static PropertyEnum<ICSideConfig>[] createSideProperties() {
        PropertyEnum<ICSideConfig>[] array = new PropertyEnum[EnumFacing.VALUES.length];
        for (EnumFacing facing : EnumFacing.VALUES) { array[facing.getIndex()] = PropertyEnum.create(facing.getName(), ICSideConfig.class); }
        return array;
    }

    @Override @Nonnull public String getTranslationKey(ItemStack stack) { return getTranslationKey(); }

    @Override public boolean allowHammerHarvest(IBlockState state) { return true; }

    @Override public TileEntity createBasicTE(World world, ICBlockType_CreativeEnergySource type) { return new TileEntityCreativeEnergySource(); }

    @Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        state = super.getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IConfigurableSides) {
            for (EnumFacing facing : EnumFacing.VALUES) { state = state.withProperty(SIDES[facing.getIndex()], ((IConfigurableSides)tile).sideConfig(facing.getIndex())); }
        }
        return state;
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IConfigurableSides && ICUtils.isToggleTool(player.getHeldItem(hand))) {
            if (!world.isRemote) { ((IConfigurableSides)tile).toggleSide(player.isSneaking() ? side.getOpposite().getIndex() : side.getIndex(), player); }
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
}
