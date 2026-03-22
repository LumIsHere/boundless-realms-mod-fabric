package com.boundless_realms.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.item.ItemStack;
import com.boundless_realms.item.ModItems;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class PortableCraftingTableScreenHandler extends CraftingScreenHandler {
    private final ScreenHandlerContext context;

    public PortableCraftingTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        // We pass the context here so the handler knows the world/position
        super(syncId, playerInventory, context);
        this.context = context;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        // We check both hands to see if the player is still holding the item
        return player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.PORTABLE_CRAFTING_TABLE) ||
                player.getStackInHand(Hand.OFF_HAND).isOf(ModItems.PORTABLE_CRAFTING_TABLE);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // Safety check to prevent the player from moving the portable table itself
        if (slotIndex >= 0 && slotIndex < this.slots.size()) {
            ItemStack stack = this.slots.get(slotIndex).getStack();
            if (stack.isOf(ModItems.PORTABLE_CRAFTING_TABLE)) {
                return;
            }
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        // Prevent shift-clicking the portable table into the grid
        if (slotIndex >= 0 && slotIndex < this.slots.size()) {
            ItemStack stack = this.slots.get(slotIndex).getStack();
            if (stack.isOf(ModItems.PORTABLE_CRAFTING_TABLE)) {
                return ItemStack.EMPTY;
            }
        }
        return super.quickMove(player, slotIndex);
    }
}