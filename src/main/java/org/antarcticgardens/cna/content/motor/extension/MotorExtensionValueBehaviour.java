package org.antarcticgardens.cna.content.motor.extension;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.world.entity.player.Player;

/** The extension's stress multiplier in percent, set in steps of the variant's scroll step. */
public class MotorExtensionValueBehaviour extends ServerScrollValueBehaviour {
    public int step = 1;

    public MotorExtensionValueBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int v = Math.max(1, valueSetting.value());
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(v * step);
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(0, Math.abs(value) / Math.max(1, step));
    }

    public void betweenValidated(int min, int max) {
        between(min, max);
        if (value > max)
            value = max;
        else if (value < min)
            value = min;
    }

    @Override
    public String getClipboardKey() {
        return "Stress Multiplier";
    }
}
