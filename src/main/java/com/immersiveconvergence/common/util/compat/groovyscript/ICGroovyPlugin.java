package com.immersiveconvergence.common.util.compat.groovyscript;

import com.immersiveconvergence.ImmersiveConvergence;

import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class ICGroovyPlugin implements GroovyPlugin {

    @Override @Nonnull public String getModId() { return ImmersiveConvergence.MODID; }

    @Override @Nonnull public String getContainerName() { return "Immersive Convergence"; }

    @Override public void onCompatLoaded(GroovyContainer<?> container) {}

    @Override public GroovyPropertyContainer createGroovyPropertyContainer() { return new ICGroovyProperties(); }
}
