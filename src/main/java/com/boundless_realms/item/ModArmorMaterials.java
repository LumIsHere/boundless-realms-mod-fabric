package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class ModArmorMaterials {
    public static final RegistryKey<EquipmentAsset> VOIDSTEEL_ASSET = RegistryKey.of(
            EquipmentAssetKeys.REGISTRY_KEY,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "voidsteel")
    );

    public static final ArmorMaterial VOIDSTEEL = new ArmorMaterial(
            45,
            Map.of(
                    EquipmentType.HELMET, 4,
                    EquipmentType.CHESTPLATE, 10,
                    EquipmentType.LEGGINGS, 8,
                    EquipmentType.BOOTS, 4
            ),
            18,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            4.5F,
            0.2F,
            ModToolMaterials.VOIDSTEEL_REPAIR_MATERIALS,
            VOIDSTEEL_ASSET
    );

    private ModArmorMaterials() {
    }
}
