package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.block.FacingLimitation;
import com.immersiveconvergence.api.multiblock.util.CapabilityUtils;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICEnergyConnector;
import com.immersiveconvergence.api.wires.IICEnergySource;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ICConnectorBlockEntity extends ICConnectableBlockEntity implements IICEnergyConnector, IICEnergySource, BlockInterfaces.IDirectionalBE, BlockInterfaces.IBlockBounds {
    private static final String FACING = "facing";
    protected static final float WIDTH_MIN = 0.3125F;
    protected static final float WIDTH_MAX = 0.6875F;
    private final ICConnectorTier tier;
    private final boolean relay;
    @Nullable private final ICConnectorEnergy energy;
    private final LazyOptional<IEnergyStorage> energyCapability;
    private Direction facing = Direction.DOWN;

    protected ICConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ICConnectorTier tier, boolean relay) {
        super(type, pos, state);
        this.tier = tier;
        this.relay = relay;
        this.energy = relay || tier.getRate() == 0 ? null : new ICConnectorEnergy(this, tier.getRate());
        this.energyCapability = energy == null ? LazyOptional.empty() : CapabilityUtils.constantOptional(energy);
    }

    @Override protected boolean isRelay() { return relay; }

    @Override public boolean canConnect() { return true; }

    protected final void offerBuffer() { if (energy != null) { energy.update(); } }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) { return tier.getCategory() == type.getCategory() && limitAllows(type); }

    @Override public int demand(@Nonnull ICConnectionPoint point) { return energy == null || !energy.canOutput(facing) ? 0 : energy.output(facing, Integer.MAX_VALUE, true); }

    @Override public int accept(@Nonnull ICConnectionPoint point, int amount, boolean simulate) { return energy == null ? 0 : energy.output(facing, amount, simulate); }

    @Override public void onPassthrough(@Nonnull ICConnectionPoint point, int amount) {}

    @Override public boolean isSource(@Nonnull ICConnectionPoint point) { return energy != null; }

    @Override public boolean isSink(@Nonnull ICConnectionPoint point) { return true; }

    @Override public int available() { return energy == null ? 0 : energy.getEnergyStored(); }

    @Override public void extracted(int amount) { if (energy != null) { energy.moved(amount); } }

    @Override @Nonnull public Vec3 offset(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) { return offset(tier.inset(relay) - type.getRenderDiameter() / 2); }

    @Override @Nonnull public VoxelShape getBlockBounds(@Nullable CollisionContext context) { return bounds(0.5F + (float)tier.inset(relay), WIDTH_MIN, WIDTH_MAX); }

    protected final Vec3 offset(double reach) {
        Direction side = facing.getOpposite();
        return new Vec3(0.5 + side.getStepX() * reach, 0.5 + side.getStepY() * reach, 0.5 + side.getStepZ() * reach);
    }

    protected final VoxelShape bounds(float length, float min, float max) {
        return switch (facing) {
            case DOWN -> Shapes.box(min, 0, min, max, length, max);
            case UP -> Shapes.box(min, 1 - length, min, max, 1, max);
            case NORTH -> Shapes.box(min, min, 0, max, max, length);
            case SOUTH -> Shapes.box(min, min, 1 - length, max, max, 1);
            case WEST -> Shapes.box(0, min, min, length, max, max);
            case EAST -> Shapes.box(1 - length, min, min, 1, max, max);
        };
    }

    @Override public Direction getFacing() { return facing; }

    @Override public void setFacing(Direction facing) {
        this.facing = facing;
        syncModelState();
    }

    @Override public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel server && modelState(getBlockState()) != getBlockState()) { server.getServer().tell(new TickTask(server.getServer().getTickCount(), this::syncModelState)); }
    }

    protected BlockState modelState(BlockState state) { return state.hasProperty(BlockStateProperties.FACING) ? state.setValue(BlockStateProperties.FACING, facing) : state; }

    protected final void syncModelState() {
        if (level == null || level.isClientSide || isRemoved()) { return; }
        BlockState state = getBlockState();
        BlockState wanted = modelState(state);
        if (wanted != state) { level.setBlock(worldPosition, wanted, Block.UPDATE_CLIENTS); }
    }

    @Override public FacingLimitation getFacingLimitation() { return FacingLimitation.SIDE_CLICKED; }

    @Override public boolean mirrorFacingOnPlacement() { return true; }

    @Override public boolean canHammerRotate() { return false; }

    @Override @Nonnull public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) { return side == facing ? energyCapability.cast() : LazyOptional.empty(); }
        return super.getCapability(capability, side);
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
    }

    @Override public void readCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        facing = Direction.from3DDataValue(nbt.getInt(FACING));
        if (energy != null) { energy.read(nbt); }
    }

    @Override public void writeCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.putInt(FACING, facing.get3DDataValue());
        if (energy != null) { energy.write(nbt); }
    }
}
