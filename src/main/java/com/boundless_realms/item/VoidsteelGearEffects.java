package com.boundless_realms.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class VoidsteelGearEffects {
    private static final float SILVER_LINING_HEALTH_THRESHOLD = 3.0F;
    private static final double SILVER_LINING_RADIUS = 10.0;
    private static final int SILVER_LINING_DURATION = 200;
    private static final long SILVER_LINING_COOLDOWN = 200L;
    private static final long VOID_RESCUE_COOLDOWN = 40L;
    private static final Map<UUID, Long> LAST_SILVER_LINING_TICK = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_VOID_RESCUE_TICK = new ConcurrentHashMap<>();

    private VoidsteelGearEffects() {
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(VoidsteelGearEffects::onEndWorldTick);
    }

    private static void onEndWorldTick(ServerWorld world) {
        long tick = world.getTime();
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!hasFullVoidsteelSet(player)) {
                continue;
            }

            tryRescueFromVoid(player, world, tick);
            trySilverLiningBurst(player, world, tick);
        }
    }

    private static boolean hasFullVoidsteelSet(ServerPlayerEntity player) {
        return player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD).isOf(ModItems.VOIDSTEEL_HELMET)
                && player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).isOf(ModItems.VOIDSTEEL_CHESTPLATE)
                && player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS).isOf(ModItems.VOIDSTEEL_LEGGINGS)
                && player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET).isOf(ModItems.VOIDSTEEL_BOOTS);
    }

    private static void tryRescueFromVoid(ServerPlayerEntity player, ServerWorld world, long tick) {
        if (player.getY() > world.getBottomY() - 4) {
            return;
        }

        long lastTrigger = LAST_VOID_RESCUE_TICK.getOrDefault(player.getUuid(), Long.MIN_VALUE);
        if (tick - lastTrigger < VOID_RESCUE_COOLDOWN) {
            return;
        }

        BlockPos rescuePos = findNearestLand(world, player.getBlockPos(), 128);
        if (rescuePos == null) {
            rescuePos = getSpawnFallback(world);
        }

        LAST_VOID_RESCUE_TICK.put(player.getUuid(), tick);
        player.teleport(
                world,
                rescuePos.getX() + 0.5,
                rescuePos.getY(),
                rescuePos.getZ() + 0.5,
                Set.<PositionFlag>of(),
                player.getYaw(),
                player.getPitch(),
                true
        );
        player.setVelocity(Vec3d.ZERO);
        world.spawnParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 0.5, player.getZ(), 32, 0.4, 0.6, 0.4, 0.05);
    }

    private static void trySilverLiningBurst(ServerPlayerEntity player, ServerWorld world, long tick) {
        if (player.getHealth() > SILVER_LINING_HEALTH_THRESHOLD) {
            return;
        }

        long lastTrigger = LAST_SILVER_LINING_TICK.getOrDefault(player.getUuid(), Long.MIN_VALUE);
        if (tick - lastTrigger < SILVER_LINING_COOLDOWN) {
            return;
        }

        Box area = player.getBoundingBox().expand(SILVER_LINING_RADIUS);
        var hostiles = world.getOtherEntities(player, area, entity -> entity instanceof HostileEntity hostile && hostile.isAlive());
        if (hostiles.isEmpty()) {
            return;
        }

        LAST_SILVER_LINING_TICK.put(player.getUuid(), tick);
        for (var entity : hostiles) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            Vec3d pushDirection = new Vec3d(entity.getX() - player.getX(), 0.0, entity.getZ() - player.getZ());
            double horizontalLength = Math.max(0.001, Math.sqrt(pushDirection.x * pushDirection.x + pushDirection.z * pushDirection.z));
            livingEntity.takeKnockback(2.5, pushDirection.x / horizontalLength, pushDirection.z / horizontalLength);
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, SILVER_LINING_DURATION, 1));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, SILVER_LINING_DURATION, 0));
        }

        world.spawnParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.0, player.getZ(), 48, 0.8, 0.8, 0.8, 0.15);
    }

    private static BlockPos findNearestLand(ServerWorld world, BlockPos origin, int maxRadius) {
        for (int radius = 0; radius <= maxRadius; radius++) {
            for (int xOffset = -radius; xOffset <= radius; xOffset++) {
                for (int zOffset = -radius; zOffset <= radius; zOffset++) {
                    if (Math.max(Math.abs(xOffset), Math.abs(zOffset)) != radius) {
                        continue;
                    }

                    BlockPos safePos = getSafeLandingPos(world, origin.getX() + xOffset, origin.getZ() + zOffset);
                    if (safePos != null) {
                        return safePos;
                    }
                }
            }
        }

        return null;
    }

    private static BlockPos getSpawnFallback(ServerWorld world) {
        BlockPos spawnPos = world.getSpawnPoint().getPos();
        BlockPos topPos = getSafeLandingPos(world, spawnPos.getX(), spawnPos.getZ());
        return topPos != null ? topPos : spawnPos.up();
    }

    private static BlockPos getSafeLandingPos(ServerWorld world, int x, int z) {
        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (topY <= world.getBottomY()) {
            return null;
        }

        BlockPos feetPos = new BlockPos(x, topY, z);
        BlockPos headPos = feetPos.up();
        BlockPos floorPos = feetPos.down();

        BlockState feetState = world.getBlockState(feetPos);
        BlockState headState = world.getBlockState(headPos);
        BlockState floorState = world.getBlockState(floorPos);

        if (feetState.isAir() && headState.isAir() && floorState.isSolidBlock(world, floorPos)) {
            return feetPos;
        }

        return null;
    }
}
