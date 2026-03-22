package com.boundless_realms.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.command.argument.EntityAnchorArgumentType;

public class BackstabTotemItem extends Item {

    public BackstabTotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {

            // Find nearest player (excluding self)
            PlayerEntity target = world.getClosestPlayer(
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    5000,
                    p -> p != user && p.isAlive()
            );

            if (target != null) {

                float yaw = target.getYaw();

                double radians = Math.toRadians(yaw);

                // पीछे direction (behind player)
                double offsetX = Math.sin(radians) * 1.5;
                double offsetZ = -Math.cos(radians) * 1.5;

                double newX = target.getX() + offsetX;
                double newY = target.getY();
                double newZ = target.getZ() + offsetZ;

                user.requestTeleport(newX, newY, newZ);
                user.lookAt(
                        EntityAnchorArgumentType.EntityAnchor.EYES,
                        target.getEyePos()
                );

                user.sendMessage(
                        net.minecraft.text.Text.translatable(
                                "teleported_to_target_backstab_totem",
                                target.getDisplayName()
                        ),
                        true
                );

                user.getItemCooldownManager().set(stack, 100);

            }

            if (target == null) {
                user.sendMessage(
                        net.minecraft.text.Text.translatable("notification_no_target_backstab_totem"),
                        true
                );
            }
            // Cooldown
        }

        return ActionResult.SUCCESS;
    }
}