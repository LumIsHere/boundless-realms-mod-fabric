package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class VoidsteelGearEffects {
    private static final int VOID_RESCUE_TRIGGER_DEPTH = 64;
    private static final int VOID_RESCUE_SEARCH_RADIUS = 1024;

    private VoidsteelGearEffects() {
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(VoidsteelGearEffects::onEndWorldTick);
    }

    @SuppressWarnings("resource")
    public static void tryRescuePlayerFromVoid(ServerPlayerEntity player) {
        if (player.isSpectator() || !player.isAlive() || !hasFullVoidsteelSet(player)) {
            BoundlessRealmsMod.LOGGER.info(
                    "Voidsteel rescue skipped for {} in {}: spectator={}, alive={}, fullSet={}",
                    player.getName().getString(),
                    player.getEntityWorld().getRegistryKey().getValue(),
                    player.isSpectator(),
                    player.isAlive(),
                    hasFullVoidsteelSet(player)
            );
            return;
        }

        ServerWorld world = player.getEntityWorld();
        RescueDestination destination = findRescueDestination(player, world);
        ServerWorld destinationWorld = destination.world();
        BoundlessRealmsMod.LOGGER.info(
                "Voidsteel rescue triggered for {} from {} at y={} to {} {}",
                player.getName().getString(),
                world.getRegistryKey().getValue(),
                player.getY(),
                destinationWorld.getRegistryKey().getValue(),
                destination.pos()
        );
        player.teleport(
                destinationWorld,
                destination.pos().getX() + 0.5,
                destination.pos().getY(),
                destination.pos().getZ() + 0.5,
                Set.of(),
                player.getYaw(),
                player.getPitch(),
                true
        );
        player.setVelocity(Vec3d.ZERO);
        player.fallDistance = 0.0F;
        destinationWorld.spawnParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 0.5, player.getZ(), 32, 0.4, 0.6, 0.4, 0.05);
    }

    private static void onEndWorldTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!hasFullVoidsteelSet(player)) {
                continue;
            }

            tryRescueFromVoid(player, world);
        }
    }

    private static boolean hasFullVoidsteelSet(ServerPlayerEntity player) {
        return player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD).isOf(ModItems.VOIDSTEEL_HELMET)
                && player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).isOf(ModItems.VOIDSTEEL_CHESTPLATE)
                && player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS).isOf(ModItems.VOIDSTEEL_LEGGINGS)
                && player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET).isOf(ModItems.VOIDSTEEL_BOOTS);
    }

    private static void tryRescueFromVoid(ServerPlayerEntity player, ServerWorld world) {
        if (player.isSpectator() || !player.isAlive()) {
            return;
        }

        if (player.getY() > world.getBottomY() - VOID_RESCUE_TRIGGER_DEPTH) {
            return;
        }

        BoundlessRealmsMod.LOGGER.info(
                "Voidsteel void threshold reached for {} in {}: y={}, triggerY={}, fullSet={}",
                player.getName().getString(),
                world.getRegistryKey().getValue(),
                player.getY(),
                world.getBottomY() - VOID_RESCUE_TRIGGER_DEPTH,
                hasFullVoidsteelSet(player)
        );
        tryRescuePlayerFromVoid(player);
    }

    private static BlockPos findNearestLand(ServerWorld world, BlockPos origin) {
        for (int radius = 0; radius <= VOID_RESCUE_SEARCH_RADIUS; radius++) {
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

    @SuppressWarnings("resource")
    private static RescueDestination findRescueDestination(ServerPlayerEntity player, ServerWorld currentWorld) {
        BlockPos nearestLand = findNearestLand(currentWorld, player.getBlockPos());
        if (nearestLand != null) {
            BoundlessRealmsMod.LOGGER.info(
                    "Voidsteel rescue found nearby land for {} in {} at {}",
                    player.getName().getString(),
                    currentWorld.getRegistryKey().getValue(),
                    nearestLand
            );
            return new RescueDestination(currentWorld, nearestLand);
        }

        ServerWorld overworld = Objects.requireNonNull(
                currentWorld.getServer().getWorld(World.OVERWORLD),
                "Overworld must be present for void rescue fallback"
        );
        BoundlessRealmsMod.LOGGER.info(
                "Voidsteel rescue falling back to overworld spawn for {} from {}",
                player.getName().getString(),
                currentWorld.getRegistryKey().getValue()
        );
        return new RescueDestination(overworld, getSpawnFallback(overworld));
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

    private record RescueDestination(ServerWorld world, BlockPos pos) {
    }
}
