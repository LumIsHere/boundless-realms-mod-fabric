package com.boundless_realms.mixin;

import com.boundless_realms.item.EmeraldGearEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    private static final float EMERALD_SET_DISCOUNT_MULTIPLIER = 0.20F;

    @Inject(method = "prepareOffersFor", at = @At("TAIL"))
    private void applyEmeraldSetDiscount(PlayerEntity player, CallbackInfo ci) {
        if (!EmeraldGearEffects.hasFullEmeraldSet(player)) {
            return;
        }

        VillagerEntity villager = (VillagerEntity) (Object) this;
        for (TradeOffer offer : villager.getOffers()) {
            int discount = Math.max(
                    1,
                    MathHelper.floor(offer.getOriginalFirstBuyItem().getCount() * EMERALD_SET_DISCOUNT_MULTIPLIER)
            );
            offer.increaseSpecialPrice(-discount);
        }
    }
}
