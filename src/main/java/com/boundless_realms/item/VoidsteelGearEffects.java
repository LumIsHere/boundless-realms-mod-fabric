package com.boundless_realms.item;

import com.boundless_realms.BoundlessRealmsMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class VoidsteelGearEffects {
    private static final int VOID_RESCUE_TRIGGER_DEPTH = 64;
    private static final int VOID_RESCUE_SEARCH_RADIUS = 1024;

    private VoidsteelGearEffects() {
    }

    public static void register() {
        ServerTickEvents.END_LEVEL_TICK.register(VoidsteelGearEffects::onEndWorldTick);
    }

    @SuppressWarnings("resource")
    public static void tryRescuePlayerFromVoid(ServerPlayer player) {
        if (player.isSpectator() || !player.isAlive() || !hasFullVoidsteelSet(player)) {
            BoundlessRealmsMod.LOGGER.info(
                    "Voidsteel rescue skipped for {} in {}: spectator={}, alive={}, fullSet={}",
                    player.getName().getString(),
                    player.level().dimension().identifier(),
                    player.isSpectator(),
                    player.isAlive(),
                    hasFullVoidsteelSet(player)
            );
            return;
        }

        ServerLevel world = player.level();
        RescueDestination destination = findRescueDestination(player, world);
        ServerLevel destinationWorld = destination.world();
        BoundlessRealmsMod.LOGGER.info(
                "Voidsteel rescue triggered for {} from {} at y={} to {} {}",
                player.getName().getString(),
                world.dimension().identifier(),
                player.getY(),
                destinationWorld.dimension().identifier(),
                destination.pos()
        );
        player.teleportTo(
                destinationWorld,
                destination.pos().getX() + 0.5,
                destination.pos().getY(),
                destination.pos().getZ() + 0.5,
                Set.of(),
                player.getYRot(),
                player.getXRot(),
                true
        );
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        destinationWorld.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 0.5, player.getZ(), 32, 0.4, 0.6, 0.4, 0.05);
    }

    private static void onEndWorldTick(ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            if (!hasFullVoidsteelSet(player)) {
                continue;
            }

            tryRescueFromVoid(player, world);
        }
    }

    private static boolean hasFullVoidsteelSet(ServerPlayer player) {
        return player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(ModItems.VOIDSTEEL_HELMET)
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(ModItems.VOIDSTEEL_CHESTPLATE)
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).is(ModItems.VOIDSTEEL_LEGGINGS)
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).is(ModItems.VOIDSTEEL_BOOTS);
    }

    private static void tryRescueFromVoid(ServerPlayer player, ServerLevel world) {
        if (player.isSpectator() || !player.isAlive()) {
            return;
        }

        if (player.getY() > world.getMinY() - VOID_RESCUE_TRIGGER_DEPTH) {
            return;
        }

        BoundlessRealmsMod.LOGGER.info(
                "Voidsteel void threshold reached for {} in {}: y={}, triggerY={}, fullSet={}",
                player.getName().getString(),
                world.dimension().identifier(),
                player.getY(),
                world.getMinY() - VOID_RESCUE_TRIGGER_DEPTH,
                hasFullVoidsteelSet(player)
        );
        tryRescuePlayerFromVoid(player);
    }

    private static BlockPos findNearestLand(ServerLevel world, BlockPos origin) {
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
    private static RescueDestination findRescueDestination(ServerPlayer player, ServerLevel currentWorld) {
        BlockPos nearestLand = findNearestLand(currentWorld, player.blockPosition());
        if (nearestLand != null) {
            BoundlessRealmsMod.LOGGER.info(
                    "Voidsteel rescue found nearby land for {} in {} at {}",
                    player.getName().getString(),
                    currentWorld.dimension().identifier(),
                    nearestLand
            );
            return new RescueDestination(currentWorld, nearestLand);
        }

        ServerLevel overworld = Objects.requireNonNull(
                currentWorld.getServer().getLevel(Level.OVERWORLD),
                "Overworld must be present for void rescue fallback"
        );
        BoundlessRealmsMod.LOGGER.info(
                "Voidsteel rescue falling back to overworld spawn for {} from {}",
                player.getName().getString(),
                currentWorld.dimension().identifier()
        );
        return new RescueDestination(overworld, getSpawnFallback(overworld));
    }

    private static BlockPos getSpawnFallback(ServerLevel world) {
        BlockPos spawnPos = world.getRespawnData().pos();
        BlockPos topPos = getSafeLandingPos(world, spawnPos.getX(), spawnPos.getZ());
        return topPos != null ? topPos : spawnPos.above();
    }

    private static BlockPos getSafeLandingPos(ServerLevel world, int x, int z) {
        int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (topY <= world.getMinY()) {
            return null;
        }

        BlockPos feetPos = new BlockPos(x, topY, z);
        BlockPos headPos = feetPos.above();
        BlockPos floorPos = feetPos.below();

        BlockState feetState = world.getBlockState(feetPos);
        BlockState headState = world.getBlockState(headPos);
        BlockState floorState = world.getBlockState(floorPos);

        if (feetState.isAir() && headState.isAir() && floorState.isRedstoneConductor(world, floorPos)) {
            return feetPos;
        }

        return null;
    }

    private record RescueDestination(ServerLevel world, BlockPos pos) {
    }
}
