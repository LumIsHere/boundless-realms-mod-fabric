package com.boundless_realms.recipe;

import com.boundless_realms.BoundlessRealmsMod;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {
    public static final RecipeSerializer<NetherSmeltingRecipe> NETHER_SMELTING_SERIALIZER =
            Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(BoundlessRealmsMod.MOD_ID, "nether_smelting"),
                    new AbstractCookingRecipe.Serializer<>(NetherSmeltingRecipe::new, 200));

    public static final RecipeType<NetherSmeltingRecipe> NETHER_SMELTING =
            Registry.register(Registries.RECIPE_TYPE, Identifier.of(BoundlessRealmsMod.MOD_ID, "nether_smelting"),
                    new RecipeType<NetherSmeltingRecipe>() {
                        @Override
                        public String toString() { return "nether_smelting"; }
                    });

    public static void registerRecipes() {
        BoundlessRealmsMod.LOGGER.info("Registering Custom Recipes for " + BoundlessRealmsMod.MOD_ID);
    }
}
