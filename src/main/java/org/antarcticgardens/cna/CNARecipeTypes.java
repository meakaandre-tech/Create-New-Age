package org.antarcticgardens.cna;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

import java.util.Locale;

public enum CNARecipeTypes {
    ENERGISING(EnergisingRecipe.SERIALIZER);

    public final Identifier id;
    private final RecipeSerializer<?> serializer;
    private final RecipeType<?> type;

    CNARecipeTypes(RecipeSerializer<?> serializer) {
        id = CreateNewAge.asResource(name().toLowerCase(Locale.ROOT));
        this.serializer = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);
        this.type = Registry.register(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<Recipe<?>>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });
    }

    public Identifier getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer;
    }

    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type;
    }

    public static void register() {
    }
}
