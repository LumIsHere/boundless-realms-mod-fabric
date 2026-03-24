package com.boundless_realms.screen;

import com.boundless_realms.block.ModBlocks;
import com.boundless_realms.block.entity.BitcoinMinerBlockEntity;
import com.boundless_realms.item.GraphicsCardItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class BitcoinMinerScreenHandler extends ScreenHandler {
    private static final int INPUT_START = 0;
    private static final int INPUT_END = BitcoinMinerBlockEntity.INPUT_SLOT_COUNT;
    private static final int OUTPUT_SLOT = BitcoinMinerBlockEntity.OUTPUT_SLOT;
    private static final int BLOCK_SLOT_COUNT = BitcoinMinerBlockEntity.TOTAL_SLOTS;
    private static final int PLAYER_INVENTORY_START = BLOCK_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Inventory inventory;
    private final ScreenHandlerContext context;

    public BitcoinMinerScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, new SimpleInventory(BitcoinMinerBlockEntity.TOTAL_SLOTS), ScreenHandlerContext.EMPTY);
    }

    public BitcoinMinerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        this(syncId, playerInventory, inventory, ScreenHandlerContext.EMPTY);
    }

    public BitcoinMinerScreenHandler(int syncId, PlayerInventory playerInventory, BitcoinMinerBlockEntity blockEntity) {
        this(syncId, playerInventory, blockEntity, ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos()));
    }

    private BitcoinMinerScreenHandler(
            int syncId,
            PlayerInventory playerInventory,
            Inventory inventory,
            ScreenHandlerContext context
    ) {
        super(ModScreenHandlers.BITCOIN_MINER, syncId);
        checkSize(inventory, BitcoinMinerBlockEntity.TOTAL_SLOTS);

        this.inventory = inventory;
        this.context = context;
        inventory.onOpen(playerInventory.player);

        int slotIndex = 0;

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 5; column++) {
                int x = 26 + (column * 18);
                int y = 18 + (row * 18);
                addSlot(new Slot(inventory, slotIndex++, x, y) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return stack.getItem() instanceof GraphicsCardItem;
                    }

                    @Override
                    public int getMaxItemCount() {
                        return 1;
                    }
                });
            }
        }

        addSlot(new Slot(inventory, OUTPUT_SLOT, 134, 27) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventorySlots(playerInventory, 8, 84);
        addPlayerHotbarSlots(playerInventory, 8, 142);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();

        if (slotIndex < BLOCK_SLOT_COUNT) {
            if (!insertItem(originalStack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (originalStack.getItem() instanceof GraphicsCardItem) {
            if (!insertItem(originalStack, INPUT_START, INPUT_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        slot.onTakeItem(player, originalStack);
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, ModBlocks.BITCOIN_MINER);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }
}
