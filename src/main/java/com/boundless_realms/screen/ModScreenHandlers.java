package com.boundless_realms.screen;

import com.boundless_realms.BoundlessRealmsMod;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModScreenHandlers {
    public static final ExtendedMenuType<BitcoinMinerScreenHandler, BlockPos> BITCOIN_MINER = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "bitcoin_miner"),
            new ExtendedMenuType<>(BitcoinMinerScreenHandler::new, BlockPos.STREAM_CODEC)
    );

    public static void registerScreenHandlers() {
        BoundlessRealmsMod.LOGGER.info("Registering screen handlers for " + BoundlessRealmsMod.MOD_ID);
    }
}
