package org.antarcticgardens.cna.content.energising.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.foundation.recipe.CreateSingleStackRollableRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.antarcticgardens.cna.CNARecipeTypes;

import java.util.List;

/**
 * Turns one item on a belt or depot into its results once the energiser above has put
 * {@code energy_needed} FE into it (per item). Works as a sequenced assembly step too.
 * <pre>{"type": "create_new_age:energising", "ingredient": "#c:ingots/iron", "energy_needed": 1000,
 *  "results": [{"id": "create_new_age:overcharged_iron"}]}</pre>
 */
public record EnergisingRecipe(List<ProcessingOutput> results, Ingredient ingredient, int energyNeeded)
        implements CreateSingleStackRollableRecipe {

    public static final MapCodec<EnergisingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProcessingOutput.CODEC.listOf().fieldOf("results").forGetter(EnergisingRecipe::results),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(EnergisingRecipe::ingredient),
            Codec.INT.fieldOf("energy_needed").forGetter(EnergisingRecipe::energyNeeded)
    ).apply(instance, EnergisingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergisingRecipe> STREAM_CODEC = StreamCodec.composite(
            ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), EnergisingRecipe::results,
            Ingredient.CONTENTS_STREAM_CODEC, EnergisingRecipe::ingredient,
            ByteBufCodecs.VAR_INT, EnergisingRecipe::energyNeeded,
            EnergisingRecipe::new);

    public static final RecipeSerializer<EnergisingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public int getEnergyNeeded() {
        return energyNeeded;
    }

    @Override
    public RecipeSerializer<EnergisingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<EnergisingRecipe> getType() {
        return CNARecipeTypes.ENERGISING.getType();
    }
}
