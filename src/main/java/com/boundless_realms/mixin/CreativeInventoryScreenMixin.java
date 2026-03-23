package com.boundless_realms.mixin;

import com.boundless_realms.item.BedrockHandHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {

    @Inject(method = "setSelectedTab", at = @At("TAIL"))
    private void boundlessRealms$repositionBedrockHandSlot(ItemGroup group, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (!(player instanceof BedrockHandHolder holder)) {
            return;
        }

        int bedrockHandSlotId = boundlessRealms$findBedrockHandSlotId(player, holder);
        if (bedrockHandSlotId < 0) {
            return;
        }

        Slot creativeSlot = ((ScreenHandlerAccessor) ((HandledScreenAccessor) this).boundlessRealms$getHandler())
                .boundlessRealms$getSlots()
                .get(bedrockHandSlotId);
        SlotAccessor creativeSlotAccessor = (SlotAccessor) creativeSlot;

        if (group.getType() == ItemGroup.Type.INVENTORY) {
            creativeSlotAccessor.boundlessRealms$setX(35);
            creativeSlotAccessor.boundlessRealms$setY(47);
        } else {
            // Hide the custom slot outside the inventory tab, matching vanilla's hidden armor/crafting slots behavior.
            creativeSlotAccessor.boundlessRealms$setX(-2000);
            creativeSlotAccessor.boundlessRealms$setY(-2000);
        }
    }

    @Unique
    private static int boundlessRealms$findBedrockHandSlotId(PlayerEntity player, BedrockHandHolder holder) {
        for (int slotId = 0; slotId < player.playerScreenHandler.getStacks().size(); slotId++) {
            Slot slot = player.playerScreenHandler.getSlot(slotId);
            if (slot.inventory == holder.boundlessRealms$getBedrockHandInventory() && slot.getIndex() == 0) {
                return slotId;
            }
        }

        return -1;
    }
}
