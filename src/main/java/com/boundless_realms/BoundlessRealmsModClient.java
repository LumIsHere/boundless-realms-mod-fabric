package com.boundless_realms;

import com.boundless_realms.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;

public class BoundlessRealmsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                ModEntities.LUNCH_TICKET_PROJECTILE,
                FlyingItemEntityRenderer::new
        );

        EntityRendererRegistry.register(
                ModEntities.TICKET_INSPECTOR,
                VillagerEntityRenderer::new
        );
    }
}
