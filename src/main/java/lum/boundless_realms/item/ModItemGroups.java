package lum.boundless_realms.item;

import lum.boundless_realms.BoundlessRealmsMod;
import lum.boundless_realms.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
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
                        entries.add(ModItems.PORTABLE_CRAFTING_TABLE);
                        entries.add(ModItems.LUNCH_TICKET);
                        entries.add(ModItems.FAKE_LUNCH_TICKET);
                        entries.add(ModItems.NETHER_FURNACE_UPGRADE_TEMPLATE);
                        entries.add(ModItems.WALLET);
                        entries.add(ModBlocks.NETHER_FURNACE);
                        entries.add(ModItems.TICKET_INSPECTOR_SPAWN_EGG);
                        entries.add(ModItems.BEDROCK_HAND);
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

    public static void registerItemGroups() {
        BoundlessRealmsMod.LOGGER.info("Registering item groups for " + BoundlessRealmsMod.MOD_ID);
    }
}
