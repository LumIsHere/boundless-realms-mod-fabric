package com.boundless_realms.item;

import com.boundless_realms.entity.LunchTicketEntity;
import com.boundless_realms.entity.ModEntities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.util.collection.DefaultedList;

public class LunchTicketItem extends Item {
    private static final int COST = 44;

    public LunchTicketItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.getAbilities().creativeMode && countTotalMoney(user) < COST) {
            if (!world.isClient()) {
                user.sendMessage(Text.translatable("notification.lunch_ticket.no_enough_money", COST), true);
            }
            return ActionResult.FAIL;
        }
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;

        int useTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float power = getPullProgress(useTicks);

        if (power < 0.1F) return false;

        boolean isCreative = player.getAbilities().creativeMode;

        if (!isCreative && countTotalMoney(player) < COST) {
            return false;
        }

        if (!world.isClient()) {
            if (!isCreative) {
                removeTotalMoney(player, COST);
            }

            LunchTicketEntity projectile = new LunchTicketEntity(ModEntities.LUNCH_TICKET, world);
            projectile.setOwner(player);
            projectile.setPosition(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, power * 3.0F, 1.0F);
            world.spawnEntity(projectile);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.5F, 1.0F);
        return true;
    }

    private int countTotalMoney(PlayerEntity player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (stack.isOf(ModItems.MONEY)) {
                total += stack.getCount();
            } else if (stack.getItem() instanceof WalletItem) {
                ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
                if (container != null) {
                    total += container.stream()
                            .filter(item -> item.isOf(ModItems.MONEY))
                            .mapToInt(ItemStack::getCount)
                            .sum();
                }
            }
        }
        return total;
    }

    private void removeTotalMoney(PlayerEntity player, int amount) {
        int remaining = amount;

        for (int i = 0; i < player.getInventory().size(); i++) {
            if (remaining <= 0) break;
            ItemStack stack = player.getInventory().getStack(i);

            if (stack.isOf(ModItems.MONEY)) {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.decrement(toRemove);
                remaining -= toRemove;
            }
            else if (stack.getItem() instanceof WalletItem) {
                ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
                if (container != null) {
                    int size = (int) container.stream().count();
                    DefaultedList<ItemStack> contents = DefaultedList.ofSize(size, ItemStack.EMPTY);

                    container.copyTo(contents);

                    boolean changed = false;
                    for (ItemStack innerStack : contents) {
                        if (remaining <= 0) break;
                        if (innerStack.isOf(ModItems.MONEY) && !innerStack.isEmpty()) {
                            int toRemove = Math.min(remaining, innerStack.getCount());
                            innerStack.decrement(toRemove);
                            remaining -= toRemove;
                            changed = true;
                        }
                    }

                    if (changed) {
                        stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(contents));
                    }
                }
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) { return 72000; }
    @Override
    public UseAction getUseAction(ItemStack stack) { return UseAction.BOW; }
    private static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }
}
