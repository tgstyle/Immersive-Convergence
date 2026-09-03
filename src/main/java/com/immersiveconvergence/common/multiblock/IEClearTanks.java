package com.immersiveconvergence.common.multiblock;

import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToIntFunction;

public final class IEClearTanks {
    private static final String KEY_INPUT_TANK_CLEARED = "gui.immersiveconvergence.input_tank_cleared";
    private static final String KEY_INPUT_TANKS_CLEARED = "gui.immersiveconvergence.input_tanks_cleared";
    private static final Map<String, Entry> ENTRIES = new HashMap<>();

    private IEClearTanks() {}

    public static void register(String uniqueName, int[] inputPositions, ToIntFunction<TileEntity> clearer) { ENTRIES.put(uniqueName, new Entry(inputPositions, clearer)); }

    public static boolean handle(World world, BlockPos pos, EntityPlayer player, EnumHand hand) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityMultiblockPart) || !player.isSneaking() || !Utils.isHammer(player.getHeldItem(hand))) { return false; }
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)tile;
        if (!part.formed || part.pos < 0) { return false; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        Entry entry = template == null ? null : ENTRIES.get(template.getUniqueName());
        if (entry == null || Arrays.binarySearch(entry.inputPositions, template.portPos(part.pos)) < 0) { return false; }
        TileEntity master = part.master();
        if (master == null) { return false; }
        if (!world.isRemote) {
            int cleared = entry.clearer.applyAsInt(master);
            master.markDirty();
            if (master instanceof TileEntityIEBase) { ((TileEntityIEBase)master).markContainingBlockForUpdate(null); }
            player.sendStatusMessage(new TextComponentTranslation(cleared > 1 ? KEY_INPUT_TANKS_CLEARED : KEY_INPUT_TANK_CLEARED), true);
        }
        return true;
    }

    private static final class Entry {
        private final int[] inputPositions;
        private final ToIntFunction<TileEntity> clearer;

        private Entry(int[] inputPositions, ToIntFunction<TileEntity> clearer) {
            this.inputPositions = inputPositions.clone();
            Arrays.sort(this.inputPositions);
            this.clearer = clearer;
        }
    }
}
