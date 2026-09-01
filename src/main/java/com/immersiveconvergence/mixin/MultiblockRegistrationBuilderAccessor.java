package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(value = MultiblockRegistrationBuilder.class, remap = false)
public interface MultiblockRegistrationBuilderAccessor {
    @Accessor(value = "name", remap = false) ResourceLocation ic$getName();

    @Accessor(value = "block", remap = false) Supplier<? extends Block> ic$getBlock();

    @SuppressWarnings("rawtypes")
    @Accessor(value = "masterBE", remap = false) void ic$setMasterBE(Supplier value);

    @SuppressWarnings("rawtypes")
    @Accessor(value = "dummyBE", remap = false) void ic$setDummyBE(Supplier value);
}
