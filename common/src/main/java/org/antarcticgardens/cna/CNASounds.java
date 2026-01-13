package org.antarcticgardens.cna;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import static org.antarcticgardens.cna.CreateNewAge.REGISTRATE;

public class CNASounds {
    public static final RegistryEntry<SoundEvent> GEIGER_COUNTER =
            REGISTRATE.object("geiger_counter")
                    .simple(Registries.SOUND_EVENT, () ->
                            SoundEvent.createVariableRangeEvent(new ResourceLocation("create_new_age", "geiger_counter")));

    public static void load() { }
}
