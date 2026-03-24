package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.entity.ModEntities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

    public static final Item AN_ITEM = register("an_item", Item::new, new Item.Settings());
    public static final Item BITCOIN = register("bitcoin", Item::new, new Item.Settings());
    public static final Item RUBY = register("ruby", Item::new, new Item.Settings());
    public static final Item FAKE_LUNCH_TICKET = register("fake_lunch_ticket", Item::new, new Item.Settings().maxCount(1));
    public static final Item MONEY = register("money", Item::new, new Item.Settings().maxCount(1000000));
    public static final Item LUNCH_TICKET = register("lunch_ticket", LunchTicketItem::new, new Item.Settings().maxCount(1));
    public static final Item BACKSTAB_TOTEM = register("backstab_totem", BackstabTotemItem::new, new Item.Settings().maxCount(1));
    public static final Item BEDROCK_GAUNTLET = register(
            "bedrock_gauntlet",
            BedrockGauntletItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .attributeModifiers(createBedrockGauntletAttributes())
    );
    public static final Item WITHER_FURY = register("wither_fury", WitherFuryItem::new,
            new Item.Settings().sword(ToolMaterial.DIAMOND, 3f, -2.4f));
    public static final Item TAIGA_ONE_SURVIVAL_MACHETE = register("taiga_1_survival_machete", Item::new,
            new Item.Settings().sword(ToolMaterial.NETHERITE, 6.0F, -3.0F));
    public static final Item ANGLERFISH_MASK = register("anglerfish_mask", AnglerfishMaskItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD)
                            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER).build()));
    public static final Item WALLET = register("wallet", WalletItem::new,
            new Item.Settings());
    public static final SpawnEggItem TICKET_INSPECTOR_SPAWN_EGG = register("ticket_inspector_spawn_egg", SpawnEggItem::new,
            new Item.Settings().spawnEgg(ModEntities.TICKET_INSPECTOR));

    /**
     * Medical Supplies
     */
    public static final Item AFAK_MED_KIT = register(
            "afak_med_kit",
            settings -> new MedKitItem(settings, 10.0f, 40),
            new Item.Settings());

    /**
     * Digital Items
     */
    public static final GraphicsCardItem ASUS_GTX_750TI = register(
            "asus_gtx_750ti",
            settings -> new GraphicsCardItem(settings, 1000),
            new Item.Settings()
    );
    public static final Item DUAL_FAN_COOLING_SYSTEM = register("dual_fan_cooling_system", Item::new, new Item.Settings());
    public static final Item HEAT_SINK = register("heat_sink", Item::new, new Item.Settings());
    public static final Item PCIE_GOLD_FINGERS = register("pcie_gold_fingers", Item::new, new Item.Settings());
    public static final Item GRAPHICS_PROCESSING_UNIT_CORE = register("graphics_processing_unit_core", Item::new, new Item.Settings());

    public static <T extends Item> T register(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
        Identifier id = Identifier.of(BoundlessRealmsMod.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        T item = itemFactory.apply(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    private static AttributeModifiersComponent createBedrockGauntletAttributes() {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                Identifier.of(BoundlessRealmsMod.MOD_ID, "bedrock_gauntlet_attack_damage"),
                                3.0,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(
                                Identifier.of(BoundlessRealmsMod.MOD_ID, "bedrock_gauntlet_attack_speed"),
                                1020.0,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }

    public static void registerModItems() {
        BoundlessRealmsMod.LOGGER.info("Registering items for " + BoundlessRealmsMod.MOD_ID);
    }
}
