package com.boundless_realms.money;

import com.boundless_realms.BoundlessRealmsMod;
import java.util.Set;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockMoneyRarityResolver {
    private static final TagKey<Block> COMMON_OVERRIDE = rarityTag("common");
    private static final TagKey<Block> UNCOMMON_OVERRIDE = rarityTag("uncommon");
    private static final TagKey<Block> RARE_OVERRIDE = rarityTag("rare");
    private static final TagKey<Block> EPIC_OVERRIDE = rarityTag("epic");
    private static final TagKey<Block> LEGENDARY_OVERRIDE = rarityTag("legendary");
    private static final TagKey<Block> MYTHIC_OVERRIDE = rarityTag("mythic");

    private static final Set<Block> MYTHIC_BLOCKS = Set.of(
            Blocks.NETHERITE_BLOCK,
            Blocks.BEACON,
            Blocks.CONDUIT,
            Blocks.DRAGON_EGG,
            Blocks.HEAVY_CORE
    );
    private static final Set<Block> LEGENDARY_BLOCKS = Set.of(
            Blocks.ANCIENT_DEBRIS,
            Blocks.DIAMOND_BLOCK,
            Blocks.EMERALD_BLOCK,
            Blocks.LODESTONE,
            Blocks.RESPAWN_ANCHOR,
            Blocks.ENCHANTING_TABLE,
            Blocks.ENDER_CHEST,
            Blocks.SPAWNER,
            Blocks.TRIAL_SPAWNER,
            Blocks.VAULT
    );
    private static final Set<Block> EPIC_BLOCKS = Set.of(
            Blocks.GOLD_BLOCK,
            Blocks.RAW_GOLD_BLOCK,
            Blocks.OBSIDIAN,
            Blocks.CRYING_OBSIDIAN,
            Blocks.GILDED_BLACKSTONE,
            Blocks.SCULK_CATALYST,
            Blocks.SCULK_SHRIEKER
    );
    private static final Set<Block> RARE_BLOCKS = Set.of(
            Blocks.IRON_BLOCK,
            Blocks.RAW_IRON_BLOCK,
            Blocks.LAPIS_BLOCK,
            Blocks.REDSTONE_BLOCK,
            Blocks.COPPER_BLOCK,
            Blocks.RAW_COPPER_BLOCK,
            Blocks.AMETHYST_BLOCK,
            Blocks.HOPPER,
            Blocks.BREWING_STAND,
            Blocks.CRAFTER
    );

    private BlockMoneyRarityResolver() {
    }

    public static BlockMoneyRarity resolve(BlockState state, BlockGetter level, BlockPos pos) {
        BlockMoneyRarity override = resolveOverride(state);
        if (override != null) {
            return override;
        }

        Block block = state.getBlock();
        if (MYTHIC_BLOCKS.contains(block) || state.is(ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE)) {
            return BlockMoneyRarity.MYTHIC;
        }
        if (LEGENDARY_BLOCKS.contains(block)
                || state.is(ConventionalBlockTags.NETHERITE_SCRAP_ORES)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_DIAMOND)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_EMERALD)
                || state.is(BlockTags.SHULKER_BOXES)) {
            return BlockMoneyRarity.LEGENDARY;
        }
        if (EPIC_BLOCKS.contains(block)
                || state.is(ConventionalBlockTags.DIAMOND_ORES)
                || state.is(ConventionalBlockTags.EMERALD_ORES)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_GOLD)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_RAW_GOLD)
                || state.is(ConventionalBlockTags.OBSIDIANS)) {
            return BlockMoneyRarity.EPIC;
        }
        if (RARE_BLOCKS.contains(block)
                || state.is(ConventionalBlockTags.GOLD_ORES)
                || state.is(ConventionalBlockTags.IRON_ORES)
                || state.is(ConventionalBlockTags.LAPIS_ORES)
                || state.is(ConventionalBlockTags.REDSTONE_ORES)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_IRON)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_RAW_IRON)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_LAPIS)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_REDSTONE)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_COPPER)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_RAW_COPPER)
                || state.is(BlockTags.ANVIL)) {
            return BlockMoneyRarity.RARE;
        }
        if (isUncommon(state)) {
            return BlockMoneyRarity.UNCOMMON;
        }
        if (isCommon(state)) {
            return BlockMoneyRarity.COMMON;
        }

        Item blockItem = block.asItem();
        if (blockItem != Items.AIR) {
            Rarity itemRarity = new ItemStack(blockItem).getRarity();
            if (itemRarity == Rarity.EPIC) {
                return BlockMoneyRarity.EPIC;
            }
            if (itemRarity == Rarity.RARE) {
                return BlockMoneyRarity.RARE;
            }
            if (itemRarity == Rarity.UNCOMMON) {
                return BlockMoneyRarity.UNCOMMON;
            }
        }

        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return BlockMoneyRarity.EPIC;
        }
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
            return BlockMoneyRarity.RARE;
        }
        if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
            return BlockMoneyRarity.UNCOMMON;
        }

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness >= 20.0F) {
            return BlockMoneyRarity.LEGENDARY;
        }
        if (hardness >= 8.0F) {
            return BlockMoneyRarity.EPIC;
        }
        if (hardness >= 4.0F) {
            return BlockMoneyRarity.RARE;
        }
        if (hardness >= 1.5F) {
            return BlockMoneyRarity.UNCOMMON;
        }
        return BlockMoneyRarity.COMMON;
    }

    private static BlockMoneyRarity resolveOverride(BlockState state) {
        if (state.is(MYTHIC_OVERRIDE)) {
            return BlockMoneyRarity.MYTHIC;
        }
        if (state.is(LEGENDARY_OVERRIDE)) {
            return BlockMoneyRarity.LEGENDARY;
        }
        if (state.is(EPIC_OVERRIDE)) {
            return BlockMoneyRarity.EPIC;
        }
        if (state.is(RARE_OVERRIDE)) {
            return BlockMoneyRarity.RARE;
        }
        if (state.is(UNCOMMON_OVERRIDE)) {
            return BlockMoneyRarity.UNCOMMON;
        }
        if (state.is(COMMON_OVERRIDE)) {
            return BlockMoneyRarity.COMMON;
        }
        return null;
    }

    private static boolean isUncommon(BlockState state) {
        return state.is(ConventionalBlockTags.COAL_ORES)
                || state.is(ConventionalBlockTags.COPPER_ORES)
                || state.is(ConventionalBlockTags.QUARTZ_ORES)
                || state.is(ConventionalBlockTags.STORAGE_BLOCKS_COAL)
                || state.is(ConventionalBlockTags.ORES)
                || state.is(ConventionalBlockTags.STONES)
                || state.is(ConventionalBlockTags.COBBLESTONES)
                || state.is(ConventionalBlockTags.END_STONES)
                || state.is(ConventionalBlockTags.GLASS_BLOCKS)
                || state.is(ConventionalBlockTags.GLASS_PANES)
                || state.is(ConventionalBlockTags.CONCRETES)
                || state.is(ConventionalBlockTags.GLAZED_TERRACOTTAS)
                || state.is(ConventionalBlockTags.SANDSTONE_BLOCKS)
                || state.is(BlockTags.TERRACOTTA)
                || state.is(BlockTags.COPPER);
    }

    private static boolean isCommon(BlockState state) {
        return state.is(BlockTags.LOGS)
                || state.is(BlockTags.PLANKS)
                || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.WOODEN_SLABS)
                || state.is(BlockTags.WOODEN_FENCES)
                || state.is(BlockTags.WOODEN_DOORS)
                || state.is(BlockTags.WOODEN_TRAPDOORS)
                || state.is(BlockTags.WOODEN_BUTTONS)
                || state.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || state.is(BlockTags.ALL_SIGNS)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.SAPLINGS)
                || state.is(BlockTags.DIRT)
                || state.is(BlockTags.MUD)
                || state.is(BlockTags.GRASS_BLOCKS)
                || state.is(BlockTags.MOSS_BLOCKS)
                || state.is(BlockTags.SAND)
                || state.is(BlockTags.SNOW)
                || state.is(BlockTags.WOOL)
                || state.is(BlockTags.WOOL_CARPETS)
                || state.is(BlockTags.CROPS)
                || state.is(BlockTags.FLOWERS)
                || state.is(BlockTags.BAMBOO_BLOCKS)
                || state.is(BlockTags.WART_BLOCKS)
                || state.is(BlockTags.BEDS)
                || state.is(ConventionalBlockTags.GRAVELS)
                || state.is(ConventionalBlockTags.NETHERRACKS);
    }

    private static TagKey<Block> rarityTag(String rarity) {
        return TagKey.create(
                Registries.BLOCK,
                Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "money_rarity/" + rarity)
        );
    }
}
