package com.boundless_realms.mixin;

import com.boundless_realms.item.EmeraldGearEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin {
    private static final float EMERALD_SET_DISCOUNT_MULTIPLIER = 0.20F;

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void applyEmeraldSetDiscount(Player player, CallbackInfo ci) {
        if (!EmeraldGearEffects.hasFullEmeraldSet(player)) {
            return;
        }

        Villager villager = (Villager) (Object) this;
        for (MerchantOffer offer : villager.getOffers()) {
            int discount = Math.max(
                    1,
                    Mth.floor(offer.getBaseCostA().getCount() * EMERALD_SET_DISCOUNT_MULTIPLIER)
            );
            offer.addToSpecialPriceDiff(-discount);
        }
    }
}
