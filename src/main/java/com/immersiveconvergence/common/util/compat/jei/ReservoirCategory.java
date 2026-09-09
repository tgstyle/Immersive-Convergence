package com.immersiveconvergence.common.util.compat.jei;

import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.metal.BlockTypes_IPMetalMultiblock;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public class ReservoirCategory extends ICRecipeCategory<PumpjackHandler.ReservoirType, ReservoirWrapper> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation("immersiveconvergence", "textures/gui/pumpjack_jei.png");
    private final IDrawable tankOverlay;

    public ReservoirCategory(IGuiHelper helper) {
        super("reservoir", "tile.immersivepetroleum.metal_multiblock.pumpjack.name", helper.createDrawable(BACKGROUND, 0, 0, 159, 69), PumpjackHandler.ReservoirType.class,
                new ItemStack(IPContent.blockMetalMultiblock, 1, BlockTypes_IPMetalMultiblock.PUMPJACK_PARENT.getMeta()));
        tankOverlay = helper.drawableBuilder(BACKGROUND, 161, 2, 16, 47).addPadding(-2, 2, -2, 2).build();
    }

    @Override public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull ReservoirWrapper wrapper, @Nonnull IIngredients ingredients) {
        List<List<FluidStack>> outputs = ingredients.getOutputs(VanillaTypes.FLUID);
        if (outputs.isEmpty()) { return; }
        IGuiFluidStackGroup fluids = layout.getFluidStacks();
        fluids.addTooltipCallback(wrapper);
        fluids.init(0, false, 11, 11, 16, 47, Math.max(1, wrapper.reservoir.maxSize), false, tankOverlay);
        fluids.set(0, outputs.get(0));
        if (outputs.size() > 1) {
            fluids.init(1, false, 34, 11, 16, 47, Math.max(1, wrapper.pumpSpeed()), false, tankOverlay);
            fluids.set(1, outputs.get(1));
        }
    }

    @Override @Nonnull public IRecipeWrapper getRecipeWrapper(@Nonnull PumpjackHandler.ReservoirType reservoir) {
        int total = 0;
        for (Integer weight : PumpjackHandler.reservoirList.values()) { total += weight == null ? 0 : weight; }
        Integer weight = PumpjackHandler.reservoirList.get(reservoir);
        return new ReservoirWrapper(reservoir, weight == null ? 0 : weight, total);
    }
}
