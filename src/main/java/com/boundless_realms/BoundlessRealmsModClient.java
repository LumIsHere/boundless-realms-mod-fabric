package com.boundless_realms;

import com.boundless_realms.entity.ModEntities;
import com.boundless_realms.screen.BitcoinMinerScreen;
import com.boundless_realms.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;

public class BoundlessRealmsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                ModEntities.LUNCH_TICKET,
                FlyingItemEntityRenderer::new
        );

        EntityRendererRegistry.register(
                ModEntities.TICKET_INSPECTOR,
                VillagerEntityRenderer::new
        );

        HandledScreens.register(
                ModScreenHandlers.BITCOIN_MINER,
                BitcoinMinerScreen::new
        );
    }
}
