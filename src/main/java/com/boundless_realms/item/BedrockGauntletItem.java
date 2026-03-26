package com.boundless_realms.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BedrockGauntletItem extends Item {

    private static final double LAUNCH_STRENGTH = 1.3;
    private static final double EXTRA_UPWARD_BOOST = 0.35;

    public BedrockGauntletItem(Properties settings) {
        super(settings);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Push the target away from the attacker, then add extra vertical lift for an uppercut feel.
        target.knockback(LAUNCH_STRENGTH, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        target.push(0.0, EXTRA_UPWARD_BOOST, 0.0);

        super.hurtEnemy(stack, target, attacker);
    }
}
