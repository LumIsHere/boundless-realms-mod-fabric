package com.boundless_realms.screen;

import com.boundless_realms.block.ModBlocks;
import com.boundless_realms.block.entity.BitcoinMinerBlockEntity;
import com.boundless_realms.item.GraphicsCardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BitcoinMinerScreenHandler extends AbstractContainerMenu {
    private static final int INPUT_START = 0;
    private static final int INPUT_END = BitcoinMinerBlockEntity.INPUT_SLOT_COUNT;
    private static final int OUTPUT_SLOT = BitcoinMinerBlockEntity.OUTPUT_SLOT;
    private static final int BLOCK_SLOT_COUNT = BitcoinMinerBlockEntity.TOTAL_SLOTS;
    private static final int PLAYER_INVENTORY_START = BLOCK_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container inventory;
    private final ContainerLevelAccess context;

    public BitcoinMinerScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(BitcoinMinerBlockEntity.TOTAL_SLOTS), ContainerLevelAccess.NULL);
    }

    public BitcoinMinerScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
        this(syncId, playerInventory, inventory, ContainerLevelAccess.NULL);
    }

    public BitcoinMinerScreenHandler(int syncId, Inventory playerInventory, BitcoinMinerBlockEntity blockEntity) {
        this(syncId, playerInventory, blockEntity, ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()));
    }

    private BitcoinMinerScreenHandler(
            int syncId,
            Inventory playerInventory,
            Container inventory,
            ContainerLevelAccess context
    ) {
        super(ModScreenHandlers.BITCOIN_MINER, syncId);
        checkContainerSize(inventory, BitcoinMinerBlockEntity.TOTAL_SLOTS);

        this.inventory = inventory;
        this.context = context;
        inventory.startOpen(playerInventory.player);

        int slotIndex = 0;

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 5; column++) {
                int x = 26 + (column * 18);
                int y = 18 + (row * 18);
                addSlot(new Slot(inventory, slotIndex++, x, y) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.getItem() instanceof GraphicsCardItem;
                    }

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }
                });
            }
        }

        addSlot(new Slot(inventory, OUTPUT_SLOT, 134, 27) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addInventoryExtendedSlots(playerInventory, 8, 84);
        addInventoryHotbarSlots(playerInventory, 8, 142);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack originalStack = slot.getItem();
        newStack = originalStack.copy();

        if (slotIndex < BLOCK_SLOT_COUNT) {
            if (!moveItemStackTo(originalStack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (originalStack.getItem() instanceof GraphicsCardItem) {
            if (!moveItemStackTo(originalStack, INPUT_START, INPUT_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, originalStack);
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(context, player, ModBlocks.BITCOIN_MINER);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        inventory.stopOpen(player);
    }
}
