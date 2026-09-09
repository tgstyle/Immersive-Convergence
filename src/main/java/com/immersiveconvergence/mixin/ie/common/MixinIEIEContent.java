package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.core.ICMixinConfig;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPipe;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPump;
import com.immersiveconvergence.common.blocks.pipes.TileEntityFluidPipeAlternative;
import com.immersiveconvergence.common.blocks.pipes.TileEntityFluidPumpAlternative;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IEContent.class, remap = false)
public abstract class MixinIEIEContent {
    @Inject(method = "registerTile(Ljava/lang/Class;)V", at = @At("HEAD"), cancellable = true)
    private static void injectRegisterTile(Class<? extends TileEntity> tile, CallbackInfo ci) {
        if (ICMixinConfig.mixinSettings.replaceIEPipes && (tile == TileEntityFluidPipe.class || tile == TileEntityFluidPump.class)) {
            Class<? extends TileEntity> alt = (tile == TileEntityFluidPipe.class) ? TileEntityFluidPipeAlternative.class : TileEntityFluidPumpAlternative.class;
            String s = tile.getSimpleName();
            s = s.substring("TileEntity".length());
            GameRegistry.registerTileEntity(alt, new ResourceLocation(ImmersiveEngineering.MODID, s));
            ICLogger.info("Replaced the Immersive Engineering " + s + " registration with Immersive Convergence's");
            ci.cancel();
        }
    }
}
