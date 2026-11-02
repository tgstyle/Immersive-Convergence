package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.common.blocks.metal.FloodlightBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.EventWaiterResult;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.FloodlightCallbacks;

public class ICFloodlightCallbacks extends FloodlightCallbacks {
    @ComputerCallable(isAsync = true) public EventWaiterResult waitForCooldown(CallbackEnvironment<FloodlightBlockEntity> env) { return waitUntilTurnable(env); }
}
