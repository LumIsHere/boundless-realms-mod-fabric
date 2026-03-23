package com.boundless_realms.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class WalletItem extends Item {
    private static final Text TITLE = Text.translatable("item.boundless_realms.wallet");

    public WalletItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);

        if (container != null) {
            int totalMoney = (int) container.stream()
                    .filter(is -> is.isOf(ModItems.MONEY))
                    .mapToLong(ItemStack::getCount)
                    .sum();

            if (totalMoney > 0) {
                textConsumer.accept(
                        Text.translatable("tooltip.boundless_realms.wallet.money", totalMoney)
                                .formatted(Formatting.GOLD)
                );
            } else {
                textConsumer.accept(
                        Text.translatable("tooltip.boundless_realms.wallet.empty")
                                .formatted(Formatting.GRAY)
                );
            }
        }
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
                final int walletInventorySize = inventory.size();

                return new GenericContainerScreenHandler(net.minecraft.screen.ScreenHandlerType.GENERIC_9X6, syncId, playerInv, inventory, 6) {
                    {
                        for (int i = 0; i < walletInventorySize; i++) {
                            Slot originalSlot = this.slots.get(i);
                            Slot walletSlot = new Slot(originalSlot.inventory, originalSlot.getIndex(), originalSlot.x, originalSlot.y) {
                                @Override
                                public boolean canInsert(ItemStack stack) {
                                    return stack.isOf(ModItems.MONEY);
                                }
                            };
                            walletSlot.id = originalSlot.id;
                            this.slots.set(i, walletSlot);
                        }
                    }

                    private boolean isWalletStorageSlot(int slotIndex) {
                        return slotIndex >= 0 && slotIndex < walletInventorySize;
                    }

                    private boolean isOpenWalletStack(ItemStack stack) {
                        return stack == walletStack;
                    }

                    private boolean isOpenWalletSlot(int slotIndex) {
                        return slotIndex >= walletInventorySize
                                && slotIndex < this.slots.size()
                                && this.getSlot(slotIndex).getStack() == walletStack;
                    }

                    @Override
                    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
                        if (slotIndex < 0) {
                            super.onSlotClick(slotIndex, button, actionType, player);
                            return;
                        }

                        if (actionType == SlotActionType.SWAP && button >= 0 && button < 9 && this.isWalletStorageSlot(slotIndex)) {
                            ItemStack hotbarStack = player.getInventory().getStack(button);
                            if (this.isOpenWalletStack(hotbarStack) || hotbarStack.getItem() instanceof WalletItem) {
                                return;
                            }
                        }

                        if (actionType == SlotActionType.QUICK_MOVE && this.isOpenWalletSlot(slotIndex)) {
                            return;
                        }

                        super.onSlotClick(slotIndex, button, actionType, player);
                    }

                    @Override
                    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
                        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
                            return ItemStack.EMPTY;
                        }

                        Slot sourceSlot = this.getSlot(slotIndex);
                        if (sourceSlot == null || !sourceSlot.hasStack()) {
                            return ItemStack.EMPTY;
                        }

                        ItemStack stack = sourceSlot.getStack();
                        if (stack.isEmpty()) {
                            return ItemStack.EMPTY;
                        }

                        if (this.isOpenWalletStack(stack)) {
                            return ItemStack.EMPTY;
                        }

                        ItemStack originalStack = stack.copy();
                        int playerMainStart = walletInventorySize;
                        int hotbarStart = playerMainStart + 27;
                        int playerEnd = hotbarStart + 9;

                        if (this.isWalletStorageSlot(slotIndex)) {
                            if (!this.insertItem(stack, playerMainStart, playerEnd, true)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (stack.isOf(ModItems.MONEY)) {
                            if (!this.insertItem(stack, 0, walletInventorySize, false)) {
                                if (slotIndex < hotbarStart) {
                                    if (!this.insertItem(stack, hotbarStart, playerEnd, false)) {
                                        return ItemStack.EMPTY;
                                    }
                                } else if (slotIndex < playerEnd) {
                                    if (!this.insertItem(stack, playerMainStart, hotbarStart, false)) {
                                        return ItemStack.EMPTY;
                                    }
                                } else {
                                    return ItemStack.EMPTY;
                                }
                            }
                        } else if (slotIndex < hotbarStart) {
                            if (!this.insertItem(stack, hotbarStart, playerEnd, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (slotIndex < playerEnd) {
                            if (!this.insertItem(stack, playerMainStart, hotbarStart, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            return ItemStack.EMPTY;
                        }

                        if (stack.isEmpty()) {
                            sourceSlot.setStack(ItemStack.EMPTY);
                        } else {
                            sourceSlot.markDirty();
                        }

                        return originalStack;
                    }

                    @Override
                    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
                        if (slot == null) {
                            return false;
                        }

                        if (this.isOpenWalletStack(stack)) {
                            return false;
                        }

                        if (slot.id < walletInventorySize) {
                            return stack.isOf(ModItems.MONEY);
                        }

                        return super.canInsertIntoSlot(stack, slot);
                    }

                    @Override
                    public boolean canInsertIntoSlot(Slot slot) {
                        return slot != null && super.canInsertIntoSlot(slot);
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
