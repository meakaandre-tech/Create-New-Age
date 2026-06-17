package org.antarcticgardens.cna;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class CNAPartialModels {
    public static final PartialModel COIL = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/carbon_brushes/coil"));

    public static final PartialModel GENERATOR_COIL = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/generator_coil/block"));

    public static final PartialModel FLOOR_THERMOMETER_INDICATOR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/thermometer/floor_indicator"));
    public static final PartialModel WALL_THERMOMETER_INDICATOR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/thermometer/wall_indicator"));
    public static final PartialModel CEILING_THERMOMETER_INDICATOR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/thermometer/ceiling_indicator"));


    public static void load() {  }
}
