package com.immersiveconvergence.api.network;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.manual.ICManual;
import com.immersiveconvergence.api.multiblock.TemplateMultiblock;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.network.NetworkEvent;
import java.util.Map;
import java.util.function.Supplier;

public final class MessageMultiblockTemplates implements INetworkMessage {
    private final Map<ResourceLocation, CompoundTag> templates;

    public MessageMultiblockTemplates(Map<ResourceLocation, CompoundTag> templates) { this.templates = templates; }

    public MessageMultiblockTemplates(FriendlyByteBuf buf) { this(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readNbt)); }

    @Override public void toBytes(FriendlyByteBuf buf) { buf.writeMap(templates, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeNbt); }

    @Override public void process(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isServer()) { return; }
        context.get().enqueueWork(() -> {
            Player player = ImmersiveConvergence.proxy.getClientPlayer();
            if (player == null) { return; }
            HolderGetter<Block> blocks = player.level().holderLookup(Registries.BLOCK);
            for (Map.Entry<ResourceLocation, CompoundTag> entry : templates.entrySet()) {
                StructureTemplate template = new StructureTemplate();
                template.load(blocks, entry.getValue());
                TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.put(entry.getKey(), template);
            }
            ICManual.reset();
        });
    }
}
