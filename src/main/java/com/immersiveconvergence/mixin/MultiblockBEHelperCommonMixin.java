package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.ClearTankRegistry;
import com.immersiveconvergence.api.multiblock.IDisassemblingAware;
import com.immersiveconvergence.api.multiblock.QueueProcessor;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiblockBEHelperCommon.class)
public abstract class MultiblockBEHelperCommonMixin implements IDisassemblingAware {
    @Shadow(remap = false) private boolean beingDisassembled;

    @Shadow(remap = false) protected abstract IMultiblockBEHelperMaster<?> getMasterHelperWithChunkloads();

    @Shadow(remap = false) protected abstract MultiblockBEHelperMaster<?> getMasterHelper();

    @Override public boolean ic$isDisassembling() { return beingDisassembled; }

    @Inject(method = "disassemble", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$queueDisassembly(CallbackInfo ci) {
        if (beingDisassembled) { return; }
        IMultiblockBEHelper<?> self = (IMultiblockBEHelper<?>) (Object) this;
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
            serverLevel.removeBlock(mbLevel.toAbsolute(self.getPositionInMB()), false);
            ci.cancel();
        }
    }

    @Inject(method = "click", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$clearTank(Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        MultiblockBEHelperMaster<?> master = getMasterHelper();
        if (master == null) { return; }
        IMultiblockBEHelper<?> self = (IMultiblockBEHelper<?>) (Object) this;
        InteractionResult result = ClearTankRegistry.handle(self.getMultiblock().id(), self.getPositionInMB(), master.getContext(), player, hand);
        if (result != null) { cir.setReturnValue(result); }
    }
}
