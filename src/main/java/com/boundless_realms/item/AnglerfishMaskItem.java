package com.boundless_realms.item;

import org.jspecify.annotations.Nullable;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AnglerfishMaskItem extends Item {
    public AnglerfishMaskItem(Properties settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world.isClientSide()) return;

        if (entity instanceof Player player) {
            ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);

            if (headStack == stack) {
                if (player.isUnderWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 11, 0, false, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20 * 11, 0, false, false, true));
                }

                repelDrowneds(world, player);
            }
        }
        super.inventoryTick(stack, world, entity, slot);
    }

    private void repelDrowneds(Level world, Player player) {
        List<Drowned> drowneds = world.getEntitiesOfClass(Drowned.class,
                player.getBoundingBox().inflate(12.0),
                drowned -> true);

        for (Drowned drowned : drowneds) {
            if (drowned.getTarget() == player || drowned.getLastHurtByMob() == player) {

                drowned.setTarget(null);
                drowned.setLastHurtByMob(null);
                drowned.setAggressive(false);

                double diffX = drowned.getX() - player.getX();
                double diffZ = drowned.getZ() - player.getZ();


                if (drowned.distanceTo(player) < 3.0) {
                    drowned.knockback(0.5, -diffX, -diffZ);
                }

                double fleeX = drowned.getX() + (diffX * 2);
                double fleeZ = drowned.getZ() + (diffZ * 2);

                drowned.getNavigation().moveTo(fleeX, drowned.getY(), fleeZ, 1.2);
            }
        }
    }
}