package org.antarcticgardens.cna;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.antarcticgardens.cna.content.nuclear.RadiationPoisoningEffect;

public class CNAEffects {
    public static Holder<MobEffect> RADIATION_POISONING;

    public static void register() {
        RADIATION_POISONING = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, CreateNewAge.asResource("radiation_poisoning"),
                new RadiationPoisoningEffect(MobEffectCategory.HARMFUL, 0x48F542));
    }
}
