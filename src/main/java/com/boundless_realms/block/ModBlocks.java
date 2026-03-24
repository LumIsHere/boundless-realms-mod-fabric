package com.boundless_realms.block;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import java.util.function.Function;

public class ModBlocks {

    public static final Block A_BLOCK = register(
            "a_block",
            Block::new,
            AbstractBlock.Settings.create(),
            true
    );
    public static final Block BITCOIN_MINER = register(
            "bitcoin_miner",
            BitcoinMinerBlock::new,
            AbstractBlock.Settings.create().strength(4.0f).mapColor(MapColor.IRON_GRAY).requiresTool(),
            true
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(BoundlessRealmsMod.MOD_ID, name);

        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);

        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    public static void registerModBlocks() {
        BoundlessRealmsMod.LOGGER.info("Registering blocks for " + BoundlessRealmsMod.MOD_ID);
    }
}
