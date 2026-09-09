package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.core.ICMixinConfig;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConveyorBelt;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConveyorVertical;
import com.immersiveconvergence.common.blocks.conveyors.TileEntityConveyorBeltAlternative;
import com.immersiveconvergence.common.blocks.conveyors.TileEntityConveyorVerticalAlternative;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IEContent.class, remap = false)
public abstract class MixinIEContentConveyors {
    @Inject(method = "registerTile(Ljava/lang/Class;)V", at = @At("HEAD"), cancellable = true)
    private static void injectRegisterTile(Class<? extends TileEntity> tile, CallbackInfo ci) {
        if (ICMixinConfig.mixinSettings.replaceIEConveyors && (tile == TileEntityConveyorBelt.class || tile == TileEntityConveyorVertical.class)) {
            Class<? extends TileEntity> alt = tile == TileEntityConveyorBelt.class ? TileEntityConveyorBeltAlternative.class : TileEntityConveyorVerticalAlternative.class;
            String s = tile.getSimpleName().substring("TileEntity".length());
            GameRegistry.registerTileEntity(alt, new ResourceLocation(ImmersiveEngineering.MODID, s));
            ICLogger.info("Replaced the Immersive Engineering " + s + " registration with Immersive Convergence's");
            ci.cancel();
        }
    }
}
