package com.boundless_realms.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class EmeraldGearEffects {
    private static final double GOLEM_CALM_RADIUS = 64.0D;

    private EmeraldGearEffects() {
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(EmeraldGearEffects::onEndWorldTick);
    }

    public static boolean hasFullEmeraldSet(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.EMERALD_HELMET)
                && player.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.EMERALD_CHESTPLATE)
                && player.getEquippedStack(EquipmentSlot.LEGS).isOf(ModItems.EMERALD_LEGGINGS)
                && player.getEquippedStack(EquipmentSlot.FEET).isOf(ModItems.EMERALD_BOOTS);
    }

    private static void onEndWorldTick(ServerWorld world) {
        for (var player : world.getPlayers()) {
            if (!hasFullEmeraldSet(player)) {
                continue;
            }

            calmNearbyGolems(world, player);
        }
    }

    private static void calmNearbyGolems(ServerWorld world, PlayerEntity player) {
        for (IronGolemEntity golem : world.getEntitiesByClass(
                IronGolemEntity.class,
                player.getBoundingBox().expand(GOLEM_CALM_RADIUS),
                ironGolem -> isAngryAtPlayer(world, ironGolem, player) || ironGolem.getTarget() == player
        )) {
            golem.stopAnger();
            golem.setTarget(null);
            golem.setAttacker(null);
        }
    }

    private static boolean isAngryAtPlayer(ServerWorld world, IronGolemEntity golem, PlayerEntity player) {
        return LazyEntityReference.getLivingEntity(golem.getAngryAt(), world) == player;
    }
}
