package com.boundless_realms.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WitherFuryItem extends Item {
    private static final double MAX_DISTANCE = 100.0;
    private static final double SKULL_HEIGHT_OFFSET = 3.0;

    public WitherFuryItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
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
                        float radius = 1.0f;
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

            user.getItemCooldownManager().set(user.getStackInHand(hand), 10);
        }
        return ActionResult.SUCCESS;
    }

    private BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling, double maxDistance) {
        Vec3d startPos = player.getEyePos();
        Vec3d endPos = startPos.add(player.getRotationVector().multiply(maxDistance));
        return world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
    }
}