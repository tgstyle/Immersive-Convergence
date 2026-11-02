package com.immersiveconvergence.mixin.integration.immersiveengineering.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.BlockIETileProvider;
import blusunrize.immersiveengineering.common.blocks.ItemBlockIEBase;
import blusunrize.immersiveengineering.common.blocks.metal.BlockMetalDevice1;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDevice1;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPipe;
import com.immersiveconvergence.api.fluid.IICPipe;
import com.immersiveconvergence.core.config.ICMixinConfig;

import com.immersiveconvergence.common.blocks.pipes.TileEntityFluidPipeAlternative;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.property.Properties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

@Mixin(value = BlockMetalDevice1.class, remap = false) public abstract class MixinBlockMetalDevice1 extends BlockIETileProvider<BlockTypes_MetalDevice1> {
    protected MixinBlockMetalDevice1() { super("metal_device1", Material.IRON, PropertyEnum.create("type", BlockTypes_MetalDevice1.class), ItemBlockIEBase.class, IEProperties.FACING_ALL, IEProperties.MULTIBLOCKSLAVE, IEProperties.BOOLEANS[0], Properties.AnimationProperty, IOBJModelCallback.PROPERTY, IEProperties.OBJ_TEXTURE_REMAP); }

    @Inject(method = "createBasicTE(Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/common/blocks/metal/BlockTypes_MetalDevice1;)Lnet/minecraft/tileentity/TileEntity;", at = @At("HEAD"), cancellable = true, remap = false) private void injectCreateTE(World world, BlockTypes_MetalDevice1 type, CallbackInfoReturnable<TileEntity> cir) { if (type == BlockTypes_MetalDevice1.FLUID_PIPE && ICMixinConfig.mixinSettings.replaceIEPipes) { cir.setReturnValue(new TileEntityFluidPipeAlternative()); } }

    /**
     * @author tgstyle
     * @reason Handle pipe break logic conditionally
     */
    @Overwrite public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, IBlockState state) {
        if (state.getBlock().getMetaFromState(state) == BlockTypes_MetalDevice1.FLUID_PIPE.getMeta()) {
            TileEntity te = world.getTileEntity(pos);
            if (!ICMixinConfig.mixinSettings.replaceIEPipes && te instanceof TileEntityFluidPipe) {
                TileEntityFluidPipe here = (TileEntityFluidPipe)te;
                for (int i = 0; i < 6; i++) { if (here.sideConfig[i] == -1) { EnumFacing f = EnumFacing.VALUES[i]; TileEntity there = world.getTileEntity(pos.offset(f)); if (there instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)there).toggleSide(f.getOpposite().ordinal()); } } }
            }
            else if (ICMixinConfig.mixinSettings.replaceIEPipes && te instanceof IICPipe) {
                IICPipe here = (IICPipe)te;
                for (int i = 0; i < 6; i++) { if (here.getSideConfig()[i] == -1) { EnumFacing f = EnumFacing.VALUES[i]; TileEntity there = world.getTileEntity(pos.offset(f)); if (there instanceof IICPipe) { ((IICPipe)there).toggleSide(f.getOpposite().ordinal()); } } }
            }
            if (ICMixinConfig.mixinSettings.replaceIEPipes && te instanceof TileEntityFluidPipeAlternative) {
                for (EnumFacing neighborDirection : EnumFacing.values()) {
                    TileEntity neighbor = world.getTileEntity(pos.offset(neighborDirection));
                    if (neighbor instanceof TileEntityFluidPipeAlternative) { ((TileEntityFluidPipeAlternative)neighbor).neighborPipeRemoved(neighborDirection.getOpposite()); }
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Redirect(method = "neighborChanged", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentHashMap;clear()V", ordinal = 0, remap = false), remap = true) private void redirectNeighborClear(ConcurrentHashMap<?, ?> instance) { if (ICMixinConfig.mixinSettings.replaceIEPipes) { TileEntityFluidPipeAlternative.indirectConnections.clear(); }
    else { instance.clear(); } }

    @Redirect(method = "getStateForPlacement(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFILnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/state/IBlockState;", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentHashMap;clear()V", ordinal = 0, remap = false), remap = true) private void redirectPlacementClear(ConcurrentHashMap<?, ?> instance) { if (ICMixinConfig.mixinSettings.replaceIEPipes) { TileEntityFluidPipeAlternative.indirectConnections.clear(); }
    else { instance.clear(); } }
}
