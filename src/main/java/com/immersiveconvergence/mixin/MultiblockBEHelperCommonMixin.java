package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.ClearTankRegistry;
import com.immersiveconvergence.api.multiblock.IDisassemblingAware;
import com.immersiveconvergence.api.multiblock.MultiblockOverride;
import com.immersiveconvergence.api.multiblock.QueueProcessor;
import com.immersiveconvergence.api.multiblock.ShapeData;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperMaster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.EnumMap;
import java.util.Map;

@Mixin(MultiblockBEHelperCommon.class)
public abstract class MultiblockBEHelperCommonMixin implements IDisassemblingAware {
    @Shadow(remap = false) private boolean beingDisassembled;

    @Shadow(remap = false) protected abstract IMultiblockBEHelperMaster<?> getMasterHelperWithChunkloads();

    @Shadow(remap = false) protected abstract MultiblockBEHelperMaster<?> getMasterHelper();
    @Shadow(remap = false) @Final protected BlockEntity be;
    @Shadow(remap = false) @Final protected MultiblockRegistration<?> multiblock;
    @Shadow(remap = false) @Final protected MultiblockOrientation orientation;
    @Unique private Map<ShapeType, VoxelShape> ic$overrideShapes;
    @Unique private BlockPos ic$overrideShapesPos;

    @Override public boolean ic$isDisassembling() { return beingDisassembled; }

    @Inject(method = "disassemble", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$queueDisassembly(CallbackInfo ci) {
        if (beingDisassembled) { return; }
        IMultiblockBEHelper<?> self = (IMultiblockBEHelper<?>) this;
        MultiblockRegistration<?> registration = self.getMultiblock();
        if (QueueProcessor.MANAGED.contains(registration.id())) { return; }
        IMultiblockBEHelperMaster<?> master = getMasterHelperWithChunkloads();
        if (master == null) { return; }
        IMultiblockLevel mbLevel = master.getContext().getLevel();
        if (!(mbLevel.getRawLevel() instanceof ServerLevel serverLevel)) { return; }
        MultiblockOrientation orientation = mbLevel.getOrientation();
        Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, orientation.front());
        if (rot == null) { return; }
        Mirror mirror = orientation.mirrored() ? Mirror.FRONT_BACK : Mirror.NONE;
        BlockPos origin = mbLevel.getAbsoluteOrigin();
        BlockPos masterPos = mbLevel.toAbsolute(registration.getMasterPosInMB().get());
        if (QueueProcessor.disassemble(serverLevel, registration.getStructure().apply(serverLevel), origin, mirror, rot, masterPos, false)) {
            beingDisassembled = true;
            BlockPos brokenPos = mbLevel.toAbsolute(self.getPositionInMB());
            serverLevel.removeBlock(brokenPos, false);
            QueueProcessor.refreshLight(serverLevel, brokenPos);
            ci.cancel();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$overrideShape(CollisionContext ctx, ShapeType type, CallbackInfoReturnable<VoxelShape> cir) {
        MultiblockOverride override = MultiblockOverride.get(multiblock.id());
        if (override == null || be.getLevel() == null) { return; }
        ShapeData shape = override.shape(multiblock.id(), multiblock.size(be.getLevel()));
        if (shape == null) { return; }
        IMultiblockBEHelper self = (IMultiblockBEHelper) this;
        BlockPos posInMB = self.getPositionInMB();
        if (!posInMB.equals(ic$overrideShapesPos)) {
            ic$overrideShapes = new EnumMap<>(ShapeType.class);
            ic$overrideShapesPos = posInMB.immutable();
        }
        VoxelShape absolute = ic$overrideShapes.computeIfAbsent(type, t -> orientation.transformRelativeShape(shape.getter.apply(posInMB)));
        if (ctx != null && multiblock.postProcessesShape()) {
            IMultiblockContext context = self.getContext();
            if (context != null) { absolute = multiblock.logic().postProcessAbsoluteShape(context, absolute, ctx, posInMB, type); }
        }
        cir.setReturnValue(absolute);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(method = "getCapability", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/multiblocks/blocks/logic/IMultiblockLogic;getCapability(Lblusunrize/immersiveengineering/api/multiblocks/blocks/env/IMultiblockContext;Lblusunrize/immersiveengineering/api/multiblocks/blocks/util/CapabilityPosition;Lnet/minecraftforge/common/capabilities/Capability;)Lnet/minecraftforge/common/util/LazyOptional;"), remap = false)
    private LazyOptional ic$overridePorts(IMultiblockLogic logic, IMultiblockContext ctx, CapabilityPosition position, Capability cap) {
        MultiblockOverride override = MultiblockOverride.get(multiblock.id());
        if (override == null || !override.covers(cap)) { return logic.getCapability(ctx, position, cap); }
        CapabilityPosition mapped = override.map(cap, position);
        return mapped == null ? LazyOptional.empty() : logic.getCapability(ctx, mapped, cap);
    }

    @Inject(method = "click", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$clearTank(Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        MultiblockBEHelperMaster<?> master = getMasterHelper();
        if (master == null) { return; }
        IMultiblockBEHelper<?> self = (IMultiblockBEHelper<?>) this;
        InteractionResult result = ClearTankRegistry.handle(self.getMultiblock().id(), self.getPositionInMB(), master.getContext(), player, hand);
        if (result != null) { cir.setReturnValue(result); }
    }
}
