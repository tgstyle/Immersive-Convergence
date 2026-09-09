package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.block.ICSideConfig;

import blusunrize.immersiveengineering.api.IEEnums.SideConfig;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.IEProperties.PropertyBoolInverted;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("unused")
public class ICBlockInterfaces {
    public interface IComparatorOverride extends IEBlockInterfaces.IComparatorOverride {}

    public interface IGuiTile extends IEBlockInterfaces.IGuiTile {}

    public interface IPlayerInteraction extends IEBlockInterfaces.IPlayerInteraction {}

    public interface ITileDrop extends IEBlockInterfaces.ITileDrop {}

    public interface IBlockOverlayText extends IEBlockInterfaces.IBlockOverlayText {}

    public interface IAttachedIntegerProperies extends IEBlockInterfaces.IAttachedIntegerProperies {}

    public interface IDirectionalTile extends IEBlockInterfaces.IDirectionalTile {}

    public interface IHammerInteraction extends IEBlockInterfaces.IHammerInteraction {}

    public interface IHasDummyBlocks extends IEBlockInterfaces.IHasDummyBlocks {}

    public interface IMetaBlock extends IEBlockInterfaces.IIEMetaBlock {}

    public interface IUsesBooleanProperty extends IEBlockInterfaces.IUsesBooleanProperty {
        default int booleanPropertyIndex(boolean activeState) { return 0; }

        @Override @Nonnull default PropertyBoolInverted getBoolProperty(@Nonnull Class<? extends IEBlockInterfaces.IUsesBooleanProperty> inf) { return IEProperties.BOOLEANS[booleanPropertyIndex(inf == IEBlockInterfaces.IActiveState.class)]; }
    }

    public interface IActiveState extends IEBlockInterfaces.IActiveState, IUsesBooleanProperty {}

    public interface IMirrorAble extends IEBlockInterfaces.IMirrorAble, IUsesBooleanProperty {}

    public interface IConfigurableSides extends IEBlockInterfaces.IConfigurableSides {
        ICSideConfig sideConfig(int side);

        @Override @Nonnull default SideConfig getSideConfig(int side) { return sideConfig(side).toIE(); }
    }

    public interface IBlockBounds extends IEBlockInterfaces.IBlockBounds {}

    public interface ICollisionBounds extends IBlockBounds {
        List<AxisAlignedBB> getAdvancedCollisionBounds();
    }

    public interface ISelectionBounds extends IBlockBounds {
        List<AxisAlignedBB> getAdvancedSelectionBounds();

        boolean isOverrideBox(AxisAlignedBB var1, EntityPlayer var2, RayTraceResult var3, List<AxisAlignedBB> var4);
    }
}
