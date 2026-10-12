package org.antarcticgardens.cna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/** Sound events. The sound definitions live in assets/create_new_age/sounds.json. */
public class CNASounds {
    public static final SoundEvent GEIGER_COUNTER = Registry.register(BuiltInRegistries.SOUND_EVENT,
            CreateNewAge.asResource("geiger_counter"), SoundEvent.createVariableRangeEvent(CreateNewAge.asResource("geiger_counter")));

    public static void playOnServer(SoundEvent sound, Level level, BlockPos pos, float volume, float pitch) {
        level.playSound(null, pos, sound, SoundSource.PLAYERS, volume, pitch);
    }

    public static void register() {
    }
}
