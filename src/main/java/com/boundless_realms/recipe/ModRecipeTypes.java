package com.boundless_realms.recipe;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {
    public static final RecipeSerializer<NetherSmeltingRecipe> NETHER_SMELTING_SERIALIZER =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "nether_smelting"),
                    new RecipeSerializer<>(
                            AbstractCookingRecipe.cookingMapCodec(NetherSmeltingRecipe::new, 200),
                            AbstractCookingRecipe.cookingStreamCodec(NetherSmeltingRecipe::new)
                    ));

    public static final RecipeType<NetherSmeltingRecipe> NETHER_SMELTING =
            Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, "nether_smelting"),
                    new RecipeType<NetherSmeltingRecipe>() {
                        @Override
                        public String toString() { return "nether_smelting"; }
                    });

    public static void registerRecipes() {
        BoundlessRealmsMod.LOGGER.info("Registering Custom Recipes for " + BoundlessRealmsMod.MOD_ID);
    }
}
