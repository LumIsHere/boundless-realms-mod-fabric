package com.boundless_realms.money;

import com.boundless_realms.item.ModItems;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockMoneyDrops {
    private BlockMoneyDrops() {
    }

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) ->
                dropMoney(level, player, pos, state));
    }

    private static void dropMoney(Level level, Player player, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)
                || player.preventsBlockDrops()
                || player.isSpectator()
                || state.isAir()) {
            return;
        }

        BlockMoneyRarity rarity = BlockMoneyRarityResolver.resolve(state, serverLevel, pos);
        if (!rarity.shouldDropMoney(serverLevel.getRandom())) {
            return;
        }

        int amount = rarity.rollAmount(serverLevel.getRandom());
        Block.popResource(serverLevel, pos, new ItemStack(ModItems.MONEY, amount));
    }
}
