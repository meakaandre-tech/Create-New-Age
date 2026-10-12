package org.antarcticgardens.cna.client;

import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import org.antarcticgardens.cna.CreateNewAge;

public class CNAPartialModels {
    public static final PartialModel COIL = PartialModel.of(CreateNewAge.asResource("block/carbon_brushes/coil"));
    public static final PartialModel GENERATOR_COIL = PartialModel.of(CreateNewAge.asResource("block/generator_coil/block"));

    public static void init() {
    }
}
