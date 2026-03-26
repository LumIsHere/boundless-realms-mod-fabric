package com.boundless_realms.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;

public final class EmeraldGearEffects {
    private static final double GOLEM_CALM_RADIUS = 64.0D;

    private EmeraldGearEffects() {
    }

    public static void register() {
        ServerTickEvents.END_LEVEL_TICK.register(EmeraldGearEffects::onEndWorldTick);
    }

    public static boolean hasFullEmeraldSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.EMERALD_HELMET)
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.EMERALD_CHESTPLATE)
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.EMERALD_LEGGINGS)
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.EMERALD_BOOTS);
    }

    private static void onEndWorldTick(ServerLevel world) {
        for (var player : world.players()) {
            if (!hasFullEmeraldSet(player)) {
                continue;
            }

            calmNearbyGolems(world, player);
        }
    }

    private static void calmNearbyGolems(ServerLevel world, Player player) {
        for (IronGolem golem : world.getEntitiesOfClass(
                IronGolem.class,
                player.getBoundingBox().inflate(GOLEM_CALM_RADIUS),
                ironGolem -> isAngryAtPlayer(world, ironGolem, player) || ironGolem.getTarget() == player
        )) {
            golem.stopBeingAngry();
            golem.setTarget(null);
            golem.setLastHurtByMob(null);
        }
    }

    private static boolean isAngryAtPlayer(ServerLevel world, IronGolem golem, Player player) {
        return EntityReference.getLivingEntity(golem.getPersistentAngerTarget(), world) == player;
    }
}
