package com.boundless_realms.mixin;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.item.BedrockHandHolder;
import com.boundless_realms.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRules;
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
    private static final Identifier BEDROCK_HAND_ATTACK_SPEED_MODIFIER_ID = Identifier.of(
            BoundlessRealmsMod.MOD_ID,
            "bedrock_hand_attack_speed"
    );
    @Unique
    private static final EntityAttributeModifier BEDROCK_HAND_ATTACK_SPEED_MODIFIER = new EntityAttributeModifier(
            BEDROCK_HAND_ATTACK_SPEED_MODIFIER_ID,
            1020.0,
            EntityAttributeModifier.Operation.ADD_VALUE
    );

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

    @Inject(method = "tick", at = @At("TAIL"))
    private void boundlessRealms$syncBedrockHandAttackSpeed(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        EntityAttributeInstance attackSpeed = player.getAttributeInstance(EntityAttributes.ATTACK_SPEED);
        if (attackSpeed == null) {
            return;
        }

        if (boundlessRealms$hasBedrockHandEquipped() && !boundlessRealms$shouldUseWeaponCooldown(player.getMainHandStack())) {
            if (!attackSpeed.hasModifier(BEDROCK_HAND_ATTACK_SPEED_MODIFIER_ID)) {
                attackSpeed.addTemporaryModifier(BEDROCK_HAND_ATTACK_SPEED_MODIFIER);
            }
            return;
        }

        attackSpeed.removeModifier(BEDROCK_HAND_ATTACK_SPEED_MODIFIER_ID);
    }

    @Inject(method = "dropInventory", at = @At("TAIL"))
    private void boundlessRealms$dropBedrockHandInventory(ServerWorld world, CallbackInfo ci) {
        ItemStack stack = boundlessRealms$bedrockHandInventory.getStack(0);
        if (stack.isEmpty() || Boolean.TRUE.equals(world.getGameRules().getValue(GameRules.KEEP_INVENTORY))) {
            return;
        }

        ((PlayerEntity) (Object) this).dropStack(world, stack.copy());
        boundlessRealms$bedrockHandInventory.setStack(0, ItemStack.EMPTY);
    }

    @Unique
    private static boolean boundlessRealms$shouldUseWeaponCooldown(ItemStack stack) {
        return stack.get(DataComponentTypes.PIERCING_WEAPON) != null;
    }

    @Inject(method = "canUseSweepAttack", at = @At("HEAD"), cancellable = true)
    private void boundlessRealms$disableSweepWithBedrockHand(boolean critical, boolean sprinting, boolean hasKnockback, CallbackInfoReturnable<Boolean> cir) {
        if (boundlessRealms$hasBedrockHandEquipped()) {
            cir.setReturnValue(false);
        }
    }
}
