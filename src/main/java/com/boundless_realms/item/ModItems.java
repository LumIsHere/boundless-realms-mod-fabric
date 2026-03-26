package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.entity.ModEntities;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;

public class ModItems {

    public static final Item AN_ITEM = register("an_item", Item::new, new Item.Properties());
    public static final Item BITCOIN = register("bitcoin", Item::new, new Item.Properties());
    public static final Item RUBY = register("ruby", Item::new, new Item.Properties());
    /**
     * Voidsteel Items
     */
    public static final Item VOIDSTEEL_INGOT = register("voidsteel_ingot", Item::new,
            voidsteelSettings());
    public static final Item VOIDSTEEL_SWORD = register("voidsteel_sword", Item::new,
            voidsteelSettings().sword(ModToolMaterials.VOIDSTEEL, 4.0F, -2.4F));
    public static final Item VOIDSTEEL_PICKAXE = register("voidsteel_pickaxe", Item::new,
            voidsteelSettings().pickaxe(ModToolMaterials.VOIDSTEEL, 2.0F, -2.8F));
    public static final Item VOIDSTEEL_AXE = register("voidsteel_axe", Item::new,
            voidsteelSettings().axe(ModToolMaterials.VOIDSTEEL, 7.0F, -3.0F));
    public static final Item VOIDSTEEL_SHOVEL = register("voidsteel_shovel", Item::new,
            voidsteelSettings().shovel(ModToolMaterials.VOIDSTEEL, 2.5F, -3.0F));
    public static final Item VOIDSTEEL_HOE = register("voidsteel_hoe", Item::new,
            voidsteelSettings().hoe(ModToolMaterials.VOIDSTEEL, -3.0F, 0.0F));
    public static final Item VOIDSTEEL_HELMET = register("voidsteel_helmet", Item::new,
            voidsteelSettings().humanoidArmor(ModArmorMaterials.VOIDSTEEL, net.minecraft.world.item.equipment.ArmorType.HELMET));
    public static final Item VOIDSTEEL_CHESTPLATE = register("voidsteel_chestplate", Item::new,
            voidsteelSettings().humanoidArmor(ModArmorMaterials.VOIDSTEEL, net.minecraft.world.item.equipment.ArmorType.CHESTPLATE));
    public static final Item VOIDSTEEL_LEGGINGS = register("voidsteel_leggings", Item::new,
            voidsteelSettings().humanoidArmor(ModArmorMaterials.VOIDSTEEL, net.minecraft.world.item.equipment.ArmorType.LEGGINGS));
    public static final Item VOIDSTEEL_BOOTS = register("voidsteel_boots", Item::new,
            voidsteelSettings().humanoidArmor(ModArmorMaterials.VOIDSTEEL, net.minecraft.world.item.equipment.ArmorType.BOOTS));
    public static final Item EMERALD_HELMET = register("emerald_helmet", Item::new,
            new Item.Properties().humanoidArmor(ModArmorMaterials.EMERALD, net.minecraft.world.item.equipment.ArmorType.HELMET));
    public static final Item EMERALD_CHESTPLATE = register("emerald_chestplate", Item::new,
            new Item.Properties().humanoidArmor(ModArmorMaterials.EMERALD, net.minecraft.world.item.equipment.ArmorType.CHESTPLATE));
    public static final Item EMERALD_LEGGINGS = register("emerald_leggings", Item::new,
            new Item.Properties().humanoidArmor(ModArmorMaterials.EMERALD, net.minecraft.world.item.equipment.ArmorType.LEGGINGS));
    public static final Item EMERALD_BOOTS = register("emerald_boots", Item::new,
            new Item.Properties().humanoidArmor(ModArmorMaterials.EMERALD, net.minecraft.world.item.equipment.ArmorType.BOOTS));

    public static final Item FAKE_LUNCH_TICKET = register("fake_lunch_ticket", Item::new, new Item.Properties().stacksTo(1));
    public static final Item MONEY = register("money", Item::new, new Item.Properties().stacksTo(10000));
    public static final Item LUNCH_TICKET = register("lunch_ticket", LunchTicketItem::new, new Item.Properties().stacksTo(1));
    public static final Item BACKSTAB_TOTEM = register("backstab_totem", BackstabTotemItem::new, new Item.Properties().stacksTo(1));
    public static final Item BEDROCK_GAUNTLET = register(
            "bedrock_gauntlet",
            BedrockGauntletItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .attributes(createBedrockGauntletAttributes())
    );
    public static final Item WITHER_FURY = register("wither_fury", WitherFuryItem::new,
            new Item.Properties().sword(ToolMaterial.DIAMOND, 3f, -2.4f));
    public static final Item TAIGA_ONE_SURVIVAL_MACHETE = register("taiga_1_survival_machete", Item::new,
            new Item.Properties().sword(ToolMaterial.NETHERITE, 6.0F, -3.0F));
    public static final Item ANGLERFISH_MASK = register("anglerfish_mask", AnglerfishMaskItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                            .setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER).build()));
    public static final Item WALLET = register("wallet", WalletItem::new,
            new Item.Properties());
    public static final SpawnEggItem TICKET_INSPECTOR_SPAWN_EGG = register("ticket_inspector_spawn_egg", SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntities.TICKET_INSPECTOR));

    /**
     * Medical Supplies
     */
    public static final Item AFAK_MED_KIT = register(
            "afak_med_kit",
            settings -> new MedKitItem(settings, 10.0f, 40),
            new Item.Properties());

    /**
     * Digital Items
     */
    public static final GraphicsCardItem ASUS_GTX_750TI = register(
            "asus_gtx_750ti",
            settings -> new GraphicsCardItem(settings, 1000),
            new Item.Properties()
    );
    public static final Item DUAL_FAN_COOLING_SYSTEM = register("dual_fan_cooling_system", Item::new, new Item.Properties());
    public static final Item HEAT_SINK = register("heat_sink", Item::new, new Item.Properties());
    public static final Item PCIE_GOLD_FINGERS = register("pcie_gold_fingers", Item::new, new Item.Properties());
    public static final Item GRAPHICS_PROCESSING_UNIT_CORE = register("graphics_processing_unit_core", Item::new, new Item.Properties());

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        Identifier id = Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        T item = itemFactory.apply(settings.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    private static Item.Properties voidsteelSettings() {
        return new Item.Properties().fireResistant().rarity(Rarity.EPIC);
    }

    private static ItemAttributeModifiers createBedrockGauntletAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "bedrock_gauntlet_attack_damage"),
                                3.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "bedrock_gauntlet_attack_speed"),
                                1020.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    public static void registerModItems() {
        BoundlessRealmsMod.LOGGER.info("Registering items for " + BoundlessRealmsMod.MOD_ID);
    }
}
