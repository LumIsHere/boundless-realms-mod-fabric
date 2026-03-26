package com.boundless_realms.block;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.block.entity.BitcoinMinerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final BlockEntityType<BitcoinMinerBlockEntity> BITCOIN_MINER = register(
            "bitcoin_miner",
            BitcoinMinerBlockEntity::new,
            ModBlocks.BITCOIN_MINER
    );

    public static void registerBlockEntities() {
        BoundlessRealmsMod.LOGGER.info("Registering Block Entities for " + BoundlessRealmsMod.MOD_ID);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks) {

        Identifier id = Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, name);
        ResourceKey<BlockEntityType<?>> key = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, id);

        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id,
                FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }
}
