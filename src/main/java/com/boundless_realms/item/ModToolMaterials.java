package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;

public final class ModToolMaterials {
    public static final TagKey<net.minecraft.world.item.Item> VOIDSTEEL_REPAIR_MATERIALS = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "voidsteel_repair_materials")
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
