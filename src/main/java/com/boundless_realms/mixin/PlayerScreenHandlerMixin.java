package com.boundless_realms.mixin;

import com.boundless_realms.item.BedrockHandHolder;
import com.boundless_realms.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin {

    private static final Identifier BEDROCK_HAND_EMPTY_SLOT_TEXTURE = PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void boundlessRealms$addBedrockHandSlot(
            PlayerInventory inventory,
            boolean onServer,
            PlayerEntity owner,
            CallbackInfo ci
    ) {
        if (!(owner instanceof BedrockHandHolder holder)) {
            return;
        }

        Inventory bedrockHandInventory = holder.boundlessRealms$getBedrockHandInventory();

        ((ScreenHandlerAccessor) this).boundlessRealms$invokeAddSlot(new Slot(bedrockHandInventory, 0, -10, 26) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.BEDROCK_HAND);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public Identifier getBackgroundSprite() {
                return BEDROCK_HAND_EMPTY_SLOT_TEXTURE;
            }
        });
    }
}
