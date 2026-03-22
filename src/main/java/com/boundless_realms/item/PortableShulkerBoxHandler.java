package com.boundless_realms.item;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public final class PortableShulkerBoxHandler {
    private PortableShulkerBoxHandler() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> tryOpen(player, world, hand));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> tryOpen(player, world, hand));
    }

    private static ActionResult tryOpen(PlayerEntity player, World world, Hand hand) {
        ItemStack shulkerStack = player.getStackInHand(hand);
        if (!isPortableShulkerBox(shulkerStack)) {
            return ActionResult.PASS;
        }

        if (!world.isClient()) {
            SimpleInventory inventory = new SimpleInventory(27);
            ContainerComponent currentContents = shulkerStack.get(DataComponentTypes.CONTAINER);
            if (currentContents != null) {
                currentContents.copyTo(inventory.getHeldStacks());
            }

            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, screenPlayer) ->
                    new ShulkerBoxScreenHandler(syncId, playerInventory, inventory) {
                        @Override
                        public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity clickingPlayer) {
                            if (slotIndex >= 0 && this.getSlot(slotIndex).getStack() == shulkerStack) {
                                return;
                            }

                            super.onSlotClick(slotIndex, button, actionType, clickingPlayer);
                        }

                        @Override
                        public void onClosed(PlayerEntity closingPlayer) {
                            super.onClosed(closingPlayer);
                            shulkerStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory.getHeldStacks()));
                        }
                    }, shulkerStack.getName()));

            player.incrementStat(Stats.USED.getOrCreateStat(shulkerStack.getItem()));
        }

        return ActionResult.SUCCESS;
    }

    private static boolean isPortableShulkerBox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }
}
