package org.antarcticgardens.cna.client;

import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.AllBlockEntityRenders;
import com.zurrtum.create.client.AllItemTooltips;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.OrientedRotatingVisual;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.foundation.block.connected.SimpleCTBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.KineticAudioBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.client.ponder.CNAPonders;
import org.antarcticgardens.cna.client.render.CarbonBrushesRenderer;
import org.antarcticgardens.cna.client.render.ElectricalConnectorRenderer;
import org.antarcticgardens.cna.client.render.EnergiserRenderer;
import org.antarcticgardens.cna.client.render.HalfShaftRenderer;
import org.antarcticgardens.cna.client.render.StirlingEngineRenderer;
import org.antarcticgardens.cna.client.tooltip.CNAItemTooltips;
import org.antarcticgardens.cna.client.tooltip.CNATooltips;

/**
 * Client entry point: everything Registrate and the NeoForge client events hooked up
 * (renderers, visuals, connected textures, goggle tooltips, value boxes, item tooltips, ponder).
 */
public class CreateNewAgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CNAPartialModels.init();
        CNASpriteShifts.init();

        registerModels();
        registerRenderers();
        registerBehaviours();

        PonderIndex.addPlugin(new CNAPonders());

        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> CNAItemTooltips.append(stack, lines));
        // item descriptions (hold Shift) and kinetic stats, as Registrate attached to every item of the mod
        for (Item item : BuiltInRegistries.ITEM)
            if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(CreateNewAge.MOD_ID))
                AllItemTooltips.register(item);
    }

    private static void registerModels() {
        AllModels.register(CNABlocks.HEAT_CASING.get(), CTModel.of(new EncasedCTBehaviour(CNASpriteShifts.HEAT_CASING)));
        AllModels.register(CNABlocks.ENCASED_HEAT_PIPE.get(), CTModel.of(new EncasedCTBehaviour(CNASpriteShifts.HEAT_CASING)));
        AllModels.register(CNABlocks.REACTOR_CASING.get(), CTModel.of(new EncasedCTBehaviour(CNASpriteShifts.REACTOR_CASING)));
        AllModels.register(CNABlocks.REACTOR_ENCASED_HEAT_PIPE.get(), CTModel.of(new EncasedCTBehaviour(CNASpriteShifts.REACTOR_CASING)));
        AllModels.register(CNABlocks.REACTOR_GLASS.get(), CTModel.of(new SimpleCTBehaviour(CNASpriteShifts.REACTOR_GLASS)));
        AllModels.register(CNABlocks.REDSTONE_MAGNET.get(), CTModel.of(new SimpleCTBehaviour(CNASpriteShifts.REDSTONE_MAGNET)));
    }

    private static void registerRenderers() {
        AllBlockEntityRenders.render(CNABlockEntityTypes.ENERGISER.get(), EnergiserRenderer::new);
        AllBlockEntityRenders.render(CNABlockEntityTypes.ELECTRICAL_CONNECTOR.get(), ElectricalConnectorRenderer::new);
        AllBlockEntityRenders.render(CNABlockEntityTypes.STREET_LIGHT.get(), ElectricalConnectorRenderer::new);
        AllBlockEntityRenders.render(CNABlockEntityTypes.CARBON_BRUSHES.get(), CarbonBrushesRenderer::new);
        AllBlockEntityRenders.render(CNABlockEntityTypes.STIRLING_ENGINE.get(), StirlingEngineRenderer::new);
        AllBlockEntityRenders.visual(CNABlockEntityTypes.GENERATOR_COIL.get(), KineticBlockEntityRenderer::new,
                SingleAxisRotatingVisual.of(CNAPartialModels.GENERATOR_COIL));
        AllBlockEntityRenders.visual(CNABlockEntityTypes.BASIC_MOTOR.get(), HalfShaftRenderer::new,
                OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));
        AllBlockEntityRenders.visual(CNABlockEntityTypes.ADVANCED_MOTOR.get(), HalfShaftRenderer::new,
                OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));
        AllBlockEntityRenders.visual(CNABlockEntityTypes.REINFORCED_MOTOR.get(), HalfShaftRenderer::new,
                OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));
    }

    private static void registerBehaviours() {
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.ELECTRICAL_CONNECTOR.get(), CNATooltips.Connector::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.STREET_LIGHT.get(), CNATooltips.StreetLight::new, CNAValueBoxes::streetLight);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.BASIC_MOTOR.get(), KineticAudioBehaviour::new, CNATooltips.Motor::new, CNAValueBoxes::motor);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.ADVANCED_MOTOR.get(), KineticAudioBehaviour::new, CNATooltips.Motor::new, CNAValueBoxes::motor);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.REINFORCED_MOTOR.get(), KineticAudioBehaviour::new, CNATooltips.Motor::new, CNAValueBoxes::motor);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.BASIC_MOTOR_EXTENSION.get(), CNAValueBoxes::motorExtension);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.ADVANCED_MOTOR_EXTENSION.get(), CNAValueBoxes::motorExtension);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.CARBON_BRUSHES.get(), KineticAudioBehaviour::new, CNATooltips.CarbonBrushes::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.GENERATOR_COIL.get(), KineticAudioBehaviour::new, CNATooltips.GeneratorCoil::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.ENERGISER.get(), KineticAudioBehaviour::new, CNATooltips.Energiser::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.STIRLING_ENGINE.get(), KineticAudioBehaviour::new, CNATooltips.StirlingEngine::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.HEAT_PIPE.get(), CNATooltips.HeatPipe::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.ENCASED_HEAT_PIPE.get(), CNATooltips.HeatPipe::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.HEAT_PUMP.get(), CNATooltips.HeatPump::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.HEATER.get(), CNATooltips.Heater::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.BASIC_SOLAR_HEATING_PLATE.get(), CNATooltips.SolarPlate::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.ADVANCED_SOLAR_HEATING_PLATE.get(), CNATooltips.SolarPlate::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.REACTOR_ROD.get(), CNATooltips.ReactorRod::new);
        AllBlockEntityBehaviours.add(CNABlockEntityTypes.REACTOR_HEAT_VENT.get(), CNATooltips.ReactorHeatVent::new);
    }
}
