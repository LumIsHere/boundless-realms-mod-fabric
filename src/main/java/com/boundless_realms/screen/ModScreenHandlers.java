package com.boundless_realms.screen;

import com.boundless_realms.BoundlessRealmsMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ExtendedScreenHandlerType<BitcoinMinerScreenHandler, BlockPos> BITCOIN_MINER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "bitcoin_miner"),
            new ExtendedScreenHandlerType<>(BitcoinMinerScreenHandler::new, BlockPos.PACKET_CODEC)
    );

    public static void registerScreenHandlers() {
        BoundlessRealmsMod.LOGGER.info("Registering screen handlers for " + BoundlessRealmsMod.MOD_ID);
    }
}
