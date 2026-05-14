package org.antarcticgardens.cna.content.heat.thermometer;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class ThermometerScrollValueBehaviour extends ScrollValueBehaviour {

    public ThermometerScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
        between(-100, 100);
    }

    @Override
    public String formatValue() {
        return Math.abs(value) + "%";
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                CreateLang.translateDirect("scroll.create_new_age.thermometer.less_or_equal"),
                CreateLang.translateDirect("scroll.create_new_age.thermometer.greater_or_equal")
        );
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(label, 100, 10, rows, formatter);
    }

    public MutableComponent formatSettings(ValueSettings settings) {
        return CreateLang.text(settings.row() == 0 ? "≤" : "≥")
                .add(CreateLang.number(Math.max(1, Math.abs(settings.value()))))
                .add(CreateLang.text("%"))
                .component();
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        if (valueSetting.equals(getValueSettings()))
            return;
        int value = Math.max(1, valueSetting.value());
        setValue(valueSetting.row() == 0 ? -value : value);
        playFeedbackSound(this);
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(value < 0 ? 0 : 1, Math.abs(value));
    }

    @Override
    public String getClipboardKey() {
        return "Temperature threshold";
    }
}
