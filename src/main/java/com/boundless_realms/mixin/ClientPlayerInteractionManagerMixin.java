package lum.boundless_realms.mixin;

import lum.boundless_realms.item.BedrockHandHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    private void boundlessRealms$removeClientAttackLimit(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = this.client.player;
        if (player instanceof BedrockHandHolder holder && holder.boundlessRealms$hasBedrockHandEquipped()) {
            cir.setReturnValue(false);
        }
    }
}
