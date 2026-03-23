package com.boundless_realms.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BedrockGauntletItem extends Item {

    private static final double LAUNCH_STRENGTH = 1.3;
    private static final double EXTRA_UPWARD_BOOST = 0.35;

    public BedrockGauntletItem(Settings settings) {
        super(settings);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Push the target away from the attacker, then add extra vertical lift for an uppercut feel.
        target.takeKnockback(LAUNCH_STRENGTH, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        target.addVelocity(0.0, EXTRA_UPWARD_BOOST, 0.0);

        super.postHit(stack, target, attacker);
    }
}
