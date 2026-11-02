package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.common.ICContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;

public class ICCreativeTab {
    public static CreativeTabs tab;

    public static void register() {
        tab = new CreativeTabs(ImmersiveConvergence.MODID) {
            @Override @Nonnull public ItemStack createIcon() { return new ItemStack(ICContent.blockDevice, 1, 0); }
        };
    }
}
