package com.immersiveconvergence.mixin.ip.client;

import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.common.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EventHandler.class)
public abstract class MixinIPEventHandler {
    @Redirect(method = "handleLubricatingMachines", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lflaxbeard/immersivepetroleum/api/crafting/LubricatedHandler$LubricatedTileInfo;ticks:I", remap = false), remap = false)
    private static void redirectSkipDuplicateCountdown(LubricatedHandler.LubricatedTileInfo info, int remaining) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isIntegratedServerRunning() && mc.isCallingFromMinecraftThread()) { return; }
        info.ticks = remaining;
    }

    @Redirect(method = "renderChunkBorder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;", remap = true), remap = false)
    private static Chunk redirectSampleChunkCoords(World world, int chunkX, int chunkZ) { return world.getChunk(chunkX >> 4, chunkZ >> 4); }
}
