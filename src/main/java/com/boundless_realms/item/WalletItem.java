package com.boundless_realms.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
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
                textConsumer.accept(Text.translatable("tooltip.boundless_realms.wallet.money", totalMoney).formatted(Formatting.GOLD));
            } else {
                textConsumer.accept(Text.translatable("tooltip.boundless_realms.wallet.empty").formatted(Formatting.GRAY));
            }
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            ItemStack initialStack = user.getStackInHand(hand);

            SimpleInventory inventory = new SimpleInventory(27) {
                @Override
                public void markDirty() {
                    super.markDirty();
                    updateWalletComponent(user, this);
                }
            };

            ContainerComponent currentContents = initialStack.get(DataComponentTypes.CONTAINER);
            if (currentContents != null) {
                currentContents.copyTo(inventory.getHeldStacks());
            }

            user.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInv, player) -> {
                return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInv, inventory, 3) {
                    {
                        for (int i = 0; i < 27; i++) {
                            Slot oldSlot = this.slots.get(i);
                            this.slots.set(i, new WalletSlot(inventory, i, oldSlot.x, oldSlot.y));
                        }
                    }

                    @Override
                    public ItemStack quickMove(PlayerEntity player, int index) {
                        Slot slot = this.slots.get(index);
                        if (slot == null || !slot.hasStack()) return ItemStack.EMPTY;

                        ItemStack stack = slot.getStack();
                        ItemStack originalStack = stack.copy();

                        if (index < 27) {
                            if (!this.insertItem(stack, 27, this.slots.size(), true)) return ItemStack.EMPTY;
                        } else {
                            if (!this.slots.getFirst().canInsert(stack) || !this.insertItem(stack, 0, 27, false)) {
                                return ItemStack.EMPTY;
                            }
                        }

                        if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
                        else slot.markDirty();

                        return originalStack;
                    }

                    @Override
                    public boolean canUse(PlayerEntity player) {
                        return !findWalletStack(player).isEmpty();
                    }
                };
            }, TITLE));
        }
        return ActionResult.SUCCESS;
    }

    private static ItemStack findWalletStack(PlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof WalletItem) return player.getMainHandStack();
        if (player.getOffHandStack().getItem() instanceof WalletItem) return player.getOffHandStack();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof WalletItem) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static void updateWalletComponent(PlayerEntity player, SimpleInventory inventory) {
        ItemStack wallet = findWalletStack(player);
        if (!wallet.isEmpty()) {
            wallet.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory.getHeldStacks()));
        }
    }

    private static class WalletSlot extends Slot {
        public WalletSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(ModItems.MONEY)
                    && !(stack.getItem() instanceof WalletItem)
                    && stack.getItem().canBeNested();
        }
    }
}