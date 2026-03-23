package com.boundless_realms;

import com.boundless_realms.block.ModBlockEntities;
import com.boundless_realms.block.ModBlocks;
import com.boundless_realms.command.InspectorAnswerCommand;
import com.boundless_realms.entity.ModEntities;
import com.boundless_realms.item.ModItemGroups;
import com.boundless_realms.item.ModItems;
import com.boundless_realms.recipe.ModRecipeTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundlessRealmsMod implements ModInitializer {
	public static final String MOD_ID = "boundless_realms";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEntities.registerModEntities();
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModRecipeTypes.registerRecipes();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				InspectorAnswerCommand.register(dispatcher)
		);
	}
}
