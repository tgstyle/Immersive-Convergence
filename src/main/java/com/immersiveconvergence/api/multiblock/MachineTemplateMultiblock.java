package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.client.ICClientUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MachineTemplateMultiblock<T extends ICTileEntityMultiblockPart<T>> extends TemplateMultiblock {
    public IBlockState masterBlockState;
    public IBlockState slaveBlockState;
    public int height;
    public int length;
    public int width;
    public int masterX, masterY, masterZ;
    public PoIJSONSchema[] pointsOfInterest;
    private final double renderX, renderY, renderZ, renderScale;
    @SideOnly(Side.CLIENT) private ItemStack renderStack;

    public MachineTemplateMultiblock(String uniqueName, ShapeData shape, IBlockState master, IBlockState slave, double renderX, double renderY, double renderZ, double renderScale) {
        super(uniqueName, shape);
        this.masterBlockState = master;
        this.slaveBlockState = slave;
        this.width = shape.width;
        this.height = shape.height;
        this.length = shape.length;
        this.masterX = shape.masterPos.getX();
        this.masterY = shape.masterPos.getY();
        this.masterZ = shape.masterPos.getZ();
        this.pointsOfInterest = shape.data != null && shape.data.pointsOfInterest != null ? shape.data.pointsOfInterest : new PoIJSONSchema[0];
        this.renderX = renderX;
        this.renderY = renderY;
        this.renderZ = renderZ;
        this.renderScale = renderScale;
    }

    @Override public boolean overwriteBlockRender(ItemStack stack, int iterator) { return false; }

    @Override public boolean canRenderFormedStructure() { return true; }

    @Override @SideOnly(Side.CLIENT) public void renderFormedStructure() {
        if (renderStack == null) { renderStack = new ItemStack(masterBlockState.getBlock(), 1, masterBlockState.getBlock().getMetaFromState(masterBlockState)); }
        GlStateManager.translate(renderX, renderY, renderZ);
        GlStateManager.rotate(-45, 0, 1, 0);
        GlStateManager.rotate(-20, 1, 0, 0);
        GlStateManager.scale(renderScale, renderScale, renderScale);
        GlStateManager.disableCull();
        ICClientUtils.mc().getRenderItem().renderItem(renderStack, ItemCameraTransforms.TransformType.GUI);
        GlStateManager.enableCull();
    }

    @Override protected void replaceStructureBlock(World world, BlockPos worldPos, BlockPos masterWorldPos, int position, boolean mirrored, EnumFacing side) {
        boolean isMaster = worldPos.equals(masterWorldPos);
        IBlockState placed = isMaster ? masterBlockState : slaveBlockState;
        world.setBlockState(worldPos, placed, 2);
        @SuppressWarnings("unchecked")
        T tile = (T) world.getTileEntity(worldPos);
        if (tile != null) {
            tile.facing = side;
            tile.formed = true;
            tile.pos = position;
            tile.offset = new int[]{worldPos.getX() - masterWorldPos.getX(), worldPos.getY() - masterWorldPos.getY(), worldPos.getZ() - masterWorldPos.getZ()};
            tile.mirrored = mirrored;
            tile.invalidateStructureCaches();
            tile.markDirty();
            world.notifyBlockUpdate(worldPos, placed, placed, 2);
            world.addBlockEvent(worldPos, slaveBlockState.getBlock(), 255, 0);
        }
    }
}
