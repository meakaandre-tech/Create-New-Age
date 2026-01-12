package org.antarcticgardens.cna;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;
import org.antarcticgardens.cna.content.nuclear.RadiationPoisoningEffect;

import static org.antarcticgardens.cna.CreateNewAge.REGISTRATE;

public class CNAEffects {
    public static final RegistryEntry<RadiationPoisoningEffect> RADIATION_POISONING =
            REGISTRATE.object("radiation_poisoning")
                    .simple(Registries.MOB_EFFECT, () -> new RadiationPoisoningEffect(MobEffectCategory.HARMFUL, 0x48F542));

    public static void load() {
        REGISTRATE.addRawLang("effect.create_new_age.radiation_poisoning", "Radiation Poisoning");
    }
}
