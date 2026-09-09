package com.immersiveconvergence.common.util.compat.groovyscript;

import com.immersiveconvergence.api.ICMods;

import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class ICGroovyProperties extends GroovyPropertyContainer {
    public final INamed reservoir = ICMods.immersivePetroleum() ? new ICReservoirRegistry() : null;
}
