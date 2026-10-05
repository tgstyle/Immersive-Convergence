package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.MixerCallbacks;

@SuppressWarnings("unused") public class ICMixerCallbacks extends MixerCallbacks {
    @ComputerCallable public boolean isValidRecipe(CallbackEnvironment<State> env) { return ComputerValues.firstRecipe(env.object().processor.getQueue(), env.level()) != null; }
}
