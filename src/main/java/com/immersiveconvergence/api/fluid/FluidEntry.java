package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.registration.BlockEntry;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record FluidEntry(RegistryObject<BaseFluid> flowing, RegistryObject<BaseFluid> still, BlockEntry<BaseFluidBlock> block, RegistryObject<BucketItem> bucket, RegistryObject<FluidType> type, List<Property<?>> properties, int tintColor) {
    public static FluidEntry make(FluidRegisters registers, String name, ResourceLocation stillTex, ResourceLocation flowingTex) { return make(registers, name, 0, stillTex, flowingTex, null, -1); }

    public static FluidEntry make(FluidRegisters registers, String name, int burnTime, ResourceLocation stillTex, ResourceLocation flowingTex, @Nullable Consumer<FluidType.Properties> buildAttributes, int tintColor) { return make(registers, name, burnTime, stillTex, flowingTex, BaseFluid::new, BaseFluid.Flowing::new, buildAttributes, ImmutableList.of(), tintColor); }

    public static FluidEntry make(FluidRegisters registers, String name, int burnTime, ResourceLocation stillTex, ResourceLocation flowingTex, Function<FluidEntry, ? extends BaseFluid> makeStill, Function<FluidEntry, ? extends BaseFluid> makeFlowing, @Nullable Consumer<FluidType.Properties> buildAttributes, List<Property<?>> properties, int tintColor) {
        FluidType.Properties builder = FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
        if (buildAttributes != null) { buildAttributes.accept(builder); }
        RegistryObject<FluidType> type = registers.fluidTypes().register(name, () -> makeTypeWithTextures(builder, stillTex, flowingTex, tintColor));
        Mutable<FluidEntry> thisMutable = new MutableObject<>();
        RegistryObject<BaseFluid> still = registers.fluids().register(name, () -> BaseFluid.makeFluid(makeStill, thisMutable.getValue()));
        RegistryObject<BaseFluid> flowing = registers.fluids().register(name + "_flowing", () -> BaseFluid.makeFluid(makeFlowing, thisMutable.getValue()));
        BlockEntry<BaseFluidBlock> block = registers.block(name + "_fluid_block", () -> BlockBehaviour.Properties.copy(Blocks.WATER), p -> new BaseFluidBlock(thisMutable.getValue(), p));
        RegistryObject<BucketItem> bucket = registers.items().register(name + "_bucket", () -> makeBucket(still, burnTime));
        FluidEntry entry = new FluidEntry(flowing, still, block, bucket, type, properties, tintColor);
        thisMutable.setValue(entry);
        return entry;
    }

    private static FluidType makeTypeWithTextures(FluidType.Properties builder, ResourceLocation stillTex, ResourceLocation flowingTex, int tintColor) {
        return new FluidType(builder) {
            @Override public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override public ResourceLocation getStillTexture() { return stillTex; }

                    @Override public ResourceLocation getFlowingTexture() { return flowingTex; }

                    @Override public int getTintColor() { return tintColor; }
                });
            }
        };
    }

    private static BucketItem makeBucket(RegistryObject<BaseFluid> still, int burnTime) {
        return new BucketItem(still, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)) {
            @Override @Nonnull public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt) { return new FluidBucketWrapper(stack); }

            @Override public int getBurnTime(ItemStack itemStack, RecipeType<?> type) { return burnTime; }

            public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable HitResult target) {
                boolean result;
                if (target == null) { result = super.emptyContents(player, level, pos, null, null); }
                else if (target instanceof BlockHitResult blockHitResult) { result = super.emptyContents(player, level, pos, blockHitResult, null); }
                else { return false; }
                if (result) {
                    FluidState placedState = level.getFluidState(pos);
                    if (placedState.getType().getFluidType().getDensity() < 0) { level.scheduleTick(pos, placedState.getType(), 100); }
                }
                return result;
            }
        };
    }

    public BaseFluid getFlowing() { return flowing.get(); }

    public BaseFluid getStill() { return still.get(); }

    public BaseFluidBlock getBlock() { return block.get(); }

    public BucketItem getBucket() { return bucket.get(); }

    public RegistryObject<BaseFluid> getStillGetter() { return still; }

    public static void registerDispenserBehavior(Collection<FluidEntry> entries) {
        for (FluidEntry entry : entries) { DispenserBlock.registerBehavior(entry.getBucket(), BaseFluid.BUCKET_DISPENSE_BEHAVIOR); }
    }

    public static void registerRenderLayers(Collection<FluidEntry> entries) {
        for (FluidEntry entry : entries) {
            ItemBlockRenderTypes.setRenderLayer(entry.getStill(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(entry.getFlowing(), RenderType.translucent());
        }
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event, Collection<FluidEntry> entries) {
        for (FluidEntry entry : entries) {
            final int tint = entry.tintColor();
            event.register((stack, index) -> index == 1 ? tint : -1, entry.bucket().get());
        }
    }

    public static void registerBlockColors(RegisterColorHandlersEvent.Block event, Collection<FluidEntry> entries) {
        for (FluidEntry entry : entries) {
            final int tint = entry.tintColor();
            event.register((state, level, pos, index) -> tint, entry.block().get());
        }
    }
}
