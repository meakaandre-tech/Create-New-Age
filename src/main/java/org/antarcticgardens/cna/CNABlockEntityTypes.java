package org.antarcticgardens.cna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlockEntity;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.EncasedHeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlockEntity;
import org.antarcticgardens.cna.content.heat.pump.HeatPumpBlockEntity;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlockEntity;
import org.antarcticgardens.cna.content.motor.extension.variants.AdvancedMotorExtensionVariant;
import org.antarcticgardens.cna.content.motor.extension.variants.BasicMotorExtensionVariant;
import org.antarcticgardens.cna.content.motor.variants.AdvancedMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.BasicMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.ReinforcedMotorVariant;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.rod.ReactorRodBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.vent.ReactorHeatVentBlockEntity;
import org.antarcticgardens.cna.energy.EnergyStorage;
import org.antarcticgardens.cna.registry.BlockEntityEntry;
import org.antarcticgardens.cna.registry.BlockEntry;

import java.util.HashSet;
import java.util.Set;

/**
 * Block entity types. The entries are created empty so blocks can refer to them while they register,
 * and are filled in by {@link #register()} once every block exists. Renderers and visuals are hooked up
 * on the client.
 */
public class CNABlockEntityTypes {
    @FunctionalInterface
    public interface Factory<T extends BlockEntity> {
        T create(BlockEntityType<?> type, BlockPos pos, BlockState state);
    }

    public static final BlockEntityEntry<EnergiserBlockEntity> ENERGISER = new BlockEntityEntry<>();
    public static final BlockEntityEntry<ElectricalConnectorBlockEntity> ELECTRICAL_CONNECTOR = new BlockEntityEntry<>();
    public static final BlockEntityEntry<GeneratorCoilBlockEntity> GENERATOR_COIL = new BlockEntityEntry<>();
    public static final BlockEntityEntry<CarbonBrushesBlockEntity> CARBON_BRUSHES = new BlockEntityEntry<>();
    public static final BlockEntityEntry<HeatPipeBlockEntity> HEAT_PIPE = new BlockEntityEntry<>();
    public static final BlockEntityEntry<EncasedHeatPipeBlockEntity> ENCASED_HEAT_PIPE = new BlockEntityEntry<>();
    public static final BlockEntityEntry<HeatPumpBlockEntity> HEAT_PUMP = new BlockEntityEntry<>();
    public static final BlockEntityEntry<HeaterBlockEntity> HEATER = new BlockEntityEntry<>();
    public static final BlockEntityEntry<SolarHeatingPlateBlockEntity> BASIC_SOLAR_HEATING_PLATE = new BlockEntityEntry<>();
    public static final BlockEntityEntry<SolarHeatingPlateBlockEntity> ADVANCED_SOLAR_HEATING_PLATE = new BlockEntityEntry<>();
    public static final BlockEntityEntry<ReactorRodBlockEntity> REACTOR_ROD = new BlockEntityEntry<>();
    public static final BlockEntityEntry<ReactorFuelAcceptorBlockEntity> REACTOR_FUEL_ACCEPTOR = new BlockEntityEntry<>();
    public static final BlockEntityEntry<ReactorHeatVentBlockEntity> REACTOR_HEAT_VENT = new BlockEntityEntry<>();
    public static final BlockEntityEntry<StirlingEngineBlockEntity> STIRLING_ENGINE = new BlockEntityEntry<>();
    public static final BlockEntityEntry<MotorBlockEntity> BASIC_MOTOR = new BlockEntityEntry<>();
    public static final BlockEntityEntry<MotorBlockEntity> ADVANCED_MOTOR = new BlockEntityEntry<>();
    public static final BlockEntityEntry<MotorBlockEntity> REINFORCED_MOTOR = new BlockEntityEntry<>();
    public static final BlockEntityEntry<MotorExtensionBlockEntity> BASIC_MOTOR_EXTENSION = new BlockEntityEntry<>();
    public static final BlockEntityEntry<MotorExtensionBlockEntity> ADVANCED_MOTOR_EXTENSION = new BlockEntityEntry<>();
    public static final BlockEntityEntry<StreetLightBlockEntity> STREET_LIGHT = new BlockEntityEntry<>();

    public static void register() {
        register(ENERGISER, "energiser", EnergiserBlockEntity::new,
                CNABlocks.BASIC_ENERGISER, CNABlocks.ADVANCED_ENERGISER, CNABlocks.REINFORCED_ENERGISER);
        register(ELECTRICAL_CONNECTOR, "electrical_connector", ElectricalConnectorBlockEntity::new, CNABlocks.ELECTRICAL_CONNECTOR);
        register(GENERATOR_COIL, "generator_coil", GeneratorCoilBlockEntity::new, CNABlocks.GENERATOR_COIL);
        register(CARBON_BRUSHES, "carbon_brushes", CarbonBrushesBlockEntity::new, CNABlocks.CARBON_BRUSHES);
        register(HEAT_PIPE, "heat_pipe", HeatPipeBlockEntity::new, CNABlocks.HEAT_PIPE);
        register(ENCASED_HEAT_PIPE, "encased_heat_pipe", EncasedHeatPipeBlockEntity::new,
                CNABlocks.ENCASED_HEAT_PIPE, CNABlocks.REACTOR_ENCASED_HEAT_PIPE);
        register(HEAT_PUMP, "heat_pump", HeatPumpBlockEntity::new, CNABlocks.HEAT_PUMP);
        register(HEATER, "heater", HeaterBlockEntity::new, CNABlocks.HEATER);
        register(BASIC_SOLAR_HEATING_PLATE, "basic_solar_heating_plate", SolarHeatingPlateBlockEntity::createBasic,
                CNABlocks.BASIC_SOLAR_HEATING_PLATE);
        register(ADVANCED_SOLAR_HEATING_PLATE, "advanced_solar_heating_plate", SolarHeatingPlateBlockEntity::createAdvanced,
                CNABlocks.ADVANCED_SOLAR_HEATING_PLATE);
        register(REACTOR_ROD, "reactor_rod", ReactorRodBlockEntity::new, CNABlocks.REACTOR_ROD);
        register(REACTOR_FUEL_ACCEPTOR, "reactor_fuel_acceptor", ReactorFuelAcceptorBlockEntity::new, CNABlocks.REACTOR_FUEL_ACCEPTOR);
        register(REACTOR_HEAT_VENT, "reactor_heat_vent", ReactorHeatVentBlockEntity::new, CNABlocks.REACTOR_HEAT_VENT);
        register(STIRLING_ENGINE, "stirling_engine", StirlingEngineBlockEntity::new, CNABlocks.STIRLING_ENGINE);
        register(BASIC_MOTOR, "basic_motor", (t, p, s) -> new MotorBlockEntity(t, p, s, new BasicMotorVariant()), CNABlocks.BASIC_MOTOR);
        register(ADVANCED_MOTOR, "advanced_motor", (t, p, s) -> new MotorBlockEntity(t, p, s, new AdvancedMotorVariant()), CNABlocks.ADVANCED_MOTOR);
        register(REINFORCED_MOTOR, "reinforced_motor", (t, p, s) -> new MotorBlockEntity(t, p, s, new ReinforcedMotorVariant()),
                CNABlocks.REINFORCED_MOTOR);
        register(BASIC_MOTOR_EXTENSION, "basic_motor_extension",
                (t, p, s) -> new MotorExtensionBlockEntity(t, p, s, new BasicMotorExtensionVariant()), CNABlocks.BASIC_MOTOR_EXTENSION);
        register(ADVANCED_MOTOR_EXTENSION, "advanced_motor_extension",
                (t, p, s) -> new MotorExtensionBlockEntity(t, p, s, new AdvancedMotorExtensionVariant()), CNABlocks.ADVANCED_MOTOR_EXTENSION);
        register(STREET_LIGHT, "street_light", StreetLightBlockEntity::new, CNABlocks.STREET_LIGHT);

        // energy storages the network and neighbouring blocks see, on every side
        EnergyStorage.registerForBlockEntity((be, side) -> be.getEnergyStorage(), ENERGISER.get());
        EnergyStorage.registerForBlockEntity((be, side) -> be.getEnergyStorage(), ELECTRICAL_CONNECTOR.get());
        EnergyStorage.registerForBlockEntity((be, side) -> be.getEnergyStorage(), CARBON_BRUSHES.get());
        EnergyStorage.registerForBlockEntity((be, side) -> be.getEnergyStorage(), STREET_LIGHT.get());
        EnergyStorage.registerForBlockEntity((be, side) -> be.getEnergyStorage(), BASIC_MOTOR.get());
        EnergyStorage.registerForBlockEntity((be, side) -> be.getEnergyStorage(), ADVANCED_MOTOR.get());
        EnergyStorage.registerForBlockEntity((be, side) -> be.getEnergyStorage(), REINFORCED_MOTOR.get());
    }

    private static <T extends BlockEntity> void register(BlockEntityEntry<T> entry, String name, Factory<T> factory, BlockEntry<?>... blocks) {
        Set<Block> set = new HashSet<>();
        for (BlockEntry<?> block : blocks)
            set.add(block.get());
        BlockEntityType<T> type = new BlockEntityType<>((pos, state) -> factory.create(entry.get(), pos, state), set);
        entry.set(Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateNewAge.asResource(name), type));
    }
}
