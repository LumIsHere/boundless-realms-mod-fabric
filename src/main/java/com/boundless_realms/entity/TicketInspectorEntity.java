package com.boundless_realms.entity;

import com.boundless_realms.BoundlessRealmsMod;
import com.boundless_realms.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TicketInspectorEntity extends VillagerEntity {

    private static final RegistryKey<DamageType> TOO_HONEST_DAMAGE_TYPE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,
            Identifier.of(BoundlessRealmsMod.MOD_ID, "too_honest")
    );

    private enum InspectorState {
        IDLE,
        APPROACHING,
        WAITING_FOR_ANSWER,
        THINKING,
        LEAVING
    }

    private InspectorState inspectorState = InspectorState.IDLE;
    private PlayerEntity targetPlayer;
    private ItemStack stolenTicket = ItemStack.EMPTY;
    private boolean stolenTicketIsFake = false;
    private boolean playerClaimedRealTicketWasFake = false;
    private boolean canTrigger = true;
    private int stateTimer = 0;

    public TicketInspectorEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        World world = this.getEntityWorld();
        if (world.isClient()) {
            return;
        }

        PlayerEntity nearbyPlayer = world.getClosestPlayer(this, 10.0);

        if (nearbyPlayer == null) {
            canTrigger = true;

            if (inspectorState == InspectorState.LEAVING && this.getNavigation().isIdle()) {
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

    private void tickIdle(PlayerEntity nearbyPlayer) {
        if (!canTrigger) {
            return;
        }

        if (!hasLunchTicketInHand(nearbyPlayer)) {
            return;
        }

        targetPlayer = nearbyPlayer;
        canTrigger = false;
        inspectorState = InspectorState.APPROACHING;
        this.getNavigation().startMovingTo(targetPlayer, 1.25);
    }

    private void tickApproaching() {
        if (!isTargetValid()) {
            resetState();
            return;
        }

        this.getLookControl().lookAt(targetPlayer, 30.0F, 30.0F);
        this.getNavigation().startMovingTo(targetPlayer, 1.25);

        if (this.distanceTo(targetPlayer) <= 2.2F) {
            stolenTicket = stealLunchTicket(targetPlayer);

            if (stolenTicket.isEmpty()) {
                targetPlayer.sendMessage(
                        Text.translatable("dialogue.boundless_realms.inspector.no_ticket"),
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

        this.getLookControl().lookAt(targetPlayer, 30.0F, 30.0F);
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

        if (stateTimer <= 0 && this.getNavigation().isIdle()) {
            if (this.getEntityWorld().getClosestPlayer(this, 10.0) == null) {
                resetState();
            }
        }
    }

    private boolean isTargetValid() {
        return targetPlayer != null && targetPlayer.isAlive();
    }

    private void sendQuestion(PlayerEntity player) {
        player.sendMessage(
                Text.translatable("dialogue.boundless_realms.inspector.question"),
                false
        );

        Text yesButton = Text.translatable("dialogue.boundless_realms.inspector.answer_yes")
                .styled(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/inspector_answer yes")
                ));

        Text noButton1 = Text.translatable("dialogue.boundless_realms.inspector.answer_no")
                .styled(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/inspector_answer no")
                ));

        player.sendMessage(
                Text.empty().append(yesButton).append(Text.literal("  ")).append(noButton1),
                false
        );
    }

    public boolean isWaitingFor(PlayerEntity player) {
        return inspectorState == InspectorState.WAITING_FOR_ANSWER
                && targetPlayer != null
                && targetPlayer.getUuid().equals(player.getUuid());
    }

    public void onPlayerAnsweredNo(PlayerEntity player) {
        if (!isWaitingFor(player)) {
            return;
        }

        playerClaimedRealTicketWasFake = false;

        player.sendMessage(
                Text.translatable("dialogue.boundless_realms.inspector.thinking"),
                false
        );

        inspectorState = InspectorState.THINKING;
        stateTimer = 40;
    }

    public void onPlayerAnsweredYes(PlayerEntity player) {
        if (!isWaitingFor(player)) {
            return;
        }

        if (stolenTicketIsFake) {
            player.sendMessage(Text.translatable("dialogue.boundless_realms.inspector.honest"), false);

            if (player.getEntityWorld() instanceof ServerWorld serverWorld) {
                player.damage(serverWorld, createTooHonestDamageSource(serverWorld), Float.MAX_VALUE);
            }

            stolenTicket = ItemStack.EMPTY;
            stolenTicketIsFake = false;
            playerClaimedRealTicketWasFake = false;
            beginLeaving();
            return;
        }

        playerClaimedRealTicketWasFake = true;
        player.sendMessage(
                Text.translatable("dialogue.boundless_realms.inspector.self_doubt"),
                false
        );
        inspectorState = InspectorState.THINKING;
        stateTimer = 40;
    }

    private DamageSource createTooHonestDamageSource(ServerWorld world) {
        return new DamageSource(
                world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(TOO_HONEST_DAMAGE_TYPE.getValue()).orElseThrow(),
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
            targetPlayer.sendMessage(
                    Text.translatable("dialogue.boundless_realms.inspector.fake"),
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
            targetPlayer.sendMessage(
                    Text.translatable("dialogue.boundless_realms.inspector.warning"),
                    false
            );
        }

        stolenTicket = ItemStack.EMPTY;
        stolenTicketIsFake = false;
    }

    private void returnTicket() {
        World world = this.getEntityWorld();

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
            Vec3d from = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
            Vec3d to = new Vec3d(targetPlayer.getX(), targetPlayer.getEyeY(), targetPlayer.getZ());
            Vec3d direction = to.subtract(from).normalize().multiply(0.35);

            itemEntity.setVelocity(direction.x, 0.25, direction.z);

            targetPlayer.sendMessage(
                    Text.translatable("dialogue.boundless_realms.inspector.return"),
                    false
            );
        }

        world.spawnEntity(itemEntity);
        stolenTicket = ItemStack.EMPTY;
        stolenTicketIsFake = false;
    }

    private void beginLeaving() {
        inspectorState = InspectorState.LEAVING;
        stateTimer = 60;

        double awayX = this.getX() + 8.0;
        double awayZ = this.getZ() + 8.0;

        if (targetPlayer != null) {
            Vec3d fromPlayerToInspector = new Vec3d(
                    this.getX() - targetPlayer.getX(),
                    0.0,
                    this.getZ() - targetPlayer.getZ()
            );

            if (fromPlayerToInspector.lengthSquared() > 0.0001) {
                Vec3d direction = fromPlayerToInspector.normalize().multiply(10.0);
                awayX = this.getX() + direction.x;
                awayZ = this.getZ() + direction.z;
            }
        }

        this.getNavigation().startMovingTo(awayX, this.getY(), awayZ, 1.35);
    }

    private void resetState() {
        inspectorState = InspectorState.IDLE;
        targetPlayer = null;
        stolenTicket = ItemStack.EMPTY;
        stolenTicketIsFake = false;
        playerClaimedRealTicketWasFake = false;
        stateTimer = 0;
    }

    private boolean hasLunchTicketInHand(PlayerEntity player) {
        return player.getMainHandStack().isOf(ModItems.LUNCH_TICKET)
                || player.getOffHandStack().isOf(ModItems.LUNCH_TICKET)
                || player.getMainHandStack().isOf(ModItems.FAKE_LUNCH_TICKET)
                || player.getOffHandStack().isOf(ModItems.FAKE_LUNCH_TICKET);
    }

    private ItemStack stealLunchTicket(PlayerEntity player) {
        if (player.getMainHandStack().isOf(ModItems.LUNCH_TICKET)) {
            ItemStack stack = player.getMainHandStack().copy();
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = false;
            return stack;
        }

        if (player.getOffHandStack().isOf(ModItems.LUNCH_TICKET)) {
            ItemStack stack = player.getOffHandStack().copy();
            player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = false;
            return stack;
        }

        if (player.getMainHandStack().isOf(ModItems.FAKE_LUNCH_TICKET)) {
            ItemStack stack = player.getMainHandStack().copy();
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = true;
            return stack;
        }

        if (player.getOffHandStack().isOf(ModItems.FAKE_LUNCH_TICKET)) {
            ItemStack stack = player.getOffHandStack().copy();
            player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            stolenTicketIsFake = true;
            return stack;
        }

        stolenTicketIsFake = false;
        return ItemStack.EMPTY;
    }
}
