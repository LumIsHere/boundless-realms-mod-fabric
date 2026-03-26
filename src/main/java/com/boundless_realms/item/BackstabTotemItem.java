package com.boundless_realms.item;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackstabTotemItem extends Item {

    public BackstabTotemItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (!world.isClientSide()) {

            // Find nearest player (excluding self)
            Player target = world.getNearestPlayer(
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    5000,
                    p -> p != user && p.isAlive()
            );

            if (target != null) {

                float yaw = target.getYRot();

                double radians = Math.toRadians(yaw);

                // पीछे direction (behind player)
                double offsetX = Math.sin(radians) * 1.5;
                double offsetZ = -Math.cos(radians) * 1.5;

                double newX = target.getX() + offsetX;
                double newY = target.getY();
                double newZ = target.getZ() + offsetZ;

                user.teleportTo(newX, newY, newZ);
                user.lookAt(
                        EntityAnchorArgument.Anchor.EYES,
                        target.getEyePosition()
                );

                user.sendOverlayMessage(
                        net.minecraft.network.chat.Component.translatable(
                                "teleported_to_target_backstab_totem",
                                target.getDisplayName()
                        )
                );

                user.getCooldowns().addCooldown(stack, 100);

            }

            if (target == null) {
                user.sendOverlayMessage(
                        net.minecraft.network.chat.Component.translatable("notification_no_target_backstab_totem")
                );
            }
            // Cooldown
        }

        return InteractionResult.SUCCESS;
    }
}
