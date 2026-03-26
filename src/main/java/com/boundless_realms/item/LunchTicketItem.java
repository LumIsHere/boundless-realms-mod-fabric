package com.boundless_realms.item;

import com.boundless_realms.entity.LunchTicketEntity;
import com.boundless_realms.entity.ModEntities;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

public class LunchTicketItem extends Item {
    private static final int COST = 44;
    private static final int USE_COOLDOWN_TICKS = 15 * 20;

    public LunchTicketItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!user.getAbilities().instabuild && countTotalMoney(user) < COST) {
            if (!world.isClientSide()) {
                user.sendOverlayMessage(Component.translatable("notification.lunch_ticket.no_enough_money", COST));
            }
            return InteractionResult.FAIL;
        }
        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof Player player)) return false;

        int useTicks = this.getUseDuration(stack, user) - remainingUseTicks;
        float power = getPullProgress(useTicks);

        if (power < 0.1F) return false;

        boolean isCreative = player.getAbilities().instabuild;

        if (!isCreative && countTotalMoney(player) < COST) {
            return false;
        }

        if (!world.isClientSide()) {
            if (!isCreative) {
                removeTotalMoney(player, COST);
            }

            LunchTicketEntity projectile = new LunchTicketEntity(ModEntities.LUNCH_TICKET, world);
            projectile.setOwner(player);
            projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
            world.addFreshEntity(projectile);
        }

        player.getCooldowns().addCooldown(stack, USE_COOLDOWN_TICKS);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 1.0F);
        return true;
    }

    private int countTotalMoney(Player player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.is(ModItems.MONEY)) {
                total += stack.getCount();
            } else if (stack.getItem() instanceof WalletItem) {
                ItemContainerContents container = stack.get(DataComponents.CONTAINER);
                if (container != null) {
                    total += container.nonEmptyItemCopyStream()
                            .filter(item -> item.is(ModItems.MONEY))
                            .mapToInt(ItemStack::getCount)
                            .sum();
                }
            }
        }
        return total;
    }

    private void removeTotalMoney(Player player, int amount) {
        int remaining = amount;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (remaining <= 0) break;
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.is(ModItems.MONEY)) {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.shrink(toRemove);
                remaining -= toRemove;
            }
            else if (stack.getItem() instanceof WalletItem) {
                ItemContainerContents container = stack.get(DataComponents.CONTAINER);
                if (container != null) {
                    NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);

                    container.copyInto(contents);

                    boolean changed = false;
                    for (ItemStack innerStack : contents) {
                        if (remaining <= 0) break;
                        if (innerStack.is(ModItems.MONEY) && !innerStack.isEmpty()) {
                            int toRemove = Math.min(remaining, innerStack.getCount());
                            innerStack.shrink(toRemove);
                            remaining -= toRemove;
                            changed = true;
                        }
                    }

                    if (changed) {
                        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
                    }
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
    private static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }
}
