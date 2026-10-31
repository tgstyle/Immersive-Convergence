package com.immersiveconvergence.api.wires;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ICWireType {
    private static final Map<String, ICWireType> REGISTRY = new ConcurrentHashMap<>();
    private final String uniqueName;
    @Nullable private final ICWireCategory category;
    private final IntSupplier transferRate;
    private final int maxLength;
    private final double lossPerBlock;
    private final DoubleSupplier renderDiameter;
    private final double slack;
    private final int color;
    private final Supplier<ItemStack> coil;
    private final float shockDamage;

    public ICWireType(String uniqueName, @Nullable ICWireCategory category, IntSupplier transferRate, int maxLength, double lossPerBlock, DoubleSupplier renderDiameter, double slack, int color, Supplier<ItemStack> coil) {
        this(uniqueName, category, transferRate, maxLength, lossPerBlock, renderDiameter, slack, color, coil, category == null ? 0 : category.getDefaultShock());
    }

    public ICWireType(String uniqueName, @Nullable ICWireCategory category, IntSupplier transferRate, int maxLength, double lossPerBlock, DoubleSupplier renderDiameter, double slack, int color, Supplier<ItemStack> coil, float shockDamage) {
        this.uniqueName = uniqueName;
        this.category = category;
        this.transferRate = transferRate;
        this.maxLength = maxLength;
        this.lossPerBlock = lossPerBlock;
        this.renderDiameter = renderDiameter;
        this.slack = slack;
        this.color = color;
        this.coil = coil;
        this.shockDamage = shockDamage;
    }

    @Nonnull public static ICWireType register(@Nonnull ICWireType type) {
        ICWireType known = REGISTRY.get(type.uniqueName);
        if (known != null) { return known; }
        ICWireCategory.checkOpen(type.uniqueName);
        return REGISTRY.computeIfAbsent(type.uniqueName, key -> type);
    }

    @Nonnull public static ICWireType registerDefault(@Nonnull ICWireType type) {
        ICWireType registered = register(type);
        if (registered.category != null) { registered.category.setDefaultType(registered); }
        return registered;
    }

    public static void freeze() { ICWireCategory.freeze(); }

    public static boolean frozen() { return ICWireCategory.frozen(); }

    @Nullable public static ICWireType byName(@Nullable String name) { return name == null ? null : REGISTRY.get(name); }

    @Nonnull public static Collection<ICWireType> values() { return Collections.unmodifiableCollection(REGISTRY.values()); }

    @Nullable public static ICWireType readFromNBT(@Nonnull CompoundTag nbt, @Nonnull String key) { return nbt.contains(key) ? byName(nbt.getString(key)) : null; }

    public void writeToNBT(@Nonnull CompoundTag nbt, @Nonnull String key) { nbt.putString(key, uniqueName); }

    public static boolean canMix(@Nullable ICWireType first, @Nullable ICWireType second) {
        if (first == null || second == null) { return false; }
        return first.category != null && first.category == second.category;
    }

    public String getUniqueName() { return uniqueName; }

    @Nullable public ICWireCategory getCategory() { return category; }

    @Nonnull public ICWireRole getRole() {
        if (category != null) { return category.getRole(); }
        return getTransferRate() > 0 ? ICWireRole.ENERGY : ICWireRole.STRUCTURE;
    }

    public double getRenderDiameter() { return renderDiameter.getAsDouble(); }

    public double getRenderRadius() { return getRenderDiameter() / 2; }

    public int getTransferRate() { return transferRate.getAsInt(); }

    public int getMaxLength() { return maxLength; }

    public double getLossPerBlock() { return lossPerBlock; }

    public double getSlack() { return slack; }

    public int getColor() { return color; }

    public int colorFor(@Nonnull ICWire wire) { return color; }

    public float getShockDamage() { return shockDamage; }

    @Nonnull public ItemStack getWireCoil() { return coil.get(); }

    @Override public String toString() { return uniqueName; }
}
