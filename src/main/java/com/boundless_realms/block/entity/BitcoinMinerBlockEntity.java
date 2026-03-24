package com.boundless_realms.block.entity;

import com.boundless_realms.block.ModBlockEntities;
import com.boundless_realms.item.GraphicsCardItem;
import com.boundless_realms.item.ModItems;
import com.boundless_realms.screen.BitcoinMinerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BitcoinMinerBlockEntity extends LockableContainerBlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {
    public static final int INPUT_SLOT_COUNT = 10;
    public static final int OUTPUT_SLOT = 10;
    public static final int TOTAL_SLOTS = 11;
    public static final int TICKS_PER_BITCOIN = 200;
    public static final int HASHRATE_PER_BITCOIN = 10000;

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private int miningTicks;

    public BitcoinMinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BITCOIN_MINER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BitcoinMinerBlockEntity blockEntity) {
        if (!blockEntity.hasRequiredHashrate()) {
            if (blockEntity.miningTicks != 0) {
                blockEntity.miningTicks = 0;
                markDirty(world, pos, state);
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
            markDirty(world, pos, state);
        }
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.boundless_realms.bitcoin_miner");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BitcoinMinerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot < INPUT_SLOT_COUNT && stack.getItem() instanceof GraphicsCardItem;
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, inventory);
        miningTicks = view.getInt("mining_ticks", 0);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, inventory);
        view.putInt("mining_ticks", miningTicks);
    }

    @Override
    public net.minecraft.nbt.NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    public net.minecraft.network.packet.Packet<net.minecraft.network.listener.ClientPlayPacketListener> toUpdatePacket() {
        return net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket.create(this);
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
                || (outputStack.isOf(ModItems.BITCOIN) && outputStack.getCount() < outputStack.getMaxCount());
    }

    private void outputBitcoin() {
        ItemStack outputStack = inventory.get(OUTPUT_SLOT);

        if (outputStack.isEmpty()) {
            inventory.set(OUTPUT_SLOT, new ItemStack(ModItems.BITCOIN));
        } else {
            outputStack.increment(1);
        }
    }
}
