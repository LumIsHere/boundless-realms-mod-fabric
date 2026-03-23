package com.boundless_realms.mixin;

import com.boundless_realms.item.BedrockHandHolder;
import com.boundless_realms.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements BedrockHandHolder {

    @Unique
    private static final String BEDROCK_HAND_SLOT_KEY = "BoundlessRealmsBedrockHandSlot";

    @Unique
    private final SimpleInventory boundlessRealms$bedrockHandInventory = new SimpleInventory(1);

    @Override
    public Inventory boundlessRealms$getBedrockHandInventory() {
        return boundlessRealms$bedrockHandInventory;
    }

    @Override
    public boolean boundlessRealms$hasBedrockHandEquipped() {
        return boundlessRealms$bedrockHandInventory.getStack(0).isOf(ModItems.BEDROCK_HAND);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void boundlessRealms$writeBedrockHandSlot(
            WriteView view,
            CallbackInfo ci
    ) {
        ItemStack stack = boundlessRealms$bedrockHandInventory.getStack(0);
        if (!stack.isEmpty()) {
            view.put(BEDROCK_HAND_SLOT_KEY, ItemStack.OPTIONAL_CODEC, stack);
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void boundlessRealms$readBedrockHandSlot(
            ReadView view,
            CallbackInfo ci
    ) {
        boundlessRealms$bedrockHandInventory.setStack(
                0,
                view.read(BEDROCK_HAND_SLOT_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY)
        );
    }

    @Inject(method = "getAttackCooldownProgressPerTick", at = @At("HEAD"), cancellable = true)
    private void boundlessRealms$removeAttackCooldownWithBedrockHand(CallbackInfoReturnable<Float> cir) {
        if (boundlessRealms$hasBedrockHandEquipped()) {
            // A very large value makes the player's attack cooldown refill almost instantly.
            cir.setReturnValue(1024.0F);
        }
    }

    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void boundlessRealms$forceFullStrengthAttacksWithBedrockHand(
            float baseTime,
            CallbackInfoReturnable<Float> cir
    ) {
        if (boundlessRealms$hasBedrockHandEquipped()) {
            // Returning 1.0 here makes every hit count as a fully charged attack.
            cir.setReturnValue(1.0F);
        }
    }
}
