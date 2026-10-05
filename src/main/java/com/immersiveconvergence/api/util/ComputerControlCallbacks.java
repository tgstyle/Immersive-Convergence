package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.utils.ComputerControlState;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;

@SuppressWarnings("unused") public class ComputerControlCallbacks extends Callback<RSState> {
    public static final ComputerControlCallbacks INSTANCE = new ComputerControlCallbacks();

    @ComputerCallable public void enableComputerControl(CallbackEnvironment<RSState> env, boolean enable) {
        ComputerControlState control = env.object().getComputerControlState();
        if (!enable) {
            control.clear();
            return;
        }
        if (!control.isAttached()) { control.addReference(); }
        control.setEnabled(true);
    }
}
