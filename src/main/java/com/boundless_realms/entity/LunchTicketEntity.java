package com.boundless_realms.entity;

import com.boundless_realms.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class LunchTicketEntity extends ThrownItemEntity {

    public LunchTicketEntity(EntityType<? extends LunchTicketEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.LUNCH_TICKET;
    }

    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        super.onEntityHit(hitResult);

        World world = this.getEntityWorld();
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        Entity target = hitResult.getEntity();

        if (target instanceof LivingEntity living) {
            living.damage(serverWorld, this.getDamageSources().thrown(this, this.getOwner()), 88.0F);
        }

        this.discard();
    }
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (!this.getEntityWorld().isClient()) {
            this.discard();
        }
    }
}

