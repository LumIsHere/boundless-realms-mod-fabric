package com.boundless_realms.mixin;

import com.boundless_realms.item.ModEnchantments;
import com.boundless_realms.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    public abstract int getLevelCost();

    protected AnvilScreenHandlerMixin() {
        super(null, 0, null, null, null);
    }

    @Inject(method = "updateResult", at = @At("TAIL"))
    private void boundlessRealms$restrictWitherFuryEnchantments(CallbackInfo ci) {
        ItemStack baseStack = this.input.getStack(0);
        ItemStack resultStack = this.output.getStack(0);
        if (resultStack.isEmpty() || baseStack.getItem() == ModItems.WITHER_FURY) {
            return;
        }

        if (ModEnchantments.hasWitherFuryExclusiveEnchantments(this.player.getEntityWorld(), resultStack)) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.sendContentUpdates();
        }
    }
}
