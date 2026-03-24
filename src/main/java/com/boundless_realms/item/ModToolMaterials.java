package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModToolMaterials {
    public static final TagKey<net.minecraft.item.Item> VOIDSTEEL_REPAIR_MATERIALS = TagKey.of(
            RegistryKeys.ITEM,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "voidsteel_repair_materials")
    );

    public static final ToolMaterial VOIDSTEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2400,
            10.5F,
            5.0F,
            18,
            VOIDSTEEL_REPAIR_MATERIALS
    );

    private ModToolMaterials() {
    }
}
