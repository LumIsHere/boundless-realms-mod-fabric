package com.boundless_realms.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class BitcoinMinerScreen extends HandledScreen<BitcoinMinerScreenHandler> {
    public BitcoinMinerScreen(BitcoinMinerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 166;
        playerInventoryTitleY = 74;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = x;
        int top = y;

        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xFF1A1A1A);
        context.drawStrokedRectangle(left, top, backgroundWidth, backgroundHeight, 0xFF5E5E5E);

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

        context.drawTextWithShadow(textRenderer, Text.literal("GPU Inputs"), left + 8, top + 6, 0xFFE0E0E0);
        context.drawTextWithShadow(textRenderer, Text.literal("Output"), left + 129, top + 6, 0xFFE0E0E0);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, titleX, titleY, 0xFFFFFFFF, false);
        context.drawText(textRenderer, playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0xFFFFFFFF, false);
    }

    private void drawSlotBox(DrawContext context, int x, int y) {
        context.fill(x, y, x + 18, y + 18, 0xFF2C2C2C);
        context.drawStrokedRectangle(x, y, 18, 18, 0xFF8A8A8A);
    }
}
