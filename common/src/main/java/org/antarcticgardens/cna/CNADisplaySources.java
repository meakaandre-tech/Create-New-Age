package org.antarcticgardens.cna;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import org.antarcticgardens.cna.content.heat.thermometer.TemperatureDisplaySource;

import static org.antarcticgardens.cna.CreateNewAge.REGISTRATE;

public class CNADisplaySources {

    public static final RegistryEntry<DisplaySource, TemperatureDisplaySource> TEMPERATURE = CreateNewAge.REGISTRATE
            .displaySource("temperature", TemperatureDisplaySource::new).register();

    public static void load() {  }
}
