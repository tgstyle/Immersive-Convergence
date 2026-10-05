package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.api.tool.assembler.RecipeQuery;
import blusunrize.immersiveengineering.common.blocks.metal.CrafterPatternInventory;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AssemblerLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AssemblerLogic.State;
import blusunrize.immersiveengineering.common.register.IEMultiblockLogic;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.IndexArgument;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.AssemblerCallbacks;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") public class ICAssemblerCallbacks extends AssemblerCallbacks {
    @ComputerCallable public boolean hasIngredients(CallbackEnvironment<State> env, @IndexArgument int recipe) {
        CrafterPatternInventory pattern = pattern(env.object(), recipe);
        if (pattern.inv.get(9).isEmpty()) { throw new IllegalArgumentException("The requested recipe is invalid"); }
        List<RecipeQuery> queries = pattern.getQueries(env.level());
        if (queries == null) { throw new IllegalArgumentException("The Assembler cannot craft this recipe"); }
        ArrayList<ItemStack> available = new ArrayList<>();
        for (ItemStack stack : env.object().inventory) {
            if (!stack.isEmpty()) { available.add(stack.copy()); }
        }
        return ((AssemblerLogic) IEMultiblockLogic.ASSEMBLER.logic()).consumeIngredients(env.object(), queries, available, false, null);
    }

    @ComputerCallable public Map<String, Object> getRecipe(CallbackEnvironment<State> env, @IndexArgument int recipe) {
        CrafterPatternInventory pattern = pattern(env.object(), recipe);
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < 9; i++) { result.put("in" + (i + 1), ComputerValues.item(pattern.inv.get(i))); }
        result.put("out", ComputerValues.item(pattern.inv.get(9)));
        return result;
    }

    private static CrafterPatternInventory pattern(State state, int recipe) {
        if (recipe < 0 || recipe >= state.patterns.length) { throw new IllegalArgumentException("Only recipes 1-3 are available"); }
        return state.patterns[recipe];
    }
}
