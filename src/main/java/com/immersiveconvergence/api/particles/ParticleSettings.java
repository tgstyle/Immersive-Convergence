package com.immersiveconvergence.api.particles;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ParticleSettings {
    public static BooleanSupplier particleCollide = () -> false;
    public static DoubleSupplier coloredSmokeHeight = () -> 3.0D;
    public static DoubleSupplier customSmokeHeight = () -> 3.0D;
}
