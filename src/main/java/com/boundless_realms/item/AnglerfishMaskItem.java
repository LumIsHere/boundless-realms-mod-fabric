package com.boundless_realms.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AnglerfishMaskItem extends Item {
    public AnglerfishMaskItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world.isClient()) return;

        if (entity instanceof PlayerEntity player) {
            ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);

            if (headStack == stack) {
                if (player.isSubmergedInWater()) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 20 * 11, 0, false, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 20 * 11, 0, false, false, true));
                }

                repelDrowneds(world, player);
            }
        }
        super.inventoryTick(stack, world, entity, slot);
    }

    private void repelDrowneds(World world, PlayerEntity player) {
        List<DrownedEntity> drowneds = world.getEntitiesByClass(DrownedEntity.class,
                player.getBoundingBox().expand(12.0),
                drowned -> true);

        for (DrownedEntity drowned : drowneds) {
            if (drowned.getTarget() == player || drowned.getAttacker() == player) {

                drowned.setTarget(null);
                drowned.setAttacker(null);
                drowned.setAttacking(false);

                double diffX = drowned.getX() - player.getX();
                double diffZ = drowned.getZ() - player.getZ();


                if (drowned.distanceTo(player) < 3.0) {
                    drowned.takeKnockback(0.5, -diffX, -diffZ);
                }

                double fleeX = drowned.getX() + (diffX * 2);
                double fleeZ = drowned.getZ() + (diffZ * 2);

                drowned.getNavigation().startMovingTo(fleeX, drowned.getY(), fleeZ, 1.2);
            }
        }
    }
}