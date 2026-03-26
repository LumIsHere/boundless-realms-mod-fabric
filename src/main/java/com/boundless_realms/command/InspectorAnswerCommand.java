package com.boundless_realms.command;

import com.mojang.brigadier.CommandDispatcher;
import com.boundless_realms.entity.TicketInspectorEntity;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class InspectorAnswerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("inspector_answer")
                        .then(literal("yes")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();

                                    List<TicketInspectorEntity> inspectors =
                                            player.level().getEntitiesOfClass(
                                                    TicketInspectorEntity.class,
                                                    player.getBoundingBox().inflate(12.0),
                                                    inspector -> inspector.isWaitingFor(player)
                                            );

                                    if (!inspectors.isEmpty()) {
                                        inspectors.get(0).onPlayerAnsweredYes(player);
                                        return 1;
                                    }

                                    return 0;
                                }))
                        .then(literal("no")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();

                                    List<TicketInspectorEntity> inspectors =
                                            player.level().getEntitiesOfClass(
                                                    TicketInspectorEntity.class,
                                                    player.getBoundingBox().inflate(12.0),
                                                    inspector -> inspector.isWaitingFor(player)
                                            );

                                    if (!inspectors.isEmpty()) {
                                        inspectors.get(0).onPlayerAnsweredNo(player);
                                        return 1;
                                    }

                                    return 0;
                                }))
        );
    }
}

