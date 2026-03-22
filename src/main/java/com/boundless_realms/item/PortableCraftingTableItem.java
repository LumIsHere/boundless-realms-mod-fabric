package com.boundless_realms.item;

import com.boundless_realms.screen.PortableCraftingTableScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class PortableCraftingTableItem extends Item {
    private static final Text TITLE = Text.translatable("container.crafting");

    public PortableCraftingTableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player) -> {
                // Pass the context (world + player position) to the handler
                return new PortableCraftingTableScreenHandler(syncId, inv, ScreenHandlerContext.create(world, user.getBlockPos()));
            }, TITLE));

            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return ActionResult.SUCCESS;
    }
}