package com.immersiveconvergence.mixin;

import com.immersiveconvergence.core.ICMixin;
import com.immersiveconvergence.core.ICMixinConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(World.class)
public abstract class MixinMCWorldCMEFix {
    @Unique private static Field immersiveconvergence$processingLoadedTilesField = null;
    @Unique private static boolean immersiveconvergence$fieldChecked = false;

    @Shadow @Final private List<TileEntity> addedTileEntityList;
    @Shadow @Final public List<TileEntity> loadedTileEntityList;
    @Shadow @Final public List<TileEntity> tickableTileEntities;
    @Shadow @Final public boolean isRemote;

    @Inject(method = "addTileEntities(Ljava/util/Collection;)V", at = @At("HEAD"), cancellable = true)
    private void injectAddTileEntities(Collection<TileEntity> tileEntityCollection, CallbackInfo ci) {
        if (!ICMixinConfig.mixinSettings.enableWorldMixin) { return; }
        ci.cancel();
        if (!immersiveconvergence$fieldChecked) {
            immersiveconvergence$fieldChecked = true;
            String[] names = {"processingLoadedTiles", "field_147481_N"};
            for (String name : names) {
                try { Field f = World.class.getDeclaredField(name); f.setAccessible(true); immersiveconvergence$processingLoadedTilesField = f; break; } catch (NoSuchFieldException ignored) { } catch (Exception e) { ICMixin.LOGGER.error("Failed to access processingLoadedTiles field", e); break; }
            }
            if (immersiveconvergence$processingLoadedTilesField == null) { ICMixin.LOGGER.info("processingLoadedTiles field not found in World (CleanroomMC compatibility mode). Using immediate add."); }
        }

        World world = (World)(Object)this;
        List<TileEntity> toAdd = new ArrayList<>(tileEntityCollection);
        boolean processingLoadedTiles = immersiveconvergence$isProcessingLoadedTiles(world);
        if (ICMixinConfig.mixinSettings.enableAdditionsLogging && !toAdd.isEmpty()) {
            String mode = processingLoadedTiles ? "delayed" : "immediate";
            ICMixin.LOGGER.debug("Adding {} TEs ({} add)", toAdd.size(), mode);
        }
        if (processingLoadedTiles) {
            for (TileEntity tile : toAdd) { if (tile.getWorld() != world) { tile.setWorld(world); } addedTileEntityList.add(tile); }
        } else {
            for (TileEntity tile : toAdd) {
                if (tile.getWorld() != world) { tile.setWorld(world); }
                if (immersiveconvergence$isProcessingLoadedTiles(world)) { addedTileEntityList.add(tile); continue; }
                int sizeBefore = loadedTileEntityList.size();
                loadedTileEntityList.add(tile);
                if (tile instanceof ITickable) { tickableTileEntities.add(tile); }
                tile.onLoad();
                if (isRemote) {
                    IBlockState state = world.getBlockState(tile.getPos());
                    world.notifyBlockUpdate(tile.getPos(), state, state, 2);
                }
                int sizeAfter = loadedTileEntityList.size();
                if (ICMixinConfig.mixinSettings.enablePotentialsLogging && sizeAfter > sizeBefore + 1) { ICMixin.LOGGER.warn("Potential CME detected: {} at {}", tile.getClass().getName(), tile.getPos()); }
            }
        }
    }

    @Unique private static boolean immersiveconvergence$isProcessingLoadedTiles(World world) {
        if (immersiveconvergence$processingLoadedTilesField == null) { return false; }
        try { return immersiveconvergence$processingLoadedTilesField.getBoolean(world); } catch (Exception ignored) { return false; }
    }
}
