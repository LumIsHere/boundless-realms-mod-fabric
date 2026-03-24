package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup BOUNDLESS_GEARS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "boundless_gears"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.VOIDSTEEL_CHESTPLATE))
                    .displayName(Text.translatable("itemgroup.boundless_realms.boundless_gears"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.VOIDSTEEL_INGOT);
                        entries.add(ModItems.VOIDSTEEL_SWORD);
                        entries.add(ModItems.VOIDSTEEL_PICKAXE);
                        entries.add(ModItems.VOIDSTEEL_AXE);
                        entries.add(ModItems.VOIDSTEEL_SHOVEL);
                        entries.add(ModItems.VOIDSTEEL_HOE);
                        entries.add(ModItems.VOIDSTEEL_HELMET);
                        entries.add(ModItems.VOIDSTEEL_CHESTPLATE);
                        entries.add(ModItems.VOIDSTEEL_LEGGINGS);
                        entries.add(ModItems.VOIDSTEEL_BOOTS);
                    })
                    .build());

    public static final ItemGroup BOUNDLESS_REALMS_GENERAL_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "boundless_realms_general"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.RUBY))
                    .displayName(Text.translatable("itemgroup.boundless_realms.boundless_realms_general"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.RUBY);
                        entries.add(ModItems.MONEY);
                        entries.add(ModItems.TAIGA_ONE_SURVIVAL_MACHETE);
                        entries.add(ModItems.BEDROCK_GAUNTLET);
                        entries.add(ModItems.WITHER_FURY);
                        entries.add(ModItems.BACKSTAB_TOTEM);
                        entries.add(ModItems.ANGLERFISH_MASK);
                        entries.add(ModItems.LUNCH_TICKET);
                        entries.add(ModItems.FAKE_LUNCH_TICKET);
                        entries.add(ModItems.WALLET);
                        entries.add(ModItems.TICKET_INSPECTOR_SPAWN_EGG);
                        entries.add(ModItems.BITCOIN);
                        entries.add(ModBlocks.BITCOIN_MINER);
                    })
                    .build());

    public static final ItemGroup BOUNDLESS_REALMS_MEDICAL_SUPPLIES_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "boundless_realms_medical_supplies"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.AFAK_MED_KIT))
                    .displayName(Text.translatable("itemgroup.boundless_realms.boundless_realms_medical_supplies"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.AFAK_MED_KIT);
                    })
                    .build());

    public static final ItemGroup DIGITAL_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "digital_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.ASUS_GTX_750TI))
                    .displayName(Text.translatable("itemgroup.boundless_realms.digital_items"))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.ASUS_GTX_750TI);
                        entries.add(ModItems.DUAL_FAN_COOLING_SYSTEM);
                        entries.add(ModItems.HEAT_SINK);
                        entries.add(ModItems.PCIE_GOLD_FINGERS);
                        entries.add(ModItems.GRAPHICS_PROCESSING_UNIT_CORE);
                    })).build());

    public static void registerItemGroups() {
        BoundlessRealmsMod.LOGGER.info("Registering item groups for " + BoundlessRealmsMod.MOD_ID);
    }
}
