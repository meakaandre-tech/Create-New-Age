package org.antarcticgardens.cna;

import com.zurrtum.create.api.boiler.BoilerHeater;
import com.zurrtum.create.catnip.placement.PlacementHelpers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.magnet.MagnetPlacementHelper;
import org.antarcticgardens.cna.content.electricity.network.NetworkTicker;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateNewAge implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Create: New Age");
    public static final String MOD_ID = "create_new_age";

    private static int magnetPlacementHelperId;

    @Override
    public void onInitialize() {
        CNAConfig.load();

        CNADataComponents.register();
        CNABlocks.register();
        CNAItems.register();
        CNABlockEntityTypes.register();
        CNAEffects.register();
        CNASounds.register();
        CNARecipeTypes.register();
        CNACreativeTab.register();

        magnetPlacementHelperId = PlacementHelpers.register(new MagnetPlacementHelper());
        BoilerHeater.REGISTRY.register(CNABlocks.HEATER.get(),
                (level, pos, state) -> state.getValue(HeaterBlock.STRENGTH).ordinal() - 1);

        ServerTickEvents.START_LEVEL_TICK.register(NetworkTicker::tickWorld);

        // thorium ore and magnetite in the Overworld, as the NeoForge biome modifiers did
        for (String feature : new String[]{"thorium_ore", "magnetite_block"})
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES,
                    ResourceKey.create(Registries.PLACED_FEATURE, asResource(feature)));

        // optional "monkey edition" data pack that removes recipes for less realistic items
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container ->
                ResourceLoader.registerBuiltinPack(asResource("create_new_age_monkey_edition"), container,
                        Component.translatable("create_new_age.monkey_edition"), PackActivationType.NORMAL));
    }

    public static int getMagnetPlacementHelperId() {
        return magnetPlacementHelperId;
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
