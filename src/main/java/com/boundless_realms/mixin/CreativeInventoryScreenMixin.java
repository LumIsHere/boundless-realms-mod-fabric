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

import java.lang.reflect.Constructor;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {

    @Unique
    private static Constructor<?> boundlessRealms$creativeSlotConstructor;

    @Inject(method = "setSelectedTab", at = @At("TAIL"))
    private void boundlessRealms$repositionBedrockHandSlot(ItemGroup group, CallbackInfo ci) {
        if (group.getType() != ItemGroup.Type.INVENTORY) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (!(player instanceof BedrockHandHolder holder)) {
            return;
        }

        int bedrockHandSlotId = boundlessRealms$findBedrockHandSlotId(player, holder);
        if (bedrockHandSlotId < 0) {
            return;
        }

        Slot wrappedSlot = player.playerScreenHandler.getSlot(bedrockHandSlotId);
        ((ScreenHandlerAccessor) ((HandledScreenAccessor) this).boundlessRealms$getHandler()).boundlessRealms$getSlots().set(
                bedrockHandSlotId,
                boundlessRealms$createCreativeSlot(wrappedSlot, bedrockHandSlotId, 35, 47)
        );
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

    @Unique
    private static Slot boundlessRealms$createCreativeSlot(Slot slot, int id, int x, int y) {
        try {
            if (boundlessRealms$creativeSlotConstructor == null) {
                Class<?> creativeSlotClass = Class.forName(
                        "net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen$CreativeSlot"
                );
                boundlessRealms$creativeSlotConstructor = creativeSlotClass.getDeclaredConstructor(
                        Slot.class,
                        int.class,
                        int.class,
                        int.class
                );
                boundlessRealms$creativeSlotConstructor.setAccessible(true);
            }

            return (Slot) boundlessRealms$creativeSlotConstructor.newInstance(slot, id, x, y);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("Failed to rebuild the Bedrock Hand creative slot", exception);
        }
    }
}
