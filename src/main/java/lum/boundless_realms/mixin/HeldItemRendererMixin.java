package lum.boundless_realms.mixin;

import lum.boundless_realms.item.BedrockHandHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ItemStack mainHand;

    @Shadow
    private float equipProgressMainHand;

    @Shadow
    private float lastEquipProgressMainHand;

    @Inject(method = "updateHeldItems", at = @At("TAIL"))
    private void boundlessRealms$keepMainHandVisibleWithBedrockHand(CallbackInfo ci) {
        PlayerEntity player = this.client.player;
        if (!(player instanceof BedrockHandHolder holder) || !holder.boundlessRealms$hasBedrockHandEquipped()) {
            return;
        }

        // Keep the first-person main hand fully equipped so rapid attacks do not constantly
        // restart the equip animation and make the hand appear to flicker or disappear.
        this.mainHand = player.getMainHandStack();
        this.equipProgressMainHand = 1.0F;
        this.lastEquipProgressMainHand = 1.0F;
    }
}
