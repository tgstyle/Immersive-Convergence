package com.immersiveconvergence.api.client.gui;

import blusunrize.immersiveengineering.api.client.TextUtils;
import blusunrize.immersiveengineering.api.utils.ResettableLazy;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class BaseContainerScreen<C extends AbstractContainerMenu> extends AbstractContainerScreen<C> {
    private final ResettableLazy<List<GuiInfoArea>> infoAreas;
    protected final ResourceLocation background;

    public BaseContainerScreen(C inventorySlotsIn, Inventory inv, Component title, ResourceLocation background) {
        super(inventorySlotsIn, inv, title);
        this.background = background;
        this.infoAreas = new ResettableLazy<>(this::makeInfoAreas);
    }

    @Override protected void init() {
        super.init();
        this.infoAreas.reset();
        this.inventoryLabelY = this.imageHeight - 91;
    }

    @Nonnull protected List<GuiInfoArea> makeInfoAreas() { return ImmutableList.of(); }

    @Override protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -557004, true);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -557004, true);
    }

    @Override public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        List<Component> tooltip = new ArrayList<>();
        for (GuiInfoArea area : this.infoAreas.get()) { area.fillTooltip(mouseX, mouseY, tooltip); }
        for (GuiEventListener w : this.children()) {
            if (w.isMouseOver(mouseX, mouseY) && w instanceof IGuiTooltipWidget ttw) { ttw.gatherTooltip(mouseX, mouseY, tooltip); }
        }
        Objects.requireNonNull(tooltip);
        this.gatherAdditionalTooltips(mouseX, mouseY, tooltip::add, (t) -> tooltip.add(TextUtils.applyFormat(t, ChatFormatting.GRAY)));
        if (!tooltip.isEmpty()) { graphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY); }
        else { this.renderTooltip(graphics, mouseX, mouseY); }
    }

    @Override protected final void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        this.drawBackgroundTexture(graphics);
        this.drawContainerBackgroundPre(graphics, partialTicks, x, y);
        for (GuiInfoArea area : this.infoAreas.get()) { area.draw(graphics); }
    }

    protected void drawBackgroundTexture(GuiGraphics graphics) { graphics.blit(this.background, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight); }

    protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {}

    protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray) {}

    public int getLeftPos() { return leftPos; }

    public int getTopPos() { return topPos; }
}
