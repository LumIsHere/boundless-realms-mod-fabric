package com.boundless_realms.block;

import com.boundless_realms.BoundlessRealmsMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<NetherFurnaceBlockEntity> NETHER_FURNACE_ENTITY = register(
            "nether_furnace_be",
            NetherFurnaceBlockEntity::new,
            ModBlocks.NETHER_FURNACE
    );

    public static void registerBlockEntities() {
        BoundlessRealmsMod.LOGGER.info("Registering Block Entities for " + BoundlessRealmsMod.MOD_ID);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks) {

        Identifier id = Identifier.of(BoundlessRealmsMod.MOD_ID, name);
        RegistryKey<BlockEntityType<?>> key = RegistryKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, id);

        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                id,
                FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }
}