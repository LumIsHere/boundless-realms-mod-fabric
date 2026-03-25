package com.boundless_realms.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WitherFuryItem extends Item {
    private static final double MAX_DISTANCE = 100.0;
    private static final double SKULL_HEIGHT_OFFSET = 3.0;
    private static final int BASE_COOLDOWN_TICKS = 20;
    private static final int FURIOUS_COOLDOWN_REDUCTION_PER_LEVEL = 4;
    private static final float BASE_EXPLOSION_RADIUS = 1.0f;
    private static final float VIOLENT_RADIUS_BONUS_PER_LEVEL = 1.0f;
    private static final float VIOLENT_DAMAGE_BONUS_PER_LEVEL = 2.0f;

    public WitherFuryItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            ItemStack stack = user.getStackInHand(hand);
            int furiousLevel = ModEnchantments.getLevel(world, stack, ModEnchantments.FURIOUS);
            int violentLevel = ModEnchantments.getLevel(world, stack, ModEnchantments.VIOLENT);
            BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.NONE, MAX_DISTANCE);
            Vec3d targetPos;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                targetPos = hitResult.getPos();
            } else {
                targetPos = user.getEyePos().add(user.getRotationVector().multiply(MAX_DISTANCE));
            }
            Vec3d spawnPos = new Vec3d(targetPos.x, targetPos.y + SKULL_HEIGHT_OFFSET, targetPos.z);

            WitherSkullEntity witherSkull = new WitherSkullEntity(EntityType.WITHER_SKULL, world) {
                @Override
                protected void onCollision(HitResult hitResult) {
                    if (hitResult.getType() == HitResult.Type.ENTITY) {
                        net.minecraft.entity.Entity hitEntity = ((net.minecraft.util.hit.EntityHitResult) hitResult).getEntity();
                        if (hitEntity.equals(this.getOwner()) || hitEntity instanceof ItemEntity || hitEntity instanceof ExperienceOrbEntity) {
                            return;
                        }
                        this.onEntityHit((net.minecraft.util.hit.EntityHitResult) hitResult);
                    } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                        this.onBlockHit((BlockHitResult) hitResult);
                    }

                    if (!this.getEntityWorld().isClient()) {
                        Vec3d pos = this.getEntityPos();
                        float radius = BASE_EXPLOSION_RADIUS + violentLevel * VIOLENT_RADIUS_BONUS_PER_LEVEL;
                        this.getEntityWorld().createExplosion(this, null, new net.minecraft.world.explosion.ExplosionBehavior() {
                            @Override
                            public boolean canDestroyBlock(net.minecraft.world.explosion.Explosion explosion, net.minecraft.world.BlockView world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state, float power) {
                                return false;
                            }
                            @Override
                            public boolean shouldDamage(net.minecraft.world.explosion.Explosion explosion, net.minecraft.entity.Entity entity) {
                                if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity) return false;
                                return !entity.equals(getOwner());
                            }

                            @Override
                            public float getKnockbackModifier(net.minecraft.entity.Entity entity) {
                                return 0.0f;
                            }
                        }, pos.x, pos.y, pos.z, radius, false, World.ExplosionSourceType.NONE);

                        if (violentLevel > 0 && this.getEntityWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                            float bonusDamage = violentLevel * VIOLENT_DAMAGE_BONUS_PER_LEVEL;
                            Entity owner = this.getOwner();
                            Box damageBox = Box.of(pos, radius * 2.0, radius * 2.0, radius * 2.0);

                            for (Entity entity : serverWorld.getOtherEntities(this, damageBox, entity ->
                                    entity instanceof LivingEntity
                                            && entity.isAlive()
                                            && !entity.equals(owner))) {
                                double deltaX = entity.getX() - pos.x;
                                double deltaY = entity.getY() - pos.y;
                                double deltaZ = entity.getZ() - pos.z;
                                if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ <= radius * radius) {
                                    entity.damage(serverWorld, this.getDamageSources().witherSkull(this, owner), bonusDamage);
                                }
                            }
                        }

                        this.discard();
                    }
                }
            };

            witherSkull.setOwner(user);
            witherSkull.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            witherSkull.setVelocity(targetPos.subtract(spawnPos).normalize().multiply(3));
            witherSkull.setCharged(true);
            world.spawnEntity(witherSkull);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            int cooldownTicks = Math.max(0, BASE_COOLDOWN_TICKS - furiousLevel * FURIOUS_COOLDOWN_REDUCTION_PER_LEVEL);
            if (cooldownTicks > 0) {
                user.getItemCooldownManager().set(stack, cooldownTicks);
            }
        }
        return ActionResult.SUCCESS;
    }

    private BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling, double maxDistance) {
        Vec3d startPos = player.getEyePos();
        Vec3d endPos = startPos.add(player.getRotationVector().multiply(maxDistance));
        return world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
    }
}
