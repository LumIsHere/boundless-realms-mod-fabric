package com.boundless_realms.entity;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TicketInspectorEntity extends Villager {

    private static final ResourceKey<DamageType> TOO_HONEST_DAMAGE_TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "too_honest")
    );

    private enum InspectorState {
        IDLE,
        APPROACHING,
        WAITING_FOR_ANSWER,
        THINKING,
        LEAVING
    }

    private InspectorState inspectorState = InspectorState.IDLE;
    private Player targetPlayer;
    private ItemStack stolenTicket = ItemStack.EMPTY;
    private boolean stolenTicketIsFake = false;
    private boolean playerClaimedRealTicketWasFake = false;
    private boolean canTrigger = true;
    private int stateTimer = 0;

    public TicketInspectorEntity(EntityType<? extends Villager> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        Level world = this.level();
        if (world.isClientSide()) {
            return;
        }

        Player nearbyPlayer = world.getNearestPlayer(this, 10.0);

        if (nearbyPlayer == null) {
            canTrigger = true;

            if (inspectorState == InspectorState.LEAVING && this.getNavigation().isDone()) {
                resetState();
            }

            return;
        }

        switch (inspectorState) {
            case IDLE -> tickIdle(nearbyPlayer);
            case APPROACHING -> tickApproaching();
            case WAITING_FOR_ANSWER -> tickWaiting();
            case THINKING -> tickThinking();
            case LEAVING -> tickLeaving();
        }
    }

    private void tickIdle(Player nearbyPlayer) {
        if (!canTrigger) {
            return;
        }

        if (!hasLunchTicketInHand(nearbyPlayer)) {
            return;
        }

        targetPlayer = nearbyPlayer;
        canTrigger = false;
        inspectorState = InspectorState.APPROACHING;
        this.getNavigation().moveTo(targetPlayer, 1.25);
    }

    private void tickApproaching() {
        if (!isTargetValid()) {
            resetState();
            return;
        }

        this.getLookControl().setLookAt(targetPlayer, 30.0F, 30.0F);
        this.getNavigation().moveTo(targetPlayer, 1.25);

        if (this.distanceTo(targetPlayer) <= 2.2F) {
            stolenTicket = stealLunchTicket(targetPlayer);

            if (stolenTicket.isEmpty()) {
                targetPlayer.displayClientMessage(
                        Component.translatable("dialogue.boundless_realms.inspector.no_ticket"),
                        false
                );
                beginLeaving();
                return;
            }

            sendQuestion(targetPlayer);
            inspectorState = InspectorState.WAITING_FOR_ANSWER;
        }
    }

    private void tickWaiting() {
        if (!isTargetValid()) {
            resetState();
            return;
        }

        this.getLookControl().setLookAt(targetPlayer, 30.0F, 30.0F);
    }

    private void tickThinking() {
        stateTimer--;

        if (stateTimer <= 0) {
            resolveInspection();
            beginLeaving();
        }
    }

    private void tickLeaving() {
        stateTimer--;

        if (stateTimer <= 0 && this.getNavigation().isDone()) {
            if (this.level().getNearestPlayer(this, 10.0) == null) {
                resetState();
            }
        }
    }

    private boolean isTargetValid() {
        return targetPlayer != null && targetPlayer.isAlive();
    }

    private void sendQuestion(Player player) {
        player.displayClientMessage(
                Component.translatable("dialogue.boundless_realms.inspector.question"),
                false
        );

        Component yesButton = Component.translatable("dialogue.boundless_realms.inspector.answer_yes")
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/inspector_answer yes")
                ));

        Component noButton1 = Component.translatable("dialogue.boundless_realms.inspector.answer_no")
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/inspector_answer no")
                ));

        player.displayClientMessage(
                Component.empty().append(yesButton).append(Component.literal("  ")).append(noButton1),
                false
        );
    }

    public boolean isWaitingFor(Player player) {
        return inspectorState == InspectorState.WAITING_FOR_ANSWER
                && targetPlayer != null
                && targetPlayer.getUUID().equals(player.getUUID());
    }

    public void onPlayerAnsweredNo(Player player) {
        if (!isWaitingFor(player)) {
            return;
        }

        playerClaimedRealTicketWasFake = false;

        player.displayClientMessage(
                Component.translatable("dialogue.boundless_realms.inspector.thinking"),
                false
        );

        inspectorState = InspectorState.THINKING;
        stateTimer = 40;
    }

    public void onPlayerAnsweredYes(Player player) {
        if (!isWaitingFor(player)) {
            return;
        }

        if (stolenTicketIsFake) {
            player.displayClientMessage(Component.translatable("dialogue.boundless_realms.inspector.honest"), false);

            if (player.level() instanceof ServerLevel serverWorld) {
                player.hurtServer(serverWorld, createTooHonestDamageSource(serverWorld), Float.MAX_VALUE);
            }

            stolenTicket = ItemStack.EMPTY;
            stolenTicketIsFake = false;
            playerClaimedRealTicketWasFake = false;
            beginLeaving();
            return;
        }

        playerClaimedRealTicketWasFake = true;
        player.displayClientMessage(
                Component.translatable("dialogue.boundless_realms.inspector.self_doubt"),
                false
        );
        inspectorState = InspectorState.THINKING;
        stateTimer = 40;
    }

    private DamageSource createTooHonestDamageSource(ServerLevel world) {
        return new DamageSource(
                world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(TOO_HONEST_DAMAGE_TYPE.identifier()).orElseThrow(),
                this
        );
    }

    private void resolveInspection() {
        if (!isTargetValid()) {
            stolenTicket = ItemStack.EMPTY;
            stolenTicketIsFake = false;
            playerClaimedRealTicketWasFake = false;
            return;
        }

        if (playerClaimedRealTicketWasFake) {
            confiscateTicketAndWarn();
            playerClaimedRealTicketWasFake = false;
            return;
        }

        if (stolenTicketIsFake && this.getRandom().nextFloat() >= 0.2F) {
            targetPlayer.setHealth(1.0F);
            targetPlayer.displayClientMessage(
                    Component.translatable("dialogue.boundless_realms.inspector.fake"),
                    false
            );
            stolenTicket = ItemStack.EMPTY;
            stolenTicketIsFake = false;
            playerClaimedRealTicketWasFake = false;
            return;
        }

        returnTicket();
    }

    private void confiscateTicketAndWarn() {
        if (targetPlayer != null) {
            targetPlayer.displayClientMessage(
                    Component.translatable("dialogue.boundless_realms.inspector.warning"),
                    false
            );
        }

        stolenTicket = ItemStack.EMPTY;
        stolenTicketIsFake = false;
    }

    private void returnTicket() {
        Level world = this.level();

        if (stolenTicket.isEmpty()) {
            return;
        }

        ItemEntity itemEntity = new ItemEntity(
                world,
                this.getX(),
                this.getEyeY(),
                this.getZ(),
                stolenTicket.copy()
        );

        if (targetPlayer != null) {
            Vec3 from = new Vec3(this.getX(), this.getEyeY(), this.getZ());
            Vec3 to = new Vec3(targetPlayer.getX(), targetPlayer.getEyeY(), targetPlayer.getZ());
            Vec3 direction = to.subtract(from).normalize().scale(0.35);

            itemEntity.setDeltaMovement(direction.x, 0.25, direction.z);

            targetPlayer.displayClientMessage(
                    Component.translatable("dialogue.boundless_realms.inspector.return"),
                    false
            );
        }

        world.addFreshEntity(itemEntity);
        stolenTicket = ItemStack.EMPTY;
        stolenTicketIsFake = false;
    }

    private void beginLeaving() {
        inspectorState = InspectorState.LEAVING;
        stateTimer = 60;

        double awayX = this.getX() + 8.0;
        double awayZ = this.getZ() + 8.0;

        if (targetPlayer != null) {
            Vec3 fromPlayerToInspector = new Vec3(
                    this.getX() - targetPlayer.getX(),
                    0.0,
                    this.getZ() - targetPlayer.getZ()
            );

            if (fromPlayerToInspector.lengthSqr() > 0.0001) {
                Vec3 direction = fromPlayerToInspector.normalize().scale(10.0);
                awayX = this.getX() + direction.x;
                awayZ = this.getZ() + direction.z;
            }
        }

        this.getNavigation().moveTo(awayX, this.getY(), awayZ, 1.35);
    }

    private void resetState() {
        inspectorState = InspectorState.IDLE;
        targetPlayer = null;
        stolenTicket = ItemStack.EMPTY;
        stolenTicketIsFake = false;
        playerClaimedRealTicketWasFake = false;
        stateTimer = 0;
    }

    private boolean hasLunchTicketInHand(Player player) {
        return player.getMainHandItem().is(ModItems.LUNCH_TICKET)
                || player.getOffhandItem().is(ModItems.LUNCH_TICKET)
                || player.getMainHandItem().is(ModItems.FAKE_LUNCH_TICKET)
                || player.getOffhandItem().is(ModItems.FAKE_LUNCH_TICKET);
    }

    private ItemStack stealLunchTicket(Player player) {
        if (player.getMainHandItem().is(ModItems.LUNCH_TICKET)) {
            ItemStack stack = player.getMainHandItem().copy();
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = false;
            return stack;
        }

        if (player.getOffhandItem().is(ModItems.LUNCH_TICKET)) {
            ItemStack stack = player.getOffhandItem().copy();
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = false;
            return stack;
        }

        if (player.getMainHandItem().is(ModItems.FAKE_LUNCH_TICKET)) {
            ItemStack stack = player.getMainHandItem().copy();
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = true;
            return stack;
        }

        if (player.getOffhandItem().is(ModItems.FAKE_LUNCH_TICKET)) {
            ItemStack stack = player.getOffhandItem().copy();
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = true;
            return stack;
        }

        stolenTicketIsFake = false;
        return ItemStack.EMPTY;
    }
}
