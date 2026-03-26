package com.boundless_realms.block.entity;

import com.boundless_realms.block.ModBlockEntities;
import com.boundless_realms.item.GraphicsCardItem;
import com.boundless_realms.item.ModItems;
import com.boundless_realms.screen.BitcoinMinerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BitcoinMinerBlockEntity extends BaseContainerBlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {
    public static final int INPUT_SLOT_COUNT = 10;
    public static final int OUTPUT_SLOT = 10;
    public static final int TOTAL_SLOTS = 11;
    public static final int TICKS_PER_BITCOIN = 200;
    public static final int HASHRATE_PER_BITCOIN = 10000;

    private NonNullList<ItemStack> inventory = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private int miningTicks;

    public BitcoinMinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BITCOIN_MINER, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, BitcoinMinerBlockEntity blockEntity) {
        if (!blockEntity.hasRequiredHashrate()) {
            if (blockEntity.miningTicks != 0) {
                blockEntity.miningTicks = 0;
                setChanged(world, pos, state);
            }

            return;
        }

        if (!blockEntity.canOutputBitcoin()) {
            return;
        }

        blockEntity.miningTicks++;

        if (blockEntity.miningTicks >= TICKS_PER_BITCOIN) {
            blockEntity.miningTicks = 0;
            blockEntity.outputBitcoin();
            setChanged(world, pos, state);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.boundless_realms.bitcoin_miner");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new BitcoinMinerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return worldPosition;
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot < INPUT_SLOT_COUNT && stack.getItem() instanceof GraphicsCardItem;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        ContainerHelper.loadAllItems(view, inventory);
        miningTicks = view.getIntOr("mining_ticks", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, inventory);
        view.putInt("mining_ticks", miningTicks);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    private boolean hasRequiredHashrate() {
        return getInstalledHashrate() >= HASHRATE_PER_BITCOIN;
    }

    private int getInstalledHashrate() {
        int totalHashrate = 0;

        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            ItemStack stack = inventory.get(slot);

            if (stack.getItem() instanceof GraphicsCardItem graphicsCardItem) {
                totalHashrate += graphicsCardItem.getHashrate();
            }
        }

        return totalHashrate;
    }

    private boolean canOutputBitcoin() {
        ItemStack outputStack = inventory.get(OUTPUT_SLOT);

        return outputStack.isEmpty()
                || (outputStack.is(ModItems.BITCOIN) && outputStack.getCount() < outputStack.getMaxStackSize());
    }

    private void outputBitcoin() {
        ItemStack outputStack = inventory.get(OUTPUT_SLOT);

        if (outputStack.isEmpty()) {
            inventory.set(OUTPUT_SLOT, new ItemStack(ModItems.BITCOIN));
        } else {
            outputStack.grow(1);
        }
    }
}
