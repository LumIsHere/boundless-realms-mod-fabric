package com.boundless_realms.block;

import com.boundless_realms.BoundlessRealmsMod;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {

    public static final Block A_BLOCK = register(
            "a_block",
            Block::new,
            BlockBehaviour.Properties.of(),
            true
    );
    public static final Block BITCOIN_MINER = register(
            "bitcoin_miner",
            BitcoinMinerBlock::new,
            BlockBehaviour.Properties.of().strength(4.0f).mapColor(MapColor.METAL).requiresCorrectToolForDrops(),
            true
    );

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        Identifier id = Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, name);

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

        Block block = blockFactory.apply(settings.setId(blockKey));

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey));
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    public static void registerModBlocks() {
        BoundlessRealmsMod.LOGGER.info("Registering blocks for " + BoundlessRealmsMod.MOD_ID);
    }
}
