package com.immersiveconvergence.api.fluid;

import com.immersiveconvergence.api.registration.BlockEntry;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record FluidEntry(Supplier<BaseFluid> flowing, Supplier<BaseFluid> still, BlockEntry<BaseFluidBlock> block, Supplier<BucketItem> bucket, Supplier<FluidType> type, List<Property<?>> properties, int tintColor) {
    public static FluidEntry make(FluidRegisters registers, String name, ResourceLocation stillTex, ResourceLocation flowingTex) { return make(registers, name, 0, stillTex, flowingTex, null, -1); }

    public static FluidEntry make(FluidRegisters registers, String name, int burnTime, ResourceLocation stillTex, ResourceLocation flowingTex, @Nullable Consumer<FluidType.Properties> buildAttributes, int tintColor) { return make(registers, name, burnTime, stillTex, flowingTex, BaseFluid::new, BaseFluid.Flowing::new, buildAttributes, ImmutableList.of(), tintColor); }

    public static FluidEntry make(FluidRegisters registers, String name, int burnTime, ResourceLocation stillTex, ResourceLocation flowingTex, Function<FluidEntry, ? extends BaseFluid> makeStill, Function<FluidEntry, ? extends BaseFluid> makeFlowing, @Nullable Consumer<FluidType.Properties> buildAttributes, List<Property<?>> properties, int tintColor) {
        FluidType.Properties builder = FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
        if (buildAttributes != null) { buildAttributes.accept(builder); }
        Supplier<FluidType> type = registers.fluidTypes().register(name, () -> makeTypeWithTextures(builder, stillTex, flowingTex, tintColor));
        Mutable<FluidEntry> thisMutable = new MutableObject<>();
        Supplier<BaseFluid> still = registers.fluids().register(name, () -> BaseFluid.makeFluid(makeStill, thisMutable.getValue()));
        Supplier<BaseFluid> flowing = registers.fluids().register(name + "_flowing", () -> BaseFluid.makeFluid(makeFlowing, thisMutable.getValue()));
        BlockEntry<BaseFluidBlock> block = registers.block(name + "_fluid_block", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), p -> new BaseFluidBlock(thisMutable.getValue(), p));
        Supplier<BucketItem> bucket = registers.items().register(name + "_bucket", () -> makeBucket(still, burnTime));
        FluidEntry entry = new FluidEntry(flowing, still, block, bucket, type, properties, tintColor);
        thisMutable.setValue(entry);
        return entry;
    }

    private static FluidType makeTypeWithTextures(FluidType.Properties builder, ResourceLocation stillTex, ResourceLocation flowingTex, int tintColor) {
        return new FluidType(builder) {
            @SuppressWarnings("removal")
            @Override public void initializeClient(@Nonnull Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override @Nonnull public ResourceLocation getStillTexture() { return stillTex; }

                    @Override @Nonnull public ResourceLocation getFlowingTexture() { return flowingTex; }

                    @Override public int getTintColor() { return tintColor; }
                });
            }
        };
    }

    private static BucketItem makeBucket(Supplier<BaseFluid> still, int burnTime) {
        return new BucketItem(still.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)) {
            @Override public int getBurnTime(@Nonnull ItemStack itemStack, RecipeType<?> type) { return burnTime; }

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

    public Supplier<BaseFluid> getStillGetter() { return still; }

    public static void registerBucketCapabilities(RegisterCapabilitiesEvent event, Collection<FluidEntry> entries) {
        for (FluidEntry entry : entries) { event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), entry.getBucket()); }
    }

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
