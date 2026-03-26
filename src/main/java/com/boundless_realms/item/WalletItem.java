package com.boundless_realms.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public class WalletItem extends Item {
    private static final Component TITLE = Component.translatable("item.boundless_realms.wallet");

    public WalletItem(Properties settings) {
        super(settings.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        ItemContainerContents container = stack.get(DataComponents.CONTAINER);
        if (container != null) {
            int totalMoney = (int) container.stream()
                    .filter(is -> is.is(ModItems.MONEY))
                    .mapToLong(ItemStack::getCount)
                    .sum();

            if (totalMoney > 0) {
                textConsumer.accept(Component.translatable("tooltip.boundless_realms.wallet.money", totalMoney).withStyle(ChatFormatting.GOLD));
            } else {
                textConsumer.accept(Component.translatable("tooltip.boundless_realms.wallet.empty").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide()) {
            ItemStack initialStack = user.getItemInHand(hand);

            SimpleContainer inventory = new SimpleContainer(27) {
                @Override
                public void setChanged() {
                    super.setChanged();
                    updateWalletComponent(user, this);
                }
            };

            ItemContainerContents currentContents = initialStack.get(DataComponents.CONTAINER);
            if (currentContents != null) {
                currentContents.copyInto(inventory.getItems());
            }

            user.openMenu(new SimpleMenuProvider((syncId, playerInv, player) -> {
                return new ChestMenu(MenuType.GENERIC_9x3, syncId, playerInv, inventory, 3) {
                    {
                        for (int i = 0; i < 27; i++) {
                            Slot oldSlot = this.slots.get(i);
                            this.slots.set(i, new WalletSlot(inventory, i, oldSlot.x, oldSlot.y));
                        }
                    }

                    @Override
                    public ItemStack quickMoveStack(Player player, int index) {
                        Slot slot = this.slots.get(index);
                        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

                        ItemStack stack = slot.getItem();
                        ItemStack originalStack = stack.copy();

                        if (index < 27) {
                            if (!this.moveItemStackTo(stack, 27, this.slots.size(), true)) return ItemStack.EMPTY;
                        } else {
                            if (!this.moveItemStackTo(stack, 0, 27, false)) {
                                return ItemStack.EMPTY;
                            }
                        }

                        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
                        else slot.setChanged();

                        return originalStack;
                    }

                    @Override
                    public boolean stillValid(Player player) {
                        return !findWalletStack(player).isEmpty();
                    }
                };
            }, TITLE));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    private static ItemStack findWalletStack(Player player) {
        if (player.getMainHandItem().getItem() instanceof WalletItem) return player.getMainHandItem();
        if (player.getOffhandItem().getItem() instanceof WalletItem) return player.getOffhandItem();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof WalletItem) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static void updateWalletComponent(Player player, SimpleContainer inventory) {
        ItemStack wallet = findWalletStack(player);
        if (!wallet.isEmpty()) {
            wallet.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory.getItems()));
        }
    }

    private static class WalletSlot extends Slot {
        public WalletSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ModItems.MONEY) && stack.getItem().canFitInsideContainerItems();
        }
    }
}
