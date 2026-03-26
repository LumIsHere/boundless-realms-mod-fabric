package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class ModArmorMaterials {
    public static final TagKey<net.minecraft.world.item.Item> EMERALD_REPAIR_MATERIALS = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "emerald_repair_materials")
    );

    public static final ResourceKey<EquipmentAsset> EMERALD_ASSET = ResourceKey.create(
            EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "emerald")
    );

    public static final ResourceKey<EquipmentAsset> VOIDSTEEL_ASSET = ResourceKey.create(
            EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "voidsteel")
    );

    public static final ArmorMaterial EMERALD = new ArmorMaterial(
            20,
            Map.of(
                    ArmorType.HELMET, 2,
                    ArmorType.CHESTPLATE, 6,
                    ArmorType.LEGGINGS, 5,
                    ArmorType.BOOTS, 2
            ),
            18,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            1.0F,
            0.0F,
            EMERALD_REPAIR_MATERIALS,
            EMERALD_ASSET
    );

    public static final ArmorMaterial VOIDSTEEL = new ArmorMaterial(
            45,
            Map.of(
                    ArmorType.HELMET, 4,
                    ArmorType.CHESTPLATE, 10,
                    ArmorType.LEGGINGS, 8,
                    ArmorType.BOOTS, 4
            ),
            18,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.5F,
            0.2F,
            ModToolMaterials.VOIDSTEEL_REPAIR_MATERIALS,
            VOIDSTEEL_ASSET
    );

    private ModArmorMaterials() {
    }
}
