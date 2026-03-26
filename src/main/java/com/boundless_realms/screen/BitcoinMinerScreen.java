package com.boundless_realms.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BitcoinMinerScreen extends AbstractContainerScreen<BitcoinMinerScreenHandler> {
    public BitcoinMinerScreen(BitcoinMinerScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = 74;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;

        context.fill(left, top, left + imageWidth, top + imageHeight, 0xFF1A1A1A);
        context.renderOutline(left, top, imageWidth, imageHeight, 0xFF5E5E5E);

        drawSlotBox(context, left + 25, top + 17);
        drawSlotBox(context, left + 43, top + 17);
        drawSlotBox(context, left + 61, top + 17);
        drawSlotBox(context, left + 79, top + 17);
        drawSlotBox(context, left + 97, top + 17);
        drawSlotBox(context, left + 25, top + 35);
        drawSlotBox(context, left + 43, top + 35);
        drawSlotBox(context, left + 61, top + 35);
        drawSlotBox(context, left + 79, top + 35);
        drawSlotBox(context, left + 97, top + 35);
        drawSlotBox(context, left + 133, top + 26);

        context.drawString(font, Component.literal("GPU Inputs"), left + 8, top + 6, 0xFFE0E0E0);
        context.drawString(font, Component.literal("Output"), left + 129, top + 6, 0xFFE0E0E0);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        context.drawString(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF, false);
        context.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFFFFFFF, false);
    }

    private void drawSlotBox(GuiGraphics context, int x, int y) {
        context.fill(x, y, x + 18, y + 18, 0xFF2C2C2C);
        context.renderOutline(x, y, 18, 18, 0xFF8A8A8A);
    }
}
