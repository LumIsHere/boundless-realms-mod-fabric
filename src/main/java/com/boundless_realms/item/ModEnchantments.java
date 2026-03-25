package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class ModEnchantments {
    public static final RegistryKey<Enchantment> FURIOUS = keyOf("furious");
    public static final RegistryKey<Enchantment> VIOLENT = keyOf("violent");

    private ModEnchantments() {
    }

    public static int getLevel(World world, ItemStack stack, RegistryKey<Enchantment> enchantmentKey) {
        Registry<Enchantment> enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        Enchantment enchantment = enchantmentRegistry.getOptionalValue(enchantmentKey).orElse(null);
        if (enchantment == null) {
            return 0;
        }

        return EnchantmentHelper.getLevel(enchantmentRegistry.getEntry(enchantment), stack);
    }

    public static boolean hasWitherFuryExclusiveEnchantments(World world, ItemStack stack) {
        return getLevel(world, stack, FURIOUS) > 0 || getLevel(world, stack, VIOLENT) > 0;
    }

    private static RegistryKey<Enchantment> keyOf(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(BoundlessRealmsMod.MOD_ID, name));
    }
}
