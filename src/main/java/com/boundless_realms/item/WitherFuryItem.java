package com.boundless_realms.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WitherFuryItem extends Item {
    private static final double MAX_DISTANCE = 100.0;
    private static final double SKULL_HEIGHT_OFFSET = 3.0;
    private static final int BASE_COOLDOWN_TICKS = 20;
    private static final int FURIOUS_COOLDOWN_REDUCTION_PER_LEVEL = 4;
    private static final float BASE_EXPLOSION_RADIUS = 1.0f;
    private static final float VIOLENT_RADIUS_BONUS_PER_LEVEL = 1.0f;
    private static final float VIOLENT_DAMAGE_BONUS_PER_LEVEL = 2.0f;

    public WitherFuryItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide()) {
            ItemStack stack = user.getItemInHand(hand);
            int furiousLevel = ModEnchantments.getLevel(world, stack, ModEnchantments.FURIOUS);
            int violentLevel = ModEnchantments.getLevel(world, stack, ModEnchantments.VIOLENT);
            BlockHitResult hitResult = raycast(world, user, ClipContext.Fluid.NONE, MAX_DISTANCE);
            Vec3 targetPos;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                targetPos = hitResult.getLocation();
            } else {
                targetPos = user.getEyePosition().add(user.getLookAngle().scale(MAX_DISTANCE));
            }
            Vec3 spawnPos = new Vec3(targetPos.x, targetPos.y + SKULL_HEIGHT_OFFSET, targetPos.z);

            WitherSkull witherSkull = new WitherSkull(EntityType.WITHER_SKULL, world) {
                @Override
                protected void onHit(HitResult hitResult) {
                    if (hitResult.getType() == HitResult.Type.ENTITY) {
                        net.minecraft.world.entity.Entity hitEntity = ((net.minecraft.world.phys.EntityHitResult) hitResult).getEntity();
                        if (hitEntity.equals(this.getOwner()) || hitEntity instanceof ItemEntity || hitEntity instanceof ExperienceOrb) {
                            return;
                        }
                        this.onHitEntity((net.minecraft.world.phys.EntityHitResult) hitResult);
                    } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                        this.onHitBlock((BlockHitResult) hitResult);
                    }

                    if (!this.level().isClientSide()) {
                        Vec3 pos = this.position();
                        float radius = BASE_EXPLOSION_RADIUS + violentLevel * VIOLENT_RADIUS_BONUS_PER_LEVEL;
                        this.level().explode(this, null, new net.minecraft.world.level.ExplosionDamageCalculator() {
                            @Override
                            public boolean shouldBlockExplode(net.minecraft.world.level.Explosion explosion, net.minecraft.world.level.BlockGetter world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, float power) {
                                return false;
                            }
                            @Override
                            public boolean shouldDamageEntity(net.minecraft.world.level.Explosion explosion, net.minecraft.world.entity.Entity entity) {
                                if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) return false;
                                return !entity.equals(getOwner());
                            }

                            @Override
                            public float getKnockbackMultiplier(net.minecraft.world.entity.Entity entity) {
                                return 0.0f;
                            }
                        }, pos.x, pos.y, pos.z, radius, false, Level.ExplosionInteraction.NONE);

                        if (violentLevel > 0 && this.level() instanceof net.minecraft.server.level.ServerLevel serverWorld) {
                            float bonusDamage = violentLevel * VIOLENT_DAMAGE_BONUS_PER_LEVEL;
                            Entity owner = this.getOwner();
                            AABB damageBox = AABB.ofSize(pos, radius * 2.0, radius * 2.0, radius * 2.0);

                            for (Entity entity : serverWorld.getEntities(this, damageBox, entity ->
                                    entity instanceof LivingEntity
                                            && entity.isAlive()
                                            && !entity.equals(owner))) {
                                double deltaX = entity.getX() - pos.x;
                                double deltaY = entity.getY() - pos.y;
                                double deltaZ = entity.getZ() - pos.z;
                                if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ <= radius * radius) {
                                    entity.hurtServer(serverWorld, this.damageSources().witherSkull(this, owner), bonusDamage);
                                }
                            }
                        }

                        this.discard();
                    }
                }
            };

            witherSkull.setOwner(user);
            witherSkull.setPosRaw(spawnPos.x, spawnPos.y, spawnPos.z);
            witherSkull.setDeltaMovement(targetPos.subtract(spawnPos).normalize().scale(3));
            witherSkull.setDangerous(true);
            world.addFreshEntity(witherSkull);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);

            int cooldownTicks = Math.max(0, BASE_COOLDOWN_TICKS - furiousLevel * FURIOUS_COOLDOWN_REDUCTION_PER_LEVEL);
            if (cooldownTicks > 0) {
                user.getCooldowns().addCooldown(stack, cooldownTicks);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private BlockHitResult raycast(Level world, Player player, ClipContext.Fluid fluidHandling, double maxDistance) {
        Vec3 startPos = player.getEyePosition();
        Vec3 endPos = startPos.add(player.getLookAngle().scale(maxDistance));
        return world.clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, fluidHandling, player));
    }
}
