package com.boundless_realms.entity;

import com.boundless_realms.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class LunchTicketEntity extends ThrowableItemProjectile {

    public LunchTicketEntity(EntityType<? extends LunchTicketEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.LUNCH_TICKET;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        Level world = this.level();
        if (!(world instanceof ServerLevel serverWorld)) {
            return;
        }

        Entity target = hitResult.getEntity();

        if (target instanceof LivingEntity living) {
            living.hurtServer(serverWorld, this.damageSources().thrown(this, this.getOwner()), 88.0F);
        }

        this.discard();
    }
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);

        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}

