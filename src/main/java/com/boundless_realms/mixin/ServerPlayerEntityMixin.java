package com.boundless_realms.mixin;

import com.boundless_realms.item.BedrockHandHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void boundlessRealms$copyBedrockHandSlot(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (!((Object) this instanceof BedrockHandHolder) || !(oldPlayer instanceof BedrockHandHolder)) {
            return;
        }

        BedrockHandHolder newHolder = (BedrockHandHolder) (Object) this;
        BedrockHandHolder oldHolder = (BedrockHandHolder) oldPlayer;
        ServerWorld oldWorld = (ServerWorld) oldPlayer.getEntityWorld();
        boolean keepInventory = alive
                || Boolean.TRUE.equals(oldWorld.getGameRules().getValue(GameRules.KEEP_INVENTORY))
                || oldPlayer.isSpectator();

        ItemStack stack = keepInventory
                ? oldHolder.boundlessRealms$getBedrockHandInventory().getStack(0).copy()
                : ItemStack.EMPTY;
        newHolder.boundlessRealms$getBedrockHandInventory().setStack(0, stack);
    }
}
