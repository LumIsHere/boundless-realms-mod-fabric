package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> FURIOUS = keyOf("furious");
    public static final ResourceKey<Enchantment> VIOLENT = keyOf("violent");

    private ModEnchantments() {
    }

    public static int getLevel(Level world, ItemStack stack, ResourceKey<Enchantment> enchantmentKey) {
        Registry<Enchantment> enchantmentRegistry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Enchantment enchantment = enchantmentRegistry.getOptional(enchantmentKey).orElse(null);
        if (enchantment == null) {
            return 0;
        }

        return EnchantmentHelper.getItemEnchantmentLevel(enchantmentRegistry.wrapAsHolder(enchantment), stack);
    }

    public static boolean hasWitherFuryExclusiveEnchantments(Level world, ItemStack stack) {
        return getLevel(world, stack, FURIOUS) > 0 || getLevel(world, stack, VIOLENT) > 0;
    }

    private static ResourceKey<Enchantment> keyOf(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, name));
    }
}
