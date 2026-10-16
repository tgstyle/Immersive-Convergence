package com.immersiveconvergence.api.network;

import com.immersiveconvergence.api.manual.ICManual;
import com.immersiveconvergence.api.multiblock.TemplateMultiblock;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public record MessageMultiblockTemplates(Map<ResourceLocation, CompoundTag> templates) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageMultiblockTemplates> TYPE = new CustomPacketPayload.Type<>(ICLib.rl("multiblock_templates"));
    public static final StreamCodec<FriendlyByteBuf, MessageMultiblockTemplates> STREAM_CODEC = ByteBufCodecs.<FriendlyByteBuf, ResourceLocation, CompoundTag, Map<ResourceLocation, CompoundTag>>map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.COMPOUND_TAG).map(MessageMultiblockTemplates::new, MessageMultiblockTemplates::templates);

    @Override @Nonnull public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageMultiblockTemplates message, IPayloadContext context) {
        context.enqueueWork(() -> {
            for (Map.Entry<ResourceLocation, CompoundTag> entry : message.templates.entrySet()) {
                StructureTemplate template = new StructureTemplate();
                template.load(BuiltInRegistries.BLOCK.asLookup(), entry.getValue());
                TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.put(entry.getKey(), template);
            }
            ICManual.reset();
        });
    }
}
