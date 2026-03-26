package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    public static final CreativeModeTab BOUNDLESS_GEARS_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "boundless_gears"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.VOIDSTEEL_CHESTPLATE))
                    .title(Component.translatable("itemgroup.boundless_realms.boundless_gears"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(ModItems.VOIDSTEEL_INGOT);
                        entries.accept(ModItems.VOIDSTEEL_SWORD);
                        entries.accept(ModItems.VOIDSTEEL_PICKAXE);
                        entries.accept(ModItems.VOIDSTEEL_AXE);
                        entries.accept(ModItems.VOIDSTEEL_SHOVEL);
                        entries.accept(ModItems.VOIDSTEEL_HOE);
                        entries.accept(ModItems.VOIDSTEEL_HELMET);
                        entries.accept(ModItems.VOIDSTEEL_CHESTPLATE);
                        entries.accept(ModItems.VOIDSTEEL_LEGGINGS);
                        entries.accept(ModItems.VOIDSTEEL_BOOTS);
                        entries.accept(ModItems.EMERALD_HELMET);
                        entries.accept(ModItems.EMERALD_CHESTPLATE);
                        entries.accept(ModItems.EMERALD_LEGGINGS);
                        entries.accept(ModItems.EMERALD_BOOTS);
                    })
                    .build());

    public static final CreativeModeTab BOUNDLESS_REALMS_GENERAL_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "boundless_realms_general"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.RUBY))
                    .title(Component.translatable("itemgroup.boundless_realms.boundless_realms_general"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(ModItems.RUBY);
                        entries.accept(ModItems.MONEY);
                        entries.accept(ModItems.TAIGA_ONE_SURVIVAL_MACHETE);
                        entries.accept(ModItems.BEDROCK_GAUNTLET);
                        entries.accept(ModItems.WITHER_FURRY);
                        entries.accept(ModItems.BACKSTAB_TOTEM);
                        entries.accept(ModItems.ANGLERFISH_MASK);
                        entries.accept(ModItems.LUNCH_TICKET);
                        entries.accept(ModItems.FAKE_LUNCH_TICKET);
                        entries.accept(ModItems.WALLET);
                        entries.accept(ModItems.TICKET_INSPECTOR_SPAWN_EGG);
                        entries.accept(ModItems.BITCOIN);
                        entries.accept(ModBlocks.BITCOIN_MINER);
                    })
                    .build());

    public static final CreativeModeTab BOUNDLESS_REALMS_MEDICAL_SUPPLIES_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "boundless_realms_medical_supplies"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.AFAK_MED_KIT))
                    .title(Component.translatable("itemgroup.boundless_realms.boundless_realms_medical_supplies"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(ModItems.AFAK_MED_KIT);
                    })
                    .build());

    public static final CreativeModeTab DIGITAL_ITEMS_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "digital_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.ASUS_GTX_750TI))
                    .title(Component.translatable("itemgroup.boundless_realms.digital_items"))
                    .displayItems(((displayContext, entries) -> {
                        entries.accept(ModItems.ASUS_GTX_750TI);
                        entries.accept(ModItems.DUAL_FAN_COOLING_SYSTEM);
                        entries.accept(ModItems.HEAT_SINK);
                        entries.accept(ModItems.PCIE_GOLD_FINGERS);
                        entries.accept(ModItems.GRAPHICS_PROCESSING_UNIT_CORE);
                    })).build());

    public static void registerItemGroups() {
        BoundlessRealmsMod.LOGGER.info("Registering item groups for " + BoundlessRealmsMod.MOD_ID);
    }
}
