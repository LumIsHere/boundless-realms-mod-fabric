package com.boundless_realms.mixin;

import com.boundless_realms.item.ModEnchantments;
import com.boundless_realms.item.ModItems;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {
    @Shadow
    public abstract int getCost();

    protected AnvilScreenHandlerMixin() {
        super(null, 0, null, null, null);
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void boundlessRealms$restrictWitherFuryEnchantments(CallbackInfo ci) {
        ItemStack baseStack = this.inputSlots.getItem(0);
        ItemStack resultStack = this.resultSlots.getItem(0);
        if (resultStack.isEmpty() || baseStack.getItem() == ModItems.WITHER_FURY) {
            return;
        }

        if (ModEnchantments.hasWitherFuryExclusiveEnchantments(this.player.level(), resultStack)) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
        }
    }
}
