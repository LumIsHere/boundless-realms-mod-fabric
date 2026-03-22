package com.boundless_realms.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WalletItem extends Item {
    private static final Text TITLE = Text.translatable("item.boundless_realms.wallet");

    public WalletItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack walletStack = user.getStackInHand(hand);

        if (!world.isClient()) {
            SimpleInventory inventory = new SimpleInventory(54);
            ContainerComponent currentContents = walletStack.get(DataComponentTypes.CONTAINER);
            if (currentContents != null) {
                currentContents.copyTo(inventory.getHeldStacks());
            }

            user.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInv, player) -> {
                return new GenericContainerScreenHandler(net.minecraft.screen.ScreenHandlerType.GENERIC_9X6, syncId, playerInv, inventory, 6) {

                    @Override
                    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
                        // 1. Safety check: ignore out-of-bounds clicks (like clicking outside the window)
                        if (slotIndex < 0) {
                            super.onSlotClick(slotIndex, button, actionType, player);
                            return;
                        }

                        ItemStack clickedStack = this.getSlot(slotIndex).getStack();
                        ItemStack cursorStack = this.getCursorStack();

                        if (slotIndex >= 54) {
                            boolean isMoneyInSlot = clickedStack.isOf(ModItems.MONEY);
                            boolean isMoneyInHand = cursorStack.isOf(ModItems.MONEY);

                            // If I'm not touching money, and the slot doesn't have money, cancel.
                            if (!isMoneyInSlot && !isMoneyInHand && !clickedStack.isEmpty()) {
                                return; // This "mutes" the click entirely
                            }
                        }

                        // 3. Prevent clicking the Wallet itself (prevents "Wallet-ception")
                        if (clickedStack == walletStack) return;

                        super.onSlotClick(slotIndex, button, actionType, player);
                    }

                    @Override
                    public void onClosed(PlayerEntity player) {
                        super.onClosed(player);
                        walletStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory.getHeldStacks()));
                    }
                };
            }, TITLE));
        }
        return ActionResult.SUCCESS;
    }
}
