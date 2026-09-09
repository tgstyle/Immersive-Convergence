package com.immersiveconvergence.common.multiblock;

import com.immersiveconvergence.api.multiblock.ICMaterial;
import com.immersiveconvergence.api.multiblock.ICMultiblock;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.common.util.advancements.IEAdvancements;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class IEMultiblockBridge {
    private static final Map<ICMultiblock, MultiblockHandler.IMultiblock> ADAPTERS = new ConcurrentHashMap<>();

    private IEMultiblockBridge() {}

    public static void register(ICMultiblock multiblock) { MultiblockHandler.registerMultiblock(adapt(multiblock)); }

    public static MultiblockHandler.IMultiblock adapt(ICMultiblock multiblock) { return ADAPTERS.computeIfAbsent(multiblock, Adapter::new); }

    public static boolean exists(String uniqueName) {
        for (MultiblockHandler.IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (multiblock.getUniqueName().equalsIgnoreCase(uniqueName)) { return true; }
        }
        return false;
    }

    public static boolean formFirstMatching(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack, Predicate<String> allowed) {
        for (MultiblockHandler.IMultiblock multiblock : MultiblockHandler.getMultiblocks()) {
            if (!multiblock.isBlockTrigger(world.getBlockState(pos))) { continue; }
            if (!allowed.test(multiblock.getUniqueName())) { continue; }
            if (MultiblockHandler.fireMultiblockFormationEventPre(player, multiblock, pos, stack).isCanceled()) { continue; }
            if (multiblock.createStructure(world, pos, side, player)) {
                if (player instanceof EntityPlayerMP) { IEAdvancements.TRIGGER_MULTIBLOCK.trigger((EntityPlayerMP)player, multiblock, stack); }
                return true;
            }
        }
        return false;
    }

    public static boolean formationCancelled(EntityPlayer player, ICMultiblock multiblock, BlockPos pos, ItemStack hammer) {
        return MultiblockHandler.fireMultiblockFormationEventPre(player, adapt(multiblock), pos, hammer).isCanceled();
    }

    public static void formationDone(EntityPlayer player, ICMultiblock multiblock, BlockPos pos, ItemStack hammer) {
        MultiblockHandler.fireMultiblockFormationEventPost(player, adapt(multiblock), pos, hammer);
    }

    public static IngredientStack[] toIngredients(List<ICMaterial> materials) {
        List<IngredientStack> ingredients = new ArrayList<>();
        for (ICMaterial material : materials) {
            String oreName = material.oreName();
            if (oreName != null) { ingredients.add(new IngredientStack(oreName, material.count())); }
            else {
                ItemStack stack = material.stack().copy();
                stack.setCount(material.count());
                ingredients.add(new IngredientStack(stack));
            }
        }
        return ingredients.toArray(new IngredientStack[0]);
    }

    private static final class Adapter implements MultiblockHandler.IMultiblock {
        private final ICMultiblock multiblock;

        private Adapter(ICMultiblock multiblock) { this.multiblock = multiblock; }

        @Override public String getUniqueName() { return multiblock.getUniqueName(); }

        @Override public boolean isBlockTrigger(IBlockState state) { return multiblock.isBlockTrigger(state); }

        @Override public boolean createStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player) { return multiblock.createStructure(world, pos, side, player); }

        @Override public ItemStack[][][] getStructureManual() { return multiblock.getStructureManual(); }

        @Override public IBlockState getBlockstateFromStack(int index, ItemStack stack) { return multiblock.getBlockstateFromStack(index, stack); }

        @Override public IngredientStack[] getTotalMaterials() { return toIngredients(multiblock.getTotalMaterialList()); }

        @Override public float getManualScale() { return multiblock.getManualScale(); }

        @Override @SideOnly(Side.CLIENT) public boolean overwriteBlockRender(ItemStack stack, int iterator) { return multiblock.overwriteBlockRender(stack, iterator); }

        @Override @SideOnly(Side.CLIENT) public boolean canRenderFormedStructure() { return multiblock.canRenderFormedStructure(); }

        @Override @SideOnly(Side.CLIENT) public void renderFormedStructure() { multiblock.renderFormedStructure(); }
    }
}
