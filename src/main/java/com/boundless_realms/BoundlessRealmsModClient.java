package com.boundless_realms;

import com.boundless_realms.entity.ModEntities;
import com.boundless_realms.screen.BitcoinMinerScreen;
import com.boundless_realms.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;

public class BoundlessRealmsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                ModEntities.LUNCH_TICKET,
                ThrownItemRenderer::new
        );

        EntityRendererRegistry.register(
                ModEntities.TICKET_INSPECTOR,
                VillagerRenderer::new
        );

        MenuScreens.register(
                ModScreenHandlers.BITCOIN_MINER,
                BitcoinMinerScreen::new
        );
    }
}
